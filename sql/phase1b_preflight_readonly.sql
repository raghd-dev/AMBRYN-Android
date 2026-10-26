-- HISTORICAL / reference script — review carefully; not part of the final release procedure.
-- AMBRYN Phase 1B preflight: READ ONLY. This query does not modify the database.
-- The 6 user_id columns should have been added in Step 6A.
SELECT
    t.table_name,
    EXISTS (
        SELECT 1
        FROM information_schema.columns c
        WHERE c.table_schema = 'public'
          AND c.table_name = t.table_name
          AND c.column_name = 'user_id'
          AND c.data_type = 'uuid'
    ) AS has_uuid_user_id
FROM (VALUES
    ('cart_items'),
    ('wishlist_items'),
    ('orders'),
    ('chat_messages'),
    ('product_reviews'),
    ('user_roles')
) AS t(table_name)
ORDER BY t.table_name;

-- Show whether insecure anonymous policies are still present.
-- This is informational only; DO NOT drop policies until the app uses authenticated JWTs.
SELECT tablename, COUNT(*) FILTER (WHERE 'anon' = ANY(roles::text[])) AS anon_policy_count,
       COUNT(*) FILTER (WHERE 'public' = ANY(roles::text[])) AS public_policy_count
FROM pg_policies
WHERE schemaname = 'public'
GROUP BY tablename
ORDER BY tablename;
