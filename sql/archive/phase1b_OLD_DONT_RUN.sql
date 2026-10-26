-- AMBRYN PHASE 1C SECURITY CUTOVER — CONTROLLED TEST WINDOW
-- SECURITY SENSITIVE: changes RLS and table grants. Requires a verified pre-cutover
-- backup and the updated Android build in this package installed first.
-- Executes in one transaction: on ERROR, use ROLLBACK (not COMMIT).
-- Follow PHASE1C_RUNBOOK.md. Run ONCE and check the COMMIT output.
BEGIN;

DO $$ BEGIN
  IF (SELECT count(*) FROM public.user_roles WHERE role = 'admin' AND user_id IS NOT NULL) < 1 THEN
    RAISE EXCEPTION 'STOP: No verified administrator role has been assigned';
  END IF;
END $$;

-- The only trusted administrator authority is this function. The SECURITY DEFINER
-- owner must be a trusted owner with access to public.user_roles (normally postgres).
CREATE OR REPLACE FUNCTION public.ambryn_is_admin()
RETURNS boolean LANGUAGE sql STABLE SECURITY DEFINER SET search_path = '' AS $$
  SELECT EXISTS (
    SELECT 1 FROM public.user_roles r
    WHERE r.user_id = (SELECT auth.uid()) AND r.role = 'admin'
  );
$$;
REVOKE ALL ON FUNCTION public.ambryn_is_admin() FROM PUBLIC, anon;
GRANT EXECUTE ON FUNCTION public.ambryn_is_admin() TO authenticated;

-- Drop every old permissive policy on the ten owned application tables.
DO $$ DECLARE rec RECORD;
BEGIN
  FOR rec IN
    SELECT schemaname, tablename, policyname FROM pg_policies
    WHERE schemaname = 'public' AND tablename IN (
      'admin_products','app_notifications','cart_items','chat_messages','coupons',
      'orders','product_reviews','product_stock','user_roles','wishlist_items'
    )
  LOOP
    EXECUTE format('DROP POLICY %I ON %I.%I',rec.policyname,rec.schemaname,rec.tablename);
  END LOOP;
END $$;

-- Start with least privilege; an RLS policy alone never grants SQL privileges.
REVOKE ALL ON public.admin_products, public.app_notifications,
  public.cart_items, public.chat_messages, public.coupons, public.orders,
  public.product_reviews, public.product_stock, public.user_roles,
  public.wishlist_items FROM PUBLIC, anon, authenticated;

GRANT SELECT ON public.admin_products, public.product_stock, public.coupons
  TO anon, authenticated;
-- Public review reads MUST exclude owner email and user_id. The Android app requests
-- these six public columns explicitly; do not grant table-level SELECT on reviews.
GRANT SELECT (id, product_id, user_name, rating, review_text, created_at)
  ON public.product_reviews TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.cart_items, public.wishlist_items TO authenticated;
GRANT SELECT, INSERT ON public.chat_messages, public.orders TO authenticated;
GRANT UPDATE ON public.orders TO authenticated; -- admin only enforced by RLS
GRANT SELECT ON public.user_roles, public.app_notifications TO authenticated;
GRANT INSERT ON public.app_notifications TO authenticated; -- admin only RLS
GRANT INSERT, UPDATE, DELETE ON public.admin_products, public.product_stock,
  public.coupons, public.product_reviews TO authenticated; -- admin / owner RLS
-- Grant usage for server-generated IDs only on tables used by client INSERTs.
-- Without these grants, POST may fail with permission denied for sequence.
DO $$ DECLARE tbl text; seq text;
BEGIN
  FOREACH tbl IN ARRAY ARRAY[
    'cart_items', 'wishlist_items', 'chat_messages', 'product_reviews',
    'app_notifications', 'product_stock', 'coupons', 'user_roles'
  ] LOOP
    SELECT pg_get_serial_sequence(format('public.%I', tbl), 'id') INTO seq;
    IF seq IS NOT NULL THEN
      EXECUTE format('GRANT USAGE ON SEQUENCE %s TO authenticated', seq);
    END IF;
  END LOOP;
END $$;

ALTER TABLE public.admin_products ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product_stock ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.coupons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.app_notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.wishlist_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.chat_messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product_reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_roles ENABLE ROW LEVEL SECURITY;

