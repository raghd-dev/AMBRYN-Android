# AMBRYN — Phase 1 private portfolio release report

**Review date:** 2026-10-27
**Target:** `raghd-dev/AMBRYN-Android`, branch `main`
**Latest confirmed before this package:** `76bf2f5` / GitHub Actions workflow #7 green
**Publication:** **PRIVATE**. Do not change visibility without independent asset permissions and full Git-history/privacy/security review.

## What is completed

- Cart duplicate-key crash fixed and manually checked with Cart, Checkout and an Admin demo order.
- Admin order screen updated, with unit tests, status dropdown and cancellation confirmation. Developer verified all four UI checks.
- Six final screenshot paths committed and displayed in README. The Checkout/Admin copies in the previous docs package were redacted.
- Latest documented Android CI workflow was green on `76bf2f5`; earlier Cart/Admin workflows also green.
- Source and original **24 JPG product/lifestyle images and five original PNG brand assets remain unchanged** by this phase-1 kit.
- Portfolio README includes the new silent 28-second slideshow, made from *real emulator screenshots*. This is **not a screen recording of actual interactions**.

## Snapshot code scan performed while preparing this kit

A pre-existing **October 26, 2026 source ZIP** was inspected for common high-risk credential signatures, including service-role values, private-key PEM delimiters, GitHub personal access tokens, Supabase `sb_secret_` values and embedded JWTs. No matching high-risk credentials were detected in the text content of that **older ZIP**. This is **not** a current-repository or complete-Git-history audit, and it does not establish there are no secrets. An additional on-device scan script in `tools/phase1/` is supplied to check the actual local Git repository and its history before a private tag.

## Blocking issues for a public release

1. **Image rights unresolved.** User has elected to keep the same product photography; that is respected. Permission to redistribute third-party photos and visible brands has not been established, so no public visibility change is performed. A README disclaimer is insufficient to grant permission.
2. **Git history not independently reviewed here.** Historical image blobs, credentials, and personal data could remain even if current files have been redacted. The local audit script catches common text patterns but is not a substitute for full review.
3. **Production security not certified.** Live non-admin JWT negative API tests, end-to-end security and transaction integrity have not been independently validated. This is a demo, not a commerce production release.
4. **Current GitHub settings need account-side action.** The finish script can set the description/topics using an installed, authenticated GitHub CLI; otherwise complete About settings manually.

## Safe completion criteria

- [x] Demo user journey documented
- [x] Six screenshots in README gallery (as reported by user)
- [x] Final CI green on `76bf2f5` (user screenshot)
- [x] Product images preserved with no replacements
- [x] Privacy-redacted Checkout/Admin files in the prior committed private docs pack
- [x] Private screenshot-based walkthrough generated
- [ ] Audit **local current Git history** using `RUN_PHASE1_FINISH.cmd` (requires user's computer)
- [ ] Push this update to private GitHub using the script
- [ ] Set/verify About description/topics on account
- [ ] Create and verify private tag and optional private draft release
- [ ] Confirm CI green for new documentation commit
- [ ] Image ownership/redistribution authorization — **not cleared**; required only before switching to Public or externally redistributing images

## GitHub About content

**Description:** Kotlin & Jetpack Compose e-commerce Android demo with Supabase Auth, PostgreSQL RLS, customer shopping, admin orders and GitHub Actions CI.

**Topics:** `android`, `kotlin`, `jetpack-compose`, `supabase`, `ecommerce`, `postgresql`, `github-actions`, `material3`, `android-app`.

**Suggested private version tag:** `v1.0.0-private-demo`. No APK or database is attached to the release.
