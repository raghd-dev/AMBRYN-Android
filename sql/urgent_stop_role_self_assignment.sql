-- HISTORICAL / reference script — review carefully; not part of the final release procedure.
-- OPTIONAL emergency hardening for the CURRENT database while finishing Android cutover.
-- As of the last read-only check, user_roles has ZERO rows.
-- This does not affect reading the customer role. Run only if you're certain you have
-- the original Supabase database backup and the intended app never creates roles itself.
BEGIN;
DROP POLICY IF EXISTS "Allow role insert" ON public.user_roles;
REVOKE INSERT, UPDATE, DELETE ON public.user_roles FROM anon, authenticated;
COMMIT;