CREATE POLICY ambryn_products_read ON public.admin_products FOR SELECT TO anon, authenticated
  USING (active IS TRUE OR (SELECT auth.role()) = 'authenticated' AND (SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_products_admin_insert ON public.admin_products FOR INSERT TO authenticated
  WITH CHECK ((SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_products_admin_update ON public.admin_products FOR UPDATE TO authenticated
  USING ((SELECT public.ambryn_is_admin())) WITH CHECK ((SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_products_admin_delete ON public.admin_products FOR DELETE TO authenticated
  USING ((SELECT public.ambryn_is_admin()));

CREATE POLICY ambryn_stock_read ON public.product_stock FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY ambryn_stock_admin_insert ON public.product_stock FOR INSERT TO authenticated
  WITH CHECK ((SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_stock_admin_update ON public.product_stock FOR UPDATE TO authenticated
  USING ((SELECT public.ambryn_is_admin())) WITH CHECK ((SELECT public.ambryn_is_admin()));

CREATE POLICY ambryn_coupons_read ON public.coupons FOR SELECT TO anon, authenticated USING (active IS TRUE);
CREATE POLICY ambryn_coupons_admin_insert ON public.coupons FOR INSERT TO authenticated
  WITH CHECK ((SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_coupons_admin_update ON public.coupons FOR UPDATE TO authenticated
  USING ((SELECT public.ambryn_is_admin())) WITH CHECK ((SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_coupons_admin_delete ON public.coupons FOR DELETE TO authenticated
  USING ((SELECT public.ambryn_is_admin()));

CREATE POLICY ambryn_cart_read ON public.cart_items FOR SELECT TO authenticated USING (user_id = (SELECT auth.uid()));
CREATE POLICY ambryn_cart_insert ON public.cart_items FOR INSERT TO authenticated
  WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(user_email) = LOWER((SELECT auth.jwt() ->> 'email')));
CREATE POLICY ambryn_cart_update ON public.cart_items FOR UPDATE TO authenticated
  USING (user_id = (SELECT auth.uid())) WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(user_email) = LOWER((SELECT auth.jwt() ->> 'email')));
CREATE POLICY ambryn_cart_delete ON public.cart_items FOR DELETE TO authenticated USING (user_id = (SELECT auth.uid()));

CREATE POLICY ambryn_wish_read ON public.wishlist_items FOR SELECT TO authenticated USING (user_id = (SELECT auth.uid()));
CREATE POLICY ambryn_wish_insert ON public.wishlist_items FOR INSERT TO authenticated
  WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(user_email) = LOWER((SELECT auth.jwt() ->> 'email')));
CREATE POLICY ambryn_wish_update ON public.wishlist_items FOR UPDATE TO authenticated
  USING (user_id = (SELECT auth.uid())) WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(user_email) = LOWER((SELECT auth.jwt() ->> 'email')));
CREATE POLICY ambryn_wish_delete ON public.wishlist_items FOR DELETE TO authenticated USING (user_id = (SELECT auth.uid()));

CREATE POLICY ambryn_orders_read ON public.orders FOR SELECT TO authenticated
  USING (user_id = (SELECT auth.uid()) OR (SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_orders_insert ON public.orders FOR INSERT TO authenticated
  WITH CHECK (
    user_id = (SELECT auth.uid())
    AND LOWER(customer_email) = LOWER((SELECT auth.jwt() ->> 'email'))
    AND status = 'Processing' AND total >= 0
  );
CREATE POLICY ambryn_orders_admin_update ON public.orders FOR UPDATE TO authenticated
  USING ((SELECT public.ambryn_is_admin())) WITH CHECK ((SELECT public.ambryn_is_admin()));

CREATE POLICY ambryn_chat_read ON public.chat_messages FOR SELECT TO authenticated
  USING (user_id = (SELECT auth.uid()) OR (SELECT public.ambryn_is_admin()));
CREATE POLICY ambryn_chat_insert ON public.chat_messages FOR INSERT TO authenticated
  WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(email) = LOWER((SELECT auth.jwt() ->> 'email')));

CREATE POLICY ambryn_review_read ON public.product_reviews FOR SELECT TO anon, authenticated USING (true);
CREATE POLICY ambryn_review_insert ON public.product_reviews FOR INSERT TO authenticated
  WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(user_email) = LOWER((SELECT auth.jwt() ->> 'email')) AND rating BETWEEN 1 AND 5);
CREATE POLICY ambryn_review_update ON public.product_reviews FOR UPDATE TO authenticated
  USING (user_id = (SELECT auth.uid())) WITH CHECK (user_id = (SELECT auth.uid()) AND LOWER(user_email) = LOWER((SELECT auth.jwt() ->> 'email')));
CREATE POLICY ambryn_review_delete ON public.product_reviews FOR DELETE TO authenticated USING (user_id = (SELECT auth.uid()));

CREATE POLICY ambryn_roles_own_read ON public.user_roles FOR SELECT TO authenticated
  USING (user_id = (SELECT auth.uid()));
-- No client-side role insert/update/delete policies: role bootstrap is a trusted SQL action.

CREATE POLICY ambryn_notification_read ON public.app_notifications FOR SELECT TO authenticated USING (true);
CREATE POLICY ambryn_notification_admin_insert ON public.app_notifications FOR INSERT TO authenticated
  WITH CHECK ((SELECT public.ambryn_is_admin()));

COMMIT;
