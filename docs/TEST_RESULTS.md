# AMBRYN — QA and verification record

_Updated 2026-10-27. Results below are based on developer-supplied logs, screenshots, and manual confirmations; this is not an independent security certification._

## Verified by developer

| Date | Area | Finding | Evidence / limit |
|---|---|---|---|
| 2026-10-26 | Original JVM unit tests and build | **PASS** | `testDebugUnitTest assembleDebug`, `BUILD SUCCESSFUL in 34s` reported from Windows. Initial test suite contained 17 tests. |
| 2026-10-27 | Cart crash diagnosis | **REPRODUCED AND FIXED** | Crash log: `IllegalArgumentException: Key "p1" was already used` in Jetpack Compose LazyList. |
| 2026-10-27 | Cart regression tests | **PASS in developer build** | 3 new cases: merge duplicate products, ignore nonpositive quantities, handle empty cart. Cart-fix code pushed as commit `02ea321`. |
| 2026-10-27 | Cart → Checkout → Admin | **PASS, manual** | Screenshots show cart opened, quantities/totals carried to Checkout, and matching demo order in Admin. |
| 2026-10-27 | GitHub Actions | **PASS for `02ea321`** | Screenshot of green Android CI run (2m 31s). Later commits must be checked separately. |
| 2026-10-27 | Admin Orders UI | **PASS, manual** | Four checks reported: short reference, status selector, status update, cancel confirmation. |
| 2026-10-27 | Latest local Gradle | **PASS** | Developer pasted `BUILD SUCCESSFUL in 1m 5s`, `testDebugUnitTest` and `assembleDebug` completed. There was one nonblocking deprecated icon warning (`ReceiptLong`). |
| 2026-10-27 | Admin formatting tests | **PASS in developer build** | 4 tests: branded ID, bare UUID, blank ID fallback and custom ID. |
| 2026-10-27 | Source-set test inventory | **24 JUnit tests** | 17 original + 3 Cart + 4 Admin, counted from known source patches. |

## Previously reported backend and journey checks

- Login/logout, session restoration, product browsing, wishlist, order history, simulated checkout, stock adjustments, and admin navigation were reported passing.
- Two customer accounts were reported isolated in the UI; this is not equivalent to a comprehensive customer JWT API security test.
- PostgreSQL RLS was reported enabled for ten public app tables; selected grants and review-email permissions were inspected.
- Simulated non-admin stock-write check returned `ambryn_is_admin() = false` and `UPDATE 0` inside a rolled-back SQL transaction. A live authenticated REST/JWT write-denial test remains unconfirmed.
- Cart/wishlist ownership checks previously found three rows of each type with valid Auth ownership. A historical admin-role row with `user_id IS NULL` was left unchanged; the inspected role check used signed-in user IDs.
- A public-schema dump passed `pg_restore --list` validation but full restore and Auth/Storage coverage were not tested.

## Not yet established

- Successful GitHub Actions for the **Admin UI commit** and this documentation commit, until those runs complete.
- A full end-to-end automated Android UI test suite; screenshots and user checks are manual.
- Real non-admin/customer JWT negative API tests and independent security review.
- Rights to redistribute the original photographs and all past image blobs in Git history.
- Screenshot privacy checks for the six new local captures; filenames alone are not verification.
- A fully transactional, production-capable checkout/inventory system.

See `FINAL_RELEASE_GATES.md` before making the repository public. Do not rerun historical database migrations during portfolio documentation work.
