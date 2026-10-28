# AMBRYN — Phase 1 Portfolio Release Report

**Original review date:** October 27, 2026
**Last updated:** October 28, 2026
**Repository:** `raghd-dev/AMBRYN-Android`
**Branch:** `main`
**Latest synchronized commit:** `67fa251`
**Project type:** University Software Engineering Portfolio Project

## 1. Project Overview

AMBRYN — Candles & Rituals is an Android e-commerce demonstration application developed as a university software engineering portfolio project.

The application was originally developed as MoonGlow Candles and later refined into AMBRYN, with an updated visual identity, improved customer experience, enhanced administrator functionality, database security controls, and automated testing.

AMBRYN is not a real commercial store. It does not process actual payments, operate a live candle business, or provide production-grade commerce infrastructure.

The project demonstrates practical software engineering skills using Kotlin, Jetpack Compose, Material 3, Supabase Authentication, PostgreSQL, Row Level Security (RLS), and GitHub Actions.

## 2. Completed Development Work

### Android Application

- Customer registration, login, and session restoration.
- Candle product browsing and search.
- Product details and authenticated product reviews.
- Wishlist and shopping Cart functionality.
- Cart quantity management and synchronization with Supabase.
- Demonstration Checkout and order history.
- Gift kit and workshop presentation.
- Administrator dashboard.
- Inventory and stock management interfaces.
- Demonstration order management and status updates.
- Customer support and notification interfaces.

### Bug Fixes and Improvements

- Fixed a Jetpack Compose Cart crash caused by duplicate LazyColumn item keys.
- Added duplicate-item consolidation and quantity merging.
- Added regression tests for Cart behavior.
- Improved Cart synchronization error messages and Retry functionality.
- Fixed authenticated product-review reading.
- Restricted product-review database access to permitted display fields.
- Improved Admin order cards and shortened displayed order references.
- Added confirmation before order cancellation.
- Improved the presentation of customer and administrator screenshots.

## 3. Database Security and Access Control

The Supabase PostgreSQL backend uses Row Level Security policies to restrict access to application data.

The security review covered:

- RLS enabled on ten application tables.
- User-specific access policies for customer data.
- Cart and Wishlist ownership restrictions.
- Order access controls.
- Administrator-role authorization.
- Authenticated product-review access.
- Column-level restrictions preventing direct review access to sensitive user identifiers.

The developer also reported successful direct JWT tests for:

- Customer A/B data isolation.
- Denial of unauthorized administrator write operations.

These results are recorded as developer-confirmed. Raw API request traces were not independently reviewed for this documentation update.

**Security scope:** The project demonstrates application security practices suitable for a university portfolio. It has not undergone an independent production-grade penetration test or complete commercial security certification.

## 4. Testing and Quality Assurance

Testing included local Android builds, unit tests, emulator verification, database checks, and automated GitHub Actions workflows.

### Confirmed Testing Activities

- Android debug build completed successfully.
- JVM unit tests passed.
- Cart duplicate-key crash regression tests passed.
- Cart, Checkout, and Admin flows were manually verified in the emulator.
- Cart persistence to Supabase was confirmed.
- Authenticated review permissions were verified.
- Customer authorization checks were reported as passed by the developer.
- GitHub Actions successfully completed multiple Android CI workflows.

### GitHub CI and Commit History

Important verified milestones include:

| Commit | Description |
|---|---|
| `02ea321` | Fixed duplicate Cart keys and crash |
| `85db419` | Improved administrator order management |
| `76bf2f5` | Finalized portfolio screenshots and QA |
| `036e907` | Prepared the private demo release |
| `1b7da93` | Fixed authenticated review reading |
| `b3d7487` | Added Cart synchronization errors and Retry |
| `a7a0140` | Finalized university portfolio documentation |
| `824dd6d` | Removed obsolete development archives |
| `4212902` | Corrected README Home screenshot and updated CI status |
| `67fa251` | Removed additional historical development files |

Green GitHub Actions results were confirmed for earlier milestones, including the documentation and cleanup work. The latest CI result for commit `67fa251` should be checked separately if needed.

The presence of successful CI results confirms that the configured automated workflow passed. It does not independently certify every application behavior or security property.

## 5. Portfolio Documentation

The repository contains a completed portfolio README presenting:

- Project introduction and educational purpose.
- Main application features.
- Kotlin and Android technology stack.
- Customer and administrator functionality.
- Real Android emulator screenshots.
- Engineering challenges and implemented solutions.
- Build and setup instructions.
- Testing and verification results.
- Security notes and limitations.

Supporting documentation includes:

- `docs/PORTFOLIO_CASE_STUDY.md`
- `docs/TEST_RESULTS.md`
- `docs/FINAL_RELEASE_GATES.md`
- `docs/PORTFOLIO_STATUS_2026-10-28.md`
- `docs/SECURITY_TEST_REPORT_2026-10-20.md`
- `docs/GITHUB_RELEASE_STEPS.md`

The README gallery was corrected to reference the existing customer Home screenshot rather than the Admin dashboard image.

## 6. Screenshots and Branding

The repository preserves AMBRYN's original application design and product photography.

Portfolio materials include:

- AMBRYN brand identity preview.
- Application logo and icon resources.
- Customer browsing screenshots.
- Product detail screenshots.
- Shopping Cart screenshot.
- Privacy-redacted Checkout screenshot.
- Administrator order-management screenshot.
- Screenshot-based demonstration walkthrough.

