# AMBRYN brand changes

## Scope
- Rebranded visible labels to **AMBRYN**.
- Replaced legacy logo artwork and launcher icon.
- Updated startup/splash, onboarding and authentication brand visuals.
- Aligned UI accent colors with plum and dusty rose while retaining the existing layouts and features.
- Left package names, app ID, class names and Supabase endpoints unchanged to reduce regression risk.
- Preserved credential loading with `SUPABASE_PUBLISHABLE_KEY`.

## Brand
Name: AMBRYN
Descriptor: Candles & Rituals
Tagline: Make room for a moment.
Palette: plum #402D38, dusty rose #CBA7A5, parchment #F7F1EB, clay #B77364, charcoal #2E2A2D.

## Rebrand limitations
- This is a UI identity update, not a fix for Supabase authorization, checkout, or admin security.
- Do not treat sample support/guest email addresses as live contact details.
- Initial repository uses `com.moonglow.candles` Kotlin package and applicationId for compatibility.
- Test actual app startup, logo cropping, onboarding, login, and end-to-end flows on an emulator.
- The name has not been legally cleared for commerce.
- Not build-verified in this environment.

## Credentials
Copy `local.properties.example` to `local.properties` and provide `SUPABASE_URL` and `SUPABASE_PUBLISHABLE_KEY`; never commit `local.properties`.
