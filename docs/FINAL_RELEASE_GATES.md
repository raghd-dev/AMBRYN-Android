# AMBRYN — final GitHub release gates (updated October 26, 2026)

**Purpose:** educational/portfolio publication only. NO real payment processing, no production store, no production data.

## Evidence received from developer
- Customer login, product browsing, cart, wishlist, simulated checkout, order history: PASS (6/6).
- Two customer accounts could not see each other's private saved data: PASS (manual app test).
- Review email permissions: `anon=false`, `authenticated=false`, public review text=true.
- RLS cutover applied with COMMIT; post-cutover verify returned zero old anonymous write policies; one administrator initially confirmed.
- Admin login and inventory display: PASS.
- Release-candidate admin stock management (including restore) and demo order management: PASS (developer reported).
- Non-admin user cannot access Admin UI: PASS (developer reported).

## Must-do gates before making the repository public
1. **Server-side non-admin write-denial**: **PASS (developer-reported)**. On October 20, 2026, a transaction with `SET LOCAL ROLE authenticated` and a temporary JWT subject returned `is_admin=f`, six visible stock records, `UPDATE 0`, and `ROLLBACK`. This is a PostgreSQL role/JWT-claim simulation, not a live REST/API token test.
2. **Database audit**: **PASS for stated checks (developer-provided output, October 26)**. All 10 app tables had RLS; cart/wishlist owners matched Auth (3/3 each). There is one correctly linked admin role plus one unlinked historical admin-role row (`user_id IS NULL`), intentionally not granted privilege or deleted. Real customer-token/API denial and independent security review are still recommended before production claims.
3. **Compile and test exact engineering package: PASS (developer CMD log)**. On Windows 10, `gradlew.bat --no-daemon --console=plain --no-parallel --max-workers=2 testDebugUnitTest assembleDebug` returned exit code 0 and `BUILD SUCCESSFUL in 34s` on October 26, 2026 using Android Studio JDK 21; Gradle 8.13. A previous persistent daemon-dispatch issue did not recur with `--no-daemon`.
4. **Photograph permissions**: check redistribution rights for bundled JPG images; if unclear, publish the repository PRIVATE until images are replaced/licensed. Also check fonts, icons, and other asset attribution.
5. **Safe screenshots**: ensure all screenshots contain fictional accounts only and do not expose email, password, access token, device notifications or real order details.
6. **No secrets staged**: run `git status`, `git diff --cached --name-only`, and inspect before first push. Never commit `local.properties`, `.env`, database backups, or private customer data.
7. **License decision**: a public GitHub repo without a license is publicly viewable but does not automatically grant reuse rights. Choose a license only after confirming ownership of code/assets and any team/coursework restrictions.

## Known limitations to disclose
- Checkout is **simulated**; no card charges or real payment provider.
- Catalog has hard-coded demo products alongside database-backed stock; checkout is not an atomic payment/inventory transaction.
- Cart/wishlist delete-and-reinsert sync is not atomic.
- Workshop bookings are not a full backend booking service.
- In-app notifications are not authenticated device push.
- Chat admin replies disabled pending privacy controls.
- 17 JUnit tests ran in the successful local Gradle test task; no independent penetration test, live customer-JWT authorization denial, or verified GitHub Actions result yet.
- `com.moonglow.candles` remains as Kotlin namespace/application ID despite AMBRYN branding.

**Release decision:** With the local build/tests and read-only database checks passed, remaining public-release blockers are third-party asset rights, safe/complete screenshots, Git history and staged-file review, first GitHub Actions run, and honest disclosure of untested production security cases. Publishing as a **private portfolio work-in-progress** can proceed earlier. Do not market it as a production-ready commerce platform.


## UI/UX patch verification (October 26, 2026)

Developer confirmed consolidated UI source built successfully on Windows (`BUILD SUCCESSFUL in 2m 2s`), launched, logged in, and passed all seven UI/UX areas: Home, Candles & product details, Cart, Wishlist, Checkout, Profile/order history and Admin Dashboard. One screenshot showed an app Home screen running in Android Studio. The test/CI-enhanced revision subsequently passed a local Gradle JVM test and debug build on October 26, 2026.

## Engineering improvements and outstanding verification

- Added 17 JVM unit tests; the exact engineering package passed the local `testDebugUnitTest assembleDebug` tasks (exit code 0, October 26).
- Added GitHub Actions on push/pull request; inspect the Actions result once pushed.
- Consolidated demo pricing between UI and stored orders at the >$50 free-shipping boundary. Recheck an order near $50 if desired.
- Two developer-supplied screenshots added: splash and updated Home emulator pane cropped to exclude editor credentials; the rest of the new-build gallery remains open.
- Public release is still blocked by unresolved third-party product photograph licensing and the other gates above. An unlinked historical admin record remains. Live customer-JWT denial and penetration testing remain pending for stronger security claims.