The demonstration walkthrough is assembled from real emulator captures. It is not represented as a recording of live application interaction.

Original product photographs and application branding were not replaced during the final documentation and repository cleanup work.

## 7. Repository Cleanup

The repository was cleaned to make its structure easier to navigate for recruiters and software engineering reviewers.

### Removed Historical Files

The cleanup included removing:

- Obsolete Phase 1 development notes.
- Archived handoff documentation.
- Historical SQL drafts.
- An outdated SHA-256 manifest.
- Redundant branding-change notes.
- Old release-candidate notes.
- Selected Phase 1 audit and finishing scripts.

Repository cleanup commits include `824dd6d` and `67fa251`.

Some Phase 1 PowerShell scripts were removed. Consequently, any remaining shortcuts that refer to those deleted scripts should not be used and must be removed or corrected.

The latest cleanup was synchronized to the local project through a successful fast-forward Git pull.

### Files Preserved

The cleanup was intended to retain all essential project components:

- Android source code.
- Application resources and original images.
- Gradle build configuration.
- Android unit tests.
- GitHub Actions workflow.
- README.
- Final portfolio screenshots.
- Current security and QA documentation.
- Relevant database reference scripts.

Removing files from the current branch does not erase their contents from historical Git commits.

## 8. Credential and Git History Review

An October 26, 2026 project snapshot was previously inspected for common high-risk credential patterns, including:

- Service-role credentials.
- Private-key delimiters.
- GitHub personal access token patterns.
- Supabase secret-key patterns.
- Embedded JWT patterns.

No matching high-risk credentials were reported in the inspected snapshot.

Additional local Git-history text scanning was performed during Phase 1, but this was a limited pattern-based review rather than a complete independent audit of every historical file and binary asset.

The old local Phase 1 scanning utilities were subsequently removed during repository cleanup. They are not required to build or operate the Android application.

**Important:** The absence of reported matches does not guarantee that every historical commit is free from confidential information.

## 9. Known Limitations

AMBRYN is intentionally an educational demonstration and has the following limitations:

1. No real payment gateway or financial transactions.
2. No production inventory reservation system.
3. No complete commercial order-processing infrastructure.
4. Cart synchronization is not implemented as a single atomic backend transaction.
5. Workshop features are demonstrations rather than a production booking system.
6. Support-message replies are not fully implemented.
7. No independent production-grade security certification.
8. Third-party image redistribution permissions remain unverified.

These limitations are disclosed to ensure that the project is represented accurately to reviewers and recruiters.

## 10. Third-Party Media and Publication

Some candle and lifestyle photographs were downloaded from Pinterest and may depict third-party products or brands.

Their copyright ownership and redistribution permissions have not been verified.

The educational purpose of AMBRYN and the repository's README disclaimer do not automatically grant permission to redistribute those photographs.

The application does not claim affiliation with the photographed brands.

**Repository visibility:** The repository was last observed as private. Its current visibility should be checked directly in GitHub before describing it as public.

Public visibility would also expose historical Git versions containing the original images.

## 11. GitHub Portfolio Presentation

**Repository:** `https://github.com/raghd-dev/AMBRYN-Android`

**Suggested GitHub About description:**

Kotlin & Jetpack Compose e-commerce Android demo with Supabase Auth, PostgreSQL RLS, customer shopping, admin orders and GitHub Actions CI.

**Suggested GitHub topics:**

`android`, `kotlin`, `jetpack-compose`, `supabase`, `ecommerce`, `postgresql`, `github-actions`, `material3`, `android-app`

**Demo version tag:** `v1.0.0-private-demo`

The version tag was prepared during the private-demo release workflow. Its current remote availability and any associated release assets should be verified separately if needed.

## 12. Final Portfolio Checklist

- [x] Android demo application developed.
- [x] Customer and administrator interfaces implemented.
- [x] Cart crash fixed.
- [x] Cart synchronization improvements implemented.
- [x] Product-review access corrected.
- [x] Database RLS policies reviewed.
- [x] Customer isolation and unauthorized Admin write tests reported as passed.
- [x] Android unit tests and debug builds passed.
- [x] Multiple GitHub Actions workflows passed.
- [x] README and portfolio case study updated.
- [x] Final emulator screenshots added.
- [x] Checkout and Admin screenshot privacy redactions applied.
- [x] Obsolete development archives removed.
- [x] Repository synchronized through commit `67fa251`.
- [ ] Confirm the latest GitHub Actions result after repository cleanup.
- [ ] Confirm no remaining scripts reference deleted Phase 1 utilities.
- [ ] Verify repository visibility and image redistribution permissions before public distribution.

## 13. Final Assessment

AMBRYN is complete as a **university Android software engineering portfolio demonstration**.

The project provides practical evidence of:

- Android application development.
- Modern declarative UI development.
- Authentication and backend integration.
- Database access-control design.
- Bug investigation and regression testing.
- Administrator functionality.
- Automated builds and continuous integration.
- Technical documentation and GitHub repository maintenance.

The project is suitable for demonstrating software engineering knowledge in academic reviews and internship applications.

**Phase 1 status:** Application development, portfolio documentation, major testing activities, and repository cleanup are complete. The remaining publication considerations concern repository visibility, unresolved third-party media rights, and any final verification of retained scripts or the latest CI run.