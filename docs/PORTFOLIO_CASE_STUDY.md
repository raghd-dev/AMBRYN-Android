# AMBRYN — Android e-commerce portfolio case study

**Project type:** University coursework evolved into a software engineering portfolio demo

**Technology:** Kotlin · Jetpack Compose · Material 3 · Supabase Auth/REST · PostgreSQL RLS · Gradle · JUnit · GitHub Actions

## Project overview

AMBRYN demonstrates a mobile candle-shopping journey and an administration workflow. Customers can browse a catalog, view products, manage a wishlist/cart, submit demonstration orders and read authenticated product reviews. An Admin view supports stock and demo-order status management. It is **not a real store** and does not accept payments.

## Engineering work demonstrated

**Debugging a Compose crash:** An Android stack trace revealed `IllegalArgumentException: Key "p1" was already used` in a lazy list. Cart entries were consolidated by product ID, merging quantities and preventing duplicate row keys. Regression tests cover the rule.

**Improving synchronization feedback:** A save operation could previously fail without a visible signal. Added user-facing Cart sync warnings and Retry, plus a bounded diagnostic log message. The developer subsequently observed saved Cart records linked to a real test account.

**Protecting product-review privacy:** Supabase RLS and column-level grants now allow signed-in users to view review content without giving the client `SELECT` permission on reviewer emails or Auth UIDs. The Android reviews request was updated to attach its authenticated session.

**Refining the Admin workflow:** More legible order references, a single status control and a confirmation on cancellation reduce clutter while retaining the original backend order ID.

**Testing and CI:** A developer-run JUnit/debug build passed, and GitHub Actions was reported green at commit `b3d7487`. RLS and privileges were reviewed. Two live JWT-negative access tests (cross-customer isolation and non-admin writes) were **reported as passed by the developer**; the request logs were not independently inspected.

## What this project shows recruiters

It brings together Android UI development, state and data consistency, REST integration, user-authenticated access, testing, debugging from real stack traces, documentation and a reproducible GitHub workflow. Its scope and limitations are deliberately stated rather than presented as a commercial product.

## Deliberate limits

- Checkout is simulated; no real payment processor is integrated.
- Workshop and support features are demonstration flows, not production services.
- Cart persistence isn't an atomic transaction across all operations.
- Original Pinterest-sourced product photographs were used for coursework; redistribution rights have not been demonstrated.

**Authorship note:** Describe individual versus group contributions accurately in interviews, the CV and LinkedIn.
