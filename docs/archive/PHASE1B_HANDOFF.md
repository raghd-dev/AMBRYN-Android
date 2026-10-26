# AMBRYN Phase 1B — preflight, not the security cutover

## Status

**Development/test build only.** This ZIP fixes Phase 1A startup navigation, prepares the Phase 1B database checks, but does **not** yet secure RLS or convert the legacy data services to JWT-authenticated access. Do not use as a live store with real customer data.

## Fixed from Phase 1A testing

- The previous code started NavHost at `onboarding` unconditionally, regardless of a valid restored Supabase login.
- `AppViewModel` now exposes `StartupAuthState` (`Checking`, `SignedIn`, `SignedOut`). UI waits for actual session validation before it chooses the initial screen.
- After a verified saved session, it opens `Home`; without one, it uses existing onboarding/login routing.
- Logging out clears the navigation history so Android Back cannot revisit the old signed-in screen.

## Run locally

1. Extract as **a new folder**, not over your working app.
2. Copy your own `local.properties` with the matching project URL and `SUPABASE_PUBLISHABLE_KEY`. Never commit secrets or local files.
3. Gradle Sync → Rebuild → Run on emulator.
4. Login normally, **force-close the app without signing out**, reopen with internet access. It should show a brief loading indicator and then go straight to Home.
5. Logout, press Back; authenticated pages should no longer be accessible from the old navigation stack.
6. If the app crashes, capture the first relevant error in Android Studio Logcat; if it returns to login, confirm the emulator retains app data and has network access (don't clear app data).

**Build not verified** in this environment (no Android SDK/Gradle distribution). The user needs to run Android Studio and report results.

## Phase 1B — correct cutover order

1. Verify full backup and `user_id uuid` columns exist; run `sql/phase1b_preflight_readonly.sql` and share the first 6 rows.
2. Backfill user IDs in verified existing records only; **preserve eight unmatched legacy placeholder orders** without assigning them to arbitrary users.
3. Implement one shared authenticated REST request layer using a **user access token** from `validAccessToken()`; ensure all customer writes use the verified `auth.uid`/`user_id`, not arbitrary email inputs. Never use publishable keys as Bearer tokens.
4. Design and stage RLS for customer ownership, verified admins, product reviews, notification types, coupon reads, chat privacy, and inventory with correct grants.
5. Coordinate Android code + database cutover; existing RLS is restricted to `anon` and moving clients to `authenticated` before policies exist will break services.
6. Verify two customers cannot see or edit each other's carts/orders, admin actions require a verified role, and legacy demo orders are invisible to customers; rerun Supabase Security Advisor.

**Do not apply new RLS policies from this ZIP:** no RLS-changing migration is included. Also, the existing eight demo orders have `customer@moonglow.com` and no matching Auth user, so leave them unassigned until reviewed.
