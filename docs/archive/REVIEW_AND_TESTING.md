# AMBRYN — Review & Testing Checklist

## Confirmed from source inspection
- Android Gradle Kotlin DSL project, Kotlin/Compose and multiple screens / Supabase REST service classes.
- Gradle wrapper files are present.
- No service-role key string detected by a basic text search. The repository-prep copy removes the project-specific Supabase URL/key from committed Kotlin source and reads them from local configuration.
- No automated test source directory was identified in the submitted archive.

## Not verified
- Android Gradle sync/compilation or APK install.
- End-to-end authentication, catalog, cart, checkout, orders, chat, roles, admin, or data persistence.
- Supabase RLS policy correctness; cross-user access restrictions.

## Action plan (priority order)
1. Verify Supabase policies and REST requests using two test accounts. Review all filters that rely only on a user-supplied email. Do not publish as secure until authorization is enforced by the server.
2. Run the aligned Gradle 8.13 / Android Gradle Plugin 8.13.2 setup locally and record the successful build output.
3. Run `assembleDebug` and install on a test device/emulator; capture build output.
4. Test registration, login, logout, catalog, add-to-cart, wishlist, checkout, order history, reviews, support, and admin permissions.
5. Add automated unit tests and/or Compose UI tests for critical flows.
6. Replace README status claims only when actually verified; add screenshots and demonstration video.
7. Confirm author's contribution and permission to publish assets and coursework.

## Known architecture limitation
- Supabase Auth login is validated, but the returned user access token is not currently persisted and reused for database REST requests. Most REST calls authenticate with the publishable client key and some filter by email. Treat this as a coursework/demo implementation until user JWT propagation and RLS policies are verified.

## Security notes
Client publishable keys are expected to be visible in a mobile app. Never embed a Supabase **service-role/secret** key; the database must restrict access even if the client request is tampered with. Replace an existing Supabase project reference with your own if needed.
