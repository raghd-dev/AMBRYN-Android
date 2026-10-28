# AMBRYN — QA and verification record

**Updated:** October 28, 2026. This report consolidates developer-supplied terminal output, screenshots, SQL query results and manual confirmations. It is **not an independent penetration test or production security certification**.

## App and build verification

| Date | Check | Result and evidence |
|---|---|---|
| Oct 26 | Baseline JVM tests and debug build | **PASS**, developer reported `BUILD SUCCESSFUL` and 17 initial JUnit tests. |
| Oct 27 | Cart duplicate-key crash | **FIXED**; Android stack trace showed duplicate LazyColumn key `p1`; merge/consolidation logic and three regression tests added. |
| Oct 27 | Cart → Checkout → Admin | **PASS**, emulator screenshots and manual confirmation, including demo order visibility. |
| Oct 27 | Admin Orders update | **PASS**, short IDs, single status menu, status update and cancellation confirmation verified manually; four formatting tests added. |
| Oct 27 | Portfolio screenshot set | **6 genuine emulator screenshots** committed; Checkout and Admin displayed-field values redacted for privacy. |
| Oct 28 | Authenticated review access | **PASS for configuration**: `authenticated` can SELECT six display columns; `anon` cannot; `user_email` and `user_id` not readable. Kotlin review GET now uses an authenticated token. |
| Oct 28 | Cart diagnostic build | **PASS**, developer pasted `testDebugUnitTest assembleDebug`, `BUILD SUCCESSFUL in 59s`. |
| Oct 28 | Cart database persistence | **OBSERVED**, developer reported two non-admin cart rows, one valid owner and zero orphaned items; log included `saveCart completed successfully`. |
| Oct 28 | GitHub Actions | **PASS reported by developer** for latest confirmed commit `b3d7487` (Cart sync warning/Retry). |

## Authorization review

- Supabase SQL output showed **RLS enabled on all ten public application tables**.
- Administrator helper `ambryn_is_admin()` was reviewed: checks `auth.uid()` against `user_roles`, uses a restricted `search_path`, and is unavailable for anonymous execution.
- Review access is column-limited to `id`, `product_id`, `user_name`, `rating`, `review_text`, and `created_at` for the `authenticated` role; email and UID are not SELECT-granted.
- **Customer A/B direct JWT isolation:** developer stated the test **passed**, without raw request/response transcripts supplied in the current exchange.
- **Unauthorized administrator writes using a regular-user JWT:** developer stated the test **passed**, without raw request/response transcripts supplied in the current exchange.
- These results support a university demo security narrative; they do not establish a comprehensive external audit or production-ready order pricing.

## Known limitations and unverified areas

- The source set previously contained **24 JUnit test cases** (17 baseline + 3 Cart regression + 4 Admin formatting); subsequent patch work did not provide a fresh independent test count.
- There is no demonstrated full instrumented UI test suite or load test.
- Checkout creates demonstration orders; server-side authoritative pricing, payments and transactional reservations are not implemented as a production store.
- Cart synchronization is not atomic across all backend operations. Warning/Retry handling was added; network-failure recovery deserves further tests.
- Original product photographs came from Pinterest; reuse/redistribution rights, including historical Git assets and screenshot appearances, were not established.
- One prior unmatched admin-role row was reported; the admin-function review did not indicate it granted privileges without a matching Auth UID.
- README display and public visibility cannot be independently checked without access to the live repository.

See `FINAL_RELEASE_GATES.md`. Never publish secrets, live tokens, database exports or real customer details in demonstration artifacts.
