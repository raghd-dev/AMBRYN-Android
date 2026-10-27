# AMBRYN — public GitHub release checklist

_Last updated 2026-10-27. Current recommendation: **keep repository PRIVATE**._

This is a university portfolio demo, **not** a live store or real-payment system. A green Android CI check means the project builds/tests, **not** that artwork rights or backend security are cleared.

## Before changing the repository to Public

- [ ] **Image licenses and provenance:** Verify written authorization or appropriate licensing for every bundled JPG/PNG and any background images in screenshots. Images bearing another candle brand require special scrutiny. Replace unlicensed content with genuinely original, separately produced licensed assets; avoid AI collage crops and unreadable artificial lettering.
- [ ] **Historical Git commits:** Current-file replacement does **not** remove old images from Git history. Review complete history and choose a safe history-cleaning/repository-publication plan before exposing the code. Do not overwrite history casually; make a local backup first.
- [ ] **Screenshot sanitation:** Review real local `docs/screenshots/*-final.png` captures at full size. Use fictional accounts; remove personal names, phone numbers, email, addresses, tokens, private orders and personal messages. Do not claim generated images are captured UI.
- [ ] **Secrets review:** Search current files and history for credentials, `local.properties`, `.env` contents, service-role keys, passwords, customer data, database dumps and signing credentials. Publishable Supabase keys are not service-role keys; server-side access must still be enforced by RLS.
- [ ] **Current GitHub Actions:** Confirm a successful workflow on the commit actually being published, after the Admin changes and README/docs update.
- [ ] **Backend and demo-account checks:** Re-run safe read-only RLS inspection, confirm a known active admin role without guessing from email, and test denied writes using a genuine non-admin authenticated API token. Document any incomplete checks honestly.
- [ ] **Project/coursework/license ownership:** Resolve any third-party code/asset attribution, collaborative coursework contribution rules, and whether an open-source license is appropriate. No license is safer than accidentally licensing assets you don't own, but absence of a license does not resolve unlicensed content.
- [ ] **Public-description accuracy:** Explain simulated checkout, nontransactional cart/wishlist synchronization, limitations of workshop/support features and retained package ID `com.moonglow.candles`.

## Already supported by developer evidence

- [x] Local `testDebugUnitTest assembleDebug` passed after the Admin update (2026-10-27).
- [x] A previously observed Cart crash caused by duplicate lazy-list key `p1` was fixed; the user confirmed Cart, Checkout and matching Admin order screens.
- [x] Admin compact order references, status selection and cancellation confirmation passed four manual emulator checks.
- [x] GitHub Actions green for Cart-fix commit `02ea321` (2026-10-27). **This does not verify later commits.**
- [x] Previously reported PostgreSQL non-admin stock-write simulation denied the write and rolled back. **No real JWT API-negative test is confirmed.**

## Don't change the database as part of the documentation release

Historical SQL under `sql/` is for context and verification. Use read-only checks for current status. A previous `user_id IS NULL` admin-role record remains a known data-cleanup decision; never assign admin privileges by inferring identity from an email string.

**Release decision:** Private GitHub portfolio work may continue. The repository must not become public just because CI is green or a screenshot gallery looks complete.
