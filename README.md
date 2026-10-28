
# AMBRYN — Candles & Rituals

**A university Android e-commerce demo and software engineering portfolio project.** Built with **Kotlin**, **Jetpack Compose**, **Material 3** and **Supabase**.

[![Android CI](https://github.com/raghd-dev/AMBRYN-Android/actions/workflows/android-ci.yml/badge.svg)](https://github.com/raghd-dev/AMBRYN-Android/actions)

![AMBRYN brand identity](docs/brand/ambryn_brand_preview.png)

AMBRYN grew from the university project *MoonGlow Candles* into a portfolio case study. It demonstrates how an Android client, an authenticated backend, PostgreSQL Row Level Security (RLS), and automated tests work together across a customer shopping flow and an administration dashboard.

> **Educational demo only.** AMBRYN is not a real shop or an affiliated candle brand. Orders are demonstrations; no real payment provider or live commerce is connected. The Android application ID remains `com.moonglow.candles` for compatibility with the original coursework project.

## App gallery

The following are genuine Android emulator screenshots. Contact and delivery fields in the Checkout/Admin captures have been covered for privacy.

| Home | Browse candles | Product details |
|:---:|:---:|:---:|
| ![Home screen](docs/screenshots/02-home-real-emulator-crop.png) | ![Candle catalog](docs/screenshots/04-candles-final.png) | ![Product details](docs/screenshots/05-product-final.png) |
| **Shopping cart** | **Demo checkout** | **Admin orders** |
| ![Shopping cart](docs/screenshots/06-cart-final.png) | ![Demo checkout](docs/screenshots/07-checkout-final.png) | ![Admin order management](docs/screenshots/08-admin-final.png) |

[View the screenshot walkthrough](docs/media/AMBRYN_Private_Demo_Walkthrough.mp4) — a slideshow assembled from real emulator captures, not a recording of live interaction.

## Features

| Customer-side demonstration | Administrator-side demonstration |
|---|---|
| Sign up, sign in and restore sessions | Role-controlled Admin dashboard |
| Browse/search candles, collections, kits and workshops | View and update inventory/stock |
| Product details and authenticated reviews | View demonstration orders and change their status |
| Wishlist and quantity-aware shopping cart | Short order references and cancellation confirmation |
| Demo checkout and order history | View customer messages and publish demo announcements |

Workshop presentation is not a production reservation service. Support-message replies are disabled pending more detailed privacy/thread handling. This project intentionally has no real payment processing.

## Engineering highlights

- **Fixed an actual Compose crash.** Duplicate cart rows reused the LazyColumn key `p1` and crashed the app. The cart now consolidates duplicate product IDs and merges quantities. Unit regression tests were added.
- **Made Cart sync observable.** The app now surfaces synchronization failures and offers Retry instead of silently ignoring save errors. The developer confirmed saved cart rows reaching Supabase and valid user ownership.
- **Added secure authenticated review reading.** Review requests now include the authenticated session, while Postgres column grants allow display fields and block direct access to `user_email` and `user_id`.
- **Polished Admin order management.** Compact order references, a status selector and confirmation before cancellation improve usability without changing underlying order IDs.
- **Applied backend access controls.** RLS is enabled on ten application tables. Owner-scoped Cart/Wishlist/Orders policies and an admin-role function were inspected. Direct Customer A/B JWT isolation and unauthorized admin write checks were **reported as passed by the developer**; request traces are not included here.
- **Reproducible development workflow.** A GitHub Actions workflow runs JVM unit tests and builds a debug APK using placeholder public settings, without committing private Supabase configuration.

## Technology stack

| Area | Technology |
|---|---|
| Android | Kotlin, Jetpack Compose, Material 3, Navigation Compose |
| App architecture | UI screens, repositories, business rules and remote services |
| Networking | OkHttp, Supabase REST API |
| Identity and data | Supabase Auth, JWT, PostgreSQL and Row Level Security |
| Local session handling | Android Keystore-backed storage, DataStore |
| Build and tests | Gradle Kotlin DSL, JUnit 4, GitHub Actions |

## Build and run

1. Open the source project in Android Studio with a compatible Android SDK and JDK 21.
2. Copy `local.properties.example` to `local.properties` and configure your **own** Supabase URL and publishable/anon client key. Do **not** use a service-role key in an Android application.
3. Provision a compatible backend and RLS policies for your own demonstration data. The historical SQL files are reference material; **do not blindly rerun migrations against an existing database**.
4. Build and run the debug app:

```bat
cd /d D:\AMBRYN_Release\AMBRYN
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
gradlew.bat --no-daemon --console=plain testDebugUnitTest assembleDebug
```

The path above is an example of the original developer environment. Replace it with your own project folder. Install/run the app from Android Studio or an emulator. Checkout creates demonstration orders only.

## Testing and verification

| Evidence | Result |
|---|---|
| Local JVM tests and debug APK build | **Passed**, most recently reported after the Cart-sync diagnostic change on Oct 28, 2026 |
| Cart duplicate-key crash and regression tests | **Passed**, crash resolved in emulator |
| Cart, Checkout and Admin flow | **Passed**, manual emulator checks and screenshots |
| Authenticated review database grants | **Verified**, six display fields readable; email and Auth UID excluded |
| Supabase Cart persistence | **Verified**, two saved rows belonged to one valid non-admin account |
| Customer A/B JWT isolation and non-admin write denial | **Passed — developer-reported**; raw negative-request evidence not independently reviewed in this documentation update |
| GitHub Actions | **Passed**, latest verified workflow on commit `824dd6d` (`main`), October 28, 2026 |
| Independent production-grade security audit | **Not performed**; outside scope of a university demo |

See the [QA record](docs/TEST_RESULTS.md), [portfolio case study](docs/PORTFOLIO_CASE_STUDY.md) and [release considerations](docs/FINAL_RELEASE_GATES.md).

## Project layout

```text
app/src/main/java/com/moonglow/candles/
  core/           # App navigation, theming
  data/           # Models, repository, remote services
  domain/         # Business rules
  presentation/   # Customer and Admin Compose UI
app/src/test/     # JVM and regression tests
.github/workflows/android-ci.yml
sql/              # Historical database scripts
docs/             # Screenshots, portfolio notes, security/QA references
```

## Limitations and attribution

This repository is presented as **coursework and an engineering demonstration**, not as a completed commercial service. No real payments, production inventory reservation or full transactional order processing are implemented. Cart synchronization uses multiple backend operations rather than a single atomic server-side transaction; network failures require handling.

**Third-party media notice:** Some candle/product photographs were downloaded from Pinterest and depict other brands. Their copyright and redistribution permissions are **not verified**. Pinterest downloads and a university-demo label do not by themselves confer a license to republish those files or their historical Git versions. AMBRYN does not claim affiliation with the photographed brands. Any public distribution of those assets requires appropriate permission or licensed alternatives. This note does **not** grant such permission.

The codebase may contain coursework contributions; authorship and license terms should be represented accurately before assigning an open-source license to the entire repository. The GitHub visibility setting is controlled by the repository owner and is **not asserted here**.
