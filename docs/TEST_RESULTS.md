# AMBRYN — Verified vs. pending tests

Developer-reported manual and database results through October 26, 2026. These are developer-reported emulator/database tests, not an independently audited production certification.

| Area | Test | Status | Evidence / note |
|---|---|---|---|
| Authentication | Login and logout | PASS | User confirmed |
| Authentication | Close/reopen session restore | PASS | User confirmed |
| Customer | Product browsing | PASS | User confirmed |
| Customer | Cart | PASS | User confirmed |
| Customer | Wishlist | PASS | User confirmed |
| Customer | Demo checkout | PASS | User confirmed |
| Customer | Order history | PASS | User confirmed |
| Security | Two distinct customer accounts do not see each other's data | PASS | User confirmed |
| Security | Public review columns don't grant read of user_email | PASS | SQL privileges returned false/false, true for review_text |
| Admin | Admin login/dashboard navigation | PASS | User confirmed |
| Admin | Six inventory records shown | PASS | User confirmed |
| Database | RLS cutover committed | PASS | User provided psql COMMIT |
| Database | Legacy anonymous write policies count = 0 | PASS | User supplied verify results |
| Admin | Update stock and reload | PASS (reported) | Developer reports latest release-candidate inventory management passed |
| Admin | Restore stock and reload | PASS (reported) | Included in developer-reported inventory management test |
| Admin | Update demo order status and reload | PASS (reported) | Developer reports latest release-candidate order management passed |
| Security | Non-admin account cannot enter admin UI | PASS (reported) | UI check only, not backend authorization verification |
| Security | Non-admin user's protected stock write refused by PostgreSQL RLS | PASS (reported) | PostgreSQL role/JWT-claim simulation: `authenticated`, `auth.uid()` set, `ambryn_is_admin()=false`, 6 visible stock rows, `UPDATE 0`, `ROLLBACK` (Oct 20, 2026). Not a live REST JWT test. |
| Release | Builds/runs as tested by developer | PASS (reported) | Earlier candidates were tested; exact final source now confirmed to compile; fresh functional retest after copy was not separately recorded |
| Release | Build exact final package | PASS (developer screenshot) | Windows CMD: BUILD SUCCESSFUL in 1m 5s; Java 21.0.8 / Gradle 8.13; not independently reproduced |
| Release | Full local automated tests and debug build | PASS (developer CMD log) | `--no-daemon testDebugUnitTest assembleDebug`; exit code 0; BUILD SUCCESSFUL in 34s on October 26, 2026 |
| Release | Review image licensing and scrub sensitive screenshots | PARTIAL | Two safe developer-supplied screenshots added; third-party image rights remain pending |

Do not change a PASS into a broader claim than the actual test supports.


## UI/UX patch verification (October 26, 2026)

The consolidated Kotlin UI patch compiled on Windows with JDK 21 (`BUILD SUCCESSFUL in 2m 2s`, developer-provided CMD output). The developer subsequently launched AMBRYN, signed in, and reported all seven areas **PASS**: Home, Candles/product details, Cart, Wishlist, Checkout, Profile/order history, Admin Dashboard. The Android Studio screenshot shows the application running. The Android Studio project path shown differed from the CMD folder, so these are developer-reported UI results, not an independent byte-for-byte installed-APK audit.

## Automated test / CI additions (October 26, 2026)

17 new JVM JUnit tests were added for pricing, shipping boundary, form presence checks, and formatting. A GitHub Actions workflow was added to build the APK and run tests on JDK 21. **LOCAL PASS**: developer ran `gradlew.bat --no-daemon --console=plain --no-parallel --max-workers=2 testDebugUnitTest assembleDebug` with exit code 0 and BUILD SUCCESSFUL in 34s on October 26, 2026, after a persistent Gradle daemon error. **REMOTE PENDING**: GitHub Actions has not yet run on GitHub.

## Latest database integrity audit (October 26, 2026)

- All 10 expected public application tables had RLS enabled.
- Read-only grant checks returned false for the sampled anonymous sensitive operations and authenticated role self-insertion; review-email SELECT permissions were false for anon/authenticated. Grants alone do not demonstrate all RLS behaviors.
- Cart: 3 rows, 0 NULL owners and 0 Auth UUID/email mismatches. Wishlist: 3 rows, 0 NULL owners and 0 mismatches.
- One currently linked admin role; one historical `user_roles` admin row has NULL `user_id` and remains unchanged. It does not satisfy the inspected UUID-based `ambryn_is_admin()` predicate.
- A public-schema `pg_dump` custom archive existed (52,089 bytes) and `pg_restore --list` exited 0. No restoration performed; Auth and Storage are outside its scope.
- Simulated non-admin stock write was denied in PostgreSQL (`UPDATE 0`, rolled back). A live REST denial check using a real customer's access token is **still pending**.
