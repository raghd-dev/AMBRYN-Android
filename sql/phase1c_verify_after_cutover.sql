-- AMBRYN post-cutover inspection (read-only)
-- These results inspect policies/privileges, but cannot prove user isolation.
SELECT table_name, count(*) AS rows FROM (
 SELECT 'cart_items' AS table_name FROM public.cart_items
 UNION ALL SELECT 'orders' FROM public.orders
 UNION ALL SELECT 'wishlist_items' FROM public.wishlist_items
) q GROUP BY table_name;

SELECT table_name, grantee, privilege_type FROM information_schema.role_table_grants
WHERE table_schema = 'public' AND table_name IN ('orders','cart_items','wishlist_items','user_roles','product_stock','product_reviews')
  AND grantee IN ('anon','authenticated')
ORDER BY table_name,grantee,privilege_type;

SELECT tablename,policyname,cmd,roles,qual,with_check
FROM pg_policies WHERE schemaname='public'
AND tablename IN ('admin_products','app_notifications','cart_items','chat_messages','coupons',
 'orders','product_reviews','product_stock','user_roles','wishlist_items')
ORDER BY tablename,policyname;

SELECT has_table_privilege('anon','public.orders','SELECT') AS anon_orders_read,
 has_table_privilege('anon','public.cart_items','INSERT') AS anon_cart_write,
 has_table_privilege('anon','public.user_roles','INSERT') AS anon_role_write,
 has_table_privilege('authenticated','public.user_roles','INSERT') AS auth_role_write,
 has_table_privilege('authenticated','public.product_stock','UPDATE') AS auth_stock_update_granted;
-- The last column may be TRUE: RLS must then deny non-admin UPDATEs.
