-- Read-only check: run in Supabase SQL Editor.
-- All 6 rows should have exists_in_public = true before Phase 1B.
WITH expected(table_name) AS (
    VALUES ('cart_items'), ('wishlist_items'), ('orders'),
           ('chat_messages'), ('product_reviews'), ('user_roles')
)
SELECT e.table_name,
       EXISTS (
           SELECT 1
           FROM information_schema.columns c
           WHERE c.table_schema = 'public'
             AND c.table_name = e.table_name
             AND c.column_name = 'user_id'
             AND c.data_type = 'uuid'
       ) AS has_uuid_user_id
FROM expected e
ORDER BY e.table_name;
