# AMBRYN — Android e-commerce case study

**Project:** AMBRYN — Candles & Rituals
**Context:** University Android app, evolved into a software engineering portfolio project
**Platform:** Android (Kotlin / Jetpack Compose)
**Backend:** Supabase Auth, REST and PostgreSQL RLS
**Status:** Educational demonstration; private GitHub repository pending asset licensing review

## The product

AMBRYN demonstrates a candle-shopping experience across browsing, product details, cart, wishlist, checkout and order history. An Admin dashboard supports stock overview and demo-order management. Checkout is simulated and does not charge a payment method.

## What was engineered and refined

**Shopping flow:** Unified purchase summaries and demonstration order display across the customer and admin experiences.

**Cart reliability:** Diagnosed an actual emulator crash through the Android runtime stack trace. Duplicate cart entries caused Jetpack Compose to reuse the key `p1` in a lazy list. Consolidation by product ID removed duplicate row keys while preserving combined quantities; three unit tests cover the regression.

**Administrator usability:** Refactored order cards to show concise references and an accessible full ID, replaced multiple stacked status buttons with a selector and required confirmation for cancellation. Four focused formatting tests protect reference formatting.

**CI and test feedback:** The Windows JVM suite and debug APK build passed after the Admin changes. GitHub Actions was confirmed green for the Cart-fix commit. The working codebase contains 24 JUnit cases across checkout/pricing, cart regression and Admin references; CI for newer commits must be confirmed separately.

**Data security boundaries:** Supabase authentication and PostgreSQL RLS are used for customer and administrator flows. The repo contains read-only database validation scripts and documented caveats, including the distinction between a simulated database permission denial and a real customer-JWT API test.

## Tools and technologies

Kotlin · Jetpack Compose · Material 3 · Supabase Auth/REST · PostgreSQL/RLS · JUnit 4 · GitHub Actions · Android Studio · Gradle

## Lessons demonstrated

- Why UI-level data assumptions, such as unique list keys, need defensive business rules and regression tests.
- Why an interface polish is stronger when it preserves backend IDs and adds confirmation to destructive changes.
- Why reproducible builds, documented security limits and licensing decisions are part of software engineering quality—not just visual presentation.

## Further work

Resolve third-party photo rights and Git-history exposure; refresh all portfolio images with legally usable assets; review sanitized emulator captures; verify real JWT-negative API behavior; and consider atomic cart/order persistence for production-like robustness.

> **Attribution note:** This case study describes the project work and observed engineering changes; individual versus group coursework contributions should be described accurately before posting on LinkedIn or in a CV.
