# AMBRYN — portfolio publication considerations

**Updated:** October 28, 2026

This repository showcases a **university Android coursework application**. It is not a commercial candle brand and does not support real payment processing. Publication of code and publication of third-party photos are separate decisions.

## Checks supported by reported evidence

- [x] Latest developer-reported Android CI was green for `b3d7487` on `main`.
- [x] Local `testDebugUnitTest assembleDebug` passed after the Cart sync diagnostic update (59s).
- [x] Cart duplicate-key crash fixed; customer demo checkout and Admin Orders views verified on emulator.
- [x] Authenticated review GET and column-granted read permissions verified; reviewer email/UID are not readable by API roles.
- [x] RLS is enabled for the ten public application tables.
- [x] The current admin authorization helper uses Auth UID and does not allow anonymous execution.
- [x] Direct Customer A/B JWT isolation and non-admin JWT write-denial tests **reported passed by developer** (raw traces were not independently inspected).
- [x] Six README screenshot paths existed in the developer's current project and committed gallery.
- [x] Basic local credential patterns and tracked-file checks reported no findings; this is not a forensic audit of every historical file or artifact.

## Open considerations, even for a portfolio

- [ ] **Photo redistribution permissions:** Product images were downloaded from Pinterest. No documentation of the photographers' licenses or permission to redistribute those images was supplied. A demonstration disclaimer does not provide those rights; historic Git commits and screenshots may contain them.
- [ ] **Screenshot privacy:** Confirm no personally identifying information remains visible in screenshots or historical assets, including embedded image metadata and CI artifacts.
- [ ] **GitHub visibility:** Verify the actual repository setting in GitHub. A `git push` does not automatically change a Private repository to Public.
- [ ] **Source attribution:** Identify any third-party code, assets or group coursework contributions and document them accurately.
- [ ] **Source-code licensing:** Decide whether to grant an open-source code license; never implicitly license third-party photographs you do not own.

## Scope of security claims

The documented policy inspection and reported API tests are meaningful engineering checks but are not an external security certification. The project is **demo-only**; no payment gateway, live revenue, transactional stock reservation or production-data guarantees are claimed.

**No app code, product photographs or Supabase configuration needs to change to update these documents.** These notes describe publication risks rather than automatically approving public distribution of third-party assets.
