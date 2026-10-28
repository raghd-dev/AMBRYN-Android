-- AMBRYN: allow signed-in users to read only display-safe review columns.
-- Run in Supabase Dashboard > SQL Editor as the project owner.
-- Existing INSERT / UPDATE / DELETE policies and grants are not changed.
-- Note: display name and review text are intentionally visible to signed-in users.

BEGIN;

-- Remove full-row SELECT permissions, including possible broad role grants.
REVOKE SELECT ON TABLE public.product_reviews FROM anon, authenticated, PUBLIC;

-- In case earlier migrations granted these columns individually.
REVOKE SELECT (user_email, user_id) ON TABLE public.product_reviews
FROM anon, authenticated, PUBLIC;

-- Authenticated users can request only these columns (never SELECT *).
GRANT SELECT (id, product_id, user_name, rating, review_text, created_at)
ON TABLE public.product_reviews TO authenticated;

-- Match the read policy to the signed-in-only UI requirement.
DROP POLICY IF EXISTS ambryn_review_read ON public.product_reviews;
CREATE POLICY ambryn_review_read
  ON public.product_reviews
  FOR SELECT
  TO authenticated
  USING (true);

COMMIT;
