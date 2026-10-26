# AMBRYN: Phase 1B Android security test build

**Not a public GitHub release yet.** Source-only implementation; build and backend integration were not verified in this environment.

## Confirmed database facts (provided by project owner, Oct 2026)

- `auth.users` 4 users; `admin_products` 6; `product_stock` 6; `coupons` 2.
- Six user-owned tables currently have **zero rows** (cart, wishlist, orders, chat, reviews, roles).
- Six `user_id uuid` columns exist.
- Legacy permissive `anon`/`public` policies still exist; 42 policies reported.
- `user_roles` empty: **no administrator has been bootstrapped**.

## Code modifications

1. New `AuthenticatedSupabaseHttp.kt`: centrally attaches `apikey` and the user's actual `Bearer <JWT>` to authenticated REST calls; reads current identity from encrypted Keystore-backed session.
2. `cart_items` and `wishlist_items`: checks email matches session, writes `user_id`, filters by it, checks every HTTP response, and serializes local save calls.
3. `orders`: persists `user_id`; customer history filtered by user ID; admin history asks for all but relies on server RLS.
4. `product_reviews`: JWT for writes, ownership ID inserted; public read only.
5. `user_roles`: role resolution uses authenticated user's ID rather than a requestable email.
6. `chat_messages`: customer messages limited to own ID, admin read separated. Admin replies are *intentionally disabled* until the DB supports proper private recipient threading.
7. Admin writes now attach JWT; still **require RLS** to reject ordinary users. No client-side authorization is sufficient.
8. Checkout: uses a demo payment label, never displays an active real-card entry form, and waits for Supabase success before claiming the order was placed. Unused legacy card UI helper remains in source but is not called.
9. Login no longer trims passwords.

## Why this isn't production secure yet

- **Do not deploy this build to real users** while the legacy Supabase anonymous/public policies remain.
- The SQL in `sql/phase1b_apply_rls_after_android_test.sql` is a *review-only staging draft*. It drops legacy policies and will alter app behavior. DO NOT RUN it until Android tests and a restorable backup are complete.
- Orders and totals are still supplied by the client: simulated payments only. A real service would calculate prices and stock atomically on a trusted server.
- Cart and wishlist sync currently delete then reinsert; a network interruption can leave partial data. Replace with a transactional server RPC for production.
- `product_reviews` public REST selects still expose stored email columns; create a limited public view and stop exposing raw user emails before production.
- `app_notifications` is a global feed and `is_read` is not user-specific.
- Admin reply flow needs private thread / recipient modeling before re-enablement.

## Test instructions, in order

1. Open the ZIP in Android Studio. Copy your existing working `local.properties` to the root (NEVER commit it).
2. Gradle Sync → Build → Rebuild. Capture any errors verbatim.
3. Run the emulator and test login, logout, restart, customer role lookup, loading products, and reading reviews.
4. Review and confirm a fully restorable backup and the exact database to be migrated. Once prepared, switch RLS using the SQL **in a controlled test window**. The first existing customer login should remain functional.
5. After RLS cutover, test with two different accounts, create cart items/wishlist/review/demo order, close/reopen and verify persistence.
6. Confirm one account cannot read or change the other one's rows (including API testing with normal JWTs). Verify anonymous client cannot insert roles or update stock/orders. Admin will not function until manually bootstrapped using the SQL template.
7. Run Supabase Security Advisor again and document remaining findings.
8. Only then finalize GitHub README and screenshots.

## Caution: admin bootstrapping

`sql/phase1b_admin_bootstrap_template.sql` requires selecting a real confirmed Supabase Auth account. Do not choose an account by guesswork or include credentials/IDs in GitHub. It deliberately grants the first admin role only when executed with trusted DB owner authority.

## Unable to test here

Gradle wrapper cannot download `gradle-8.13-bin.zip` from the network in this environment, so Android compilation and on-device behavior are **unverified**. The SQL has **not been executed** against your Supabase database.
