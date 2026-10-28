-- Run separately after the migration. READ ONLY.
SELECT
  has_table_privilege('authenticated', 'public.product_reviews', 'SELECT') AS authenticated_full_table_select,
  has_column_privilege('authenticated', 'public.product_reviews', 'product_id', 'SELECT') AS authenticated_can_read_product_id,
  has_column_privilege('authenticated', 'public.product_reviews', 'review_text', 'SELECT') AS authenticated_can_read_review_text,
  has_column_privilege('authenticated', 'public.product_reviews', 'user_email', 'SELECT') AS authenticated_can_read_user_email,
  has_column_privilege('authenticated', 'public.product_reviews', 'user_id', 'SELECT') AS authenticated_can_read_user_id,
  has_column_privilege('anon', 'public.product_reviews', 'review_text', 'SELECT') AS anon_can_read_review_text;

SELECT policyname, roles, cmd, qual
FROM pg_policies
WHERE schemaname = 'public' AND tablename = 'product_reviews'
ORDER BY policyname;
