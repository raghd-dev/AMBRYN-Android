-- AMBRYN final release status checks (read only): run as database administrator.
-- Does NOT establish full proof of RLS user isolation.
SELECT current_database() as db, current_user as role;
SELECT tablename, count(*) AS policies
FROM pg_policies WHERE schemaname='public'
  AND tablename IN ('admin_products','app_notifications','cart_items',
      'chat_messages','coupons','orders','product_reviews','product_stock',
      'user_roles','wishlist_items')
GROUP BY tablename ORDER BY tablename;
SELECT has_table_privilege('anon','public.cart_items','SELECT') AS anon_cart_read,
 has_table_privilege('anon','public.orders','SELECT') AS anon_orders_read,
 has_table_privilege('anon','public.product_stock','UPDATE') AS anon_stock_update,
 has_table_privilege('authenticated','public.user_roles','INSERT') AS auth_insert_roles,
 has_column_privilege('anon','public.product_reviews','user_email','SELECT') AS anon_read_review_email,
 has_column_privilege('authenticated','public.product_reviews','user_email','SELECT') AS auth_read_review_email;
SELECT count(*) FILTER (WHERE role = 'admin' AND user_id IS NOT NULL) AS admin_count
FROM public.user_roles;
