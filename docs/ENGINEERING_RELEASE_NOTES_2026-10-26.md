# Engineering release notes — October 26, 2026

- UI regressions: all seven developer-reported test areas passed after the consolidated UI update.
- Refactored cart/checkout/saved-order subtotal and shipping into one pure Kotlin function (`domain/checkout/DemoCheckoutRules.kt`). Preserves the established demo rule: shipping is $5.99 for non-empty subtotals **at or below $50.00**, and free above $50.00; empty carts incur no shipping.
- Extracted form presence validation to a testable pure function. This does not claim phone format validation or backend storage of phone details.
- Added 17 local JUnit tests in `app/src/test`, including the exact free-shipping boundary and formatting behavior.
- Added private-repo friendly GitHub Actions workflow for JDK 21 / SDK 35 with placeholder Supabase config (no live backend used).
- Added two authentic developer-supplied captures to the README (splash and cropped updated Home).
- No migration, auth, role, RLS or order API change in this pass.

**Verification update (October 26):** The developer executed local Android Gradle `testDebugUnitTest assembleDebug` using `--no-daemon`, with exit code 0 and `BUILD SUCCESSFUL in 34s`. This is developer-provided evidence of a successful local test/build; GitHub Actions is unverified until the first remote run.

**Database update (October 26):** all ten public tables had RLS enabled; 3/3 cart and 3/3 wishlist rows had matching Auth users; public-schema backup was readable. One historical admin role has NULL `user_id` and remains unlinked.

**Open gates:** product-photo rights, live non-admin JWT/API denial test for stronger security assurance, full gallery capture, GitHub Actions first green run, and final staged-file/Git-history review. Keep GitHub visibility PRIVATE until public-release blockers are resolved.
