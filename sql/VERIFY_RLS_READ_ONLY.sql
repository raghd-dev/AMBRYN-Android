-- Read-only checks; safe for review without altering data.
-- 03. READ ONLY: post-cutover policy/privilege checks.
-- These checks don't prove user isolation. Test separate real accounts in the app.
SELECT current_database() AS database_name, current_user AS connected_as;
SELECT 'anon_can_insert_role' AS check_name, has_table_privilege('anon','public.user_roles','INSERT') AS result
UNION ALL SELECT 'anon_can_read_cart', has_table_privilege('anon','public.cart_items','SELECT')
UNION ALL SELECT 'anon_can_read_orders', has_table_privilege('anon','public.orders','SELECT')
UNION ALL SELECT 'anon_can_insert_orders', has_table_privilege('anon','public.orders','INSERT')
UNION ALL SELECT 'anon_can_update_stock', has_table_privilege('anon','public.product_stock','UPDATE')
UNION ALL SELECT 'auth_can_insert_role', has_table_privilege('authenticated','public.user_roles','INSERT')
UNION ALL SELECT 'auth_can_update_stock_grant', has_table_privilege('authenticated','public.product_stock','UPDATE');
-- All should be FALSE EXCEPT the final auth_can_update_stock_grant,
-- which can be TRUE because RLS denies non-admin users.
SELECT 'legacy_anon_write_policies' AS check_name, COUNT(*) AS count
FROM pg_policies WHERE schemaname='public' AND (cmd IN ('ALL','INSERT','UPDATE','DELETE'))
AND ('anon' = ANY(roles) OR 'public' = ANY(roles));
SELECT tablename, cmd, roles, policyname FROM pg_policies WHERE schemaname='public'
AND tablename IN ('admin_products','app_notifications','cart_items','chat_messages','coupons',
                  'orders','product_reviews','product_stock','user_roles','wishlist_items')
ORDER BY tablename,cmd,policyname;
SELECT 'admin_roles' AS check_name, COUNT(*) AS actual FROM public.user_roles
WHERE role='admin' AND user_id IS NOT NULL;
