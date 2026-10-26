# AMBRYN Security Phase 1A — Authentication session groundwork

**Status: source changes prepared; NOT yet Gradle/emulator verified.**

## Implemented
- Supabase login now parses its `access_token`, `refresh_token`, `user.id`, `user.email`, and expiry instead of throwing away the response.
- Removed the invalid publishable-key-as-Bearer headers from non-auth REST services; the existing demo still uses anonymous RLS until phase 1B.
- Tokens are encrypted at rest using an Android Keystore AES-GCM key; no tokens or secrets are logged.
- Startup restores a profile **only if** a real Supabase session can be validated (refresh when expired, then `/auth/v1/user`).
- Logout clears both authentication tokens and local user profile.
- An incorrect role choice clears the just-created local token session.
- New signup no longer automatically logs in; user must sign in (and verify email if Supabase requires it).
- Android Auto Backup disabled to avoid copying encrypted token data without its non-exportable Keystore key.
- `validAccessToken()` prepared for subsequent RLS migration.

## NOT done / not safe yet
- **Existing data REST services still make anonymous requests using only the `apikey` header**; they do NOT use the new JWT. We removed the invalid `Authorization: Bearer sb_publishable_...` headers. The user JWT must not be attached until matching `authenticated` RLS policies are created and verified. The legacy `anon` policies remain wide open.
- Existing `orders`, `cart_items`, `wishlist_items`, `chat_messages`, `product_reviews`, `user_roles` writes do not yet use new `user_id` columns.
- Admin role checks still rely on old email-based `user_roles` access; server-side privileges are NOT secure.
- Legacy checkout still has simulated payments; do not submit real card details.
- No full build/test completed in this environment.
- Local `SessionManager` saves a name and email separately from tokens; it is never treated as proof of authentication.
- If session validation fails due to network downtime, user appears logged out until a later login/restart; refresh token is preserved. Offline UX improvement is future work.
- Verified session restoration does not automatically skip onboarding/login in the current UI; this needs UX follow-up.

## Before you run
1. Back up your Supabase database (CSV exports and policies alone are not a complete database backup).
2. **Confirm** Step 6A (nullable `user_id` columns) succeeded; do not assume it succeeded from this ZIP.
3. Keep existing database policies unchanged for now (do not publish the backend as secure).
4. Copy your actual working `local.properties` from your computer (not included).
5. Build and test signup/login, sign out, and restart after login.

## Next phase (1B)
- Switch REST requests to authenticated `Bearer access_token` and `user_id` filters in a controlled migration.
- Define and apply table-specific RLS policies; compare results for customer A, customer B, and admin.
- Fix unsafe admin rights, order history filtering, checkout status, chat visibility, review creation, notifications and coupons.
- Run Supabase Security Advisor and integration testing.
