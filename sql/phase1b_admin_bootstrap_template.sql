-- HISTORICAL / reference script — review carefully; not part of the final release procedure.
-- Bootstrap ONLY after you have personally verified the intended administrator
-- account in Supabase Authentication. Run as trusted database owner.
-- Replace YOUR_VERIFIED_ADMIN_EMAIL locally; do not share it in public GitHub issues.
-- Never execute this template with an unverified email.
BEGIN;
DO $$ BEGIN
 IF (SELECT COUNT(*) FROM auth.users WHERE LOWER(email)=LOWER('YOUR_VERIFIED_ADMIN_EMAIL')) <> 1 THEN
   RAISE EXCEPTION 'Admin bootstrap requires one verified Auth user';
 END IF;
END $$;
INSERT INTO public.user_roles (email, role, user_id)
SELECT email, 'admin', id FROM auth.users
WHERE LOWER(email)=LOWER('YOUR_VERIFIED_ADMIN_EMAIL')
AND email_confirmed_at IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM public.user_roles WHERE user_id=auth.users.id);
COMMIT;
