> **HISTORICAL DOCUMENT — cutover was already applied. Do not repeat these instructions.**

# AMBRYN Phase 1C — controlled RLS cutover

**IMPORTANT: No database changes have been applied by producing this ZIP.** The changes in this package must be tested before using real customer data. You have confirmed admin role setup and a six-item inventory view in the Android emulator. The previous database checks showed 4 Auth accounts, 6 products, 6 stock rows, 2 coupons, and 0 rows in the six user-owned tables.

## What's new

- Keeps your tested Phase 1B Android app and existing visual identity.
- `SupabaseReviewService.kt`: requests only safe public review columns instead of `select=*` so customers' emails are not publicly returned.
- `sql/phase1b_apply_rls_after_android_test.sql`: controlled cutover migration; deletes permissive legacy policies and replaces them with ownership/admin checks. Adds sequence usage grants needed for inserts, and only grants column-limited public SELECT for reviews.
- `sql/phase1c_verify_after_cutover.sql`: read-only review of policy and table grants.

## BEFORE applying migration

1. Confirm `ambryn_public.backup` is safe and its archive table-of-contents can be listed with `pg_restore -l`. That proves archive readability, NOT a restore. For stronger protection, test restore on a separate database (not production) before cutover.
2. Save a CURRENT public-schema backup, because administrator and inventory policies have changed since the original backup:

    `"D:\Program Files\PostgreSQL\18\bin\pg_dump.exe" -h "YOUR_SESSION_POOLER_HOST" -p 5432 -U "YOUR_SESSION_POOLER_USER" -d postgres -W -Fc -n public -f "D:\AMBRYN_Private_Backup\ambryn_precutover.backup"`

   Never upload this private backup to GitHub. This does not include Supabase Auth users or Storage.
3. Open this Android version, copy your own `local.properties`, Rebuild, run, log in as admin and confirm inventory. The only code difference is the limited public review selection.
4. Choose a time when no one else is actively using the test application. Do not use production customer data.

## Execute in psql (ONLY when prepared)

From a `postgres=>` prompt, `\i 'D:/YOUR_EXTRACT_PATH/AMBRYN/sql/phase1b_apply_rls_after_android_test.sql'`. Ensure you use the REAL extracted folder. A successful transaction ends with `COMMIT`. If an error occurs and you see `postgres=!>`, run `ROLLBACK;` and stop. DO NOT rerun blindly.

## Immediately test after migration

- **Customer A**: login, add cart item, wishlist item, a review, place a DEMO checkout (no real payment), reopen and check persistence. If error, collect the visible error/Logcat without JWTs or secrets.
- **Customer B**: login with separate account, verify A's private items/orders/chats are not accessible, and create B's own items.
- **Admin**: login and check six inventory rows; change stock of a test product and restore the original count; see demo orders, change order status. Confirm normal customer cannot do these writes.
- **Public reviews**: see reviews, but do not expose `user_email` or `user_id` via public REST results.
- Run the read-only SQL `sql/phase1c_verify_after_cutover.sql` and Supabase Security Advisor. Public read policies may intentionally remain for nonprivate catalog/reviews/stock.

## Known limitations

- Order totals, inventory decrements and coupons are not atomically verified server-side. Checkout is DEMO only. Never use with real payment data.
- Cart/wishlist sync deletes+reinserts, and a network drop can lose items; production needs transactional RPC.
- In-app notifications are a GLOBAL feed; do not post personal information. Individual read-state is not implemented.
- Admin support replies remain disabled until thread-specific recipient modeling is completed.
- Backing up `public` does not back up `auth`, storage objects, or all project-level settings.
- Read-only SQL inspection and a working dashboard alone DO NOT establish cross-user isolation. Test with two actual users.
