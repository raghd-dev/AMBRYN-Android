-- HISTORICAL / reference script — review carefully; not part of the final release procedure.
-- Read-only verification after migration. Does not expose customer data.
SELECT tablename, cmd, roles, count(*) AS policy_count
FROM pg_policies
WHERE schemaname='public'
GROUP BY tablename, cmd, roles ORDER BY tablename,cmd;
SELECT 'auth.users' AS source, COUNT(*) AS records FROM auth.users
UNION ALL SELECT 'admin_products',COUNT(*) FROM public.admin_products
UNION ALL SELECT 'product_stock',COUNT(*) FROM public.product_stock
UNION ALL SELECT 'coupons',COUNT(*) FROM public.coupons;
SELECT table_name, column_name, data_type FROM information_schema.columns
WHERE table_schema='public' AND column_name='user_id'
ORDER BY table_name;
