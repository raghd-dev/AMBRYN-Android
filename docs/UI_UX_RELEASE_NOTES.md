# AMBRYN — Consolidated UI/UX update

Prepared October 20, 2026. **Subsequently developer-build verified and manually tested.** Windows Gradle reported `BUILD SUCCESSFUL in 2m 2s` using Java 21; on October 26 the developer reported PASS for all seven UI/UX regression areas. The automated-tests/CI additions from October 26 still require a fresh build.

## Included source changes

- **Cart:** opaque background, tighter cards, accessible add/remove quantity actions, fixed bottom subtotal/checkout panel; updates the product dialog from live cart state.
- **Checkout:** fixed bottom call-to-action, clear loading and validation feedback, explicit disabled state for an empty cart, scrollable content above the action. This does not change order persistence or simulated-payment policy.
- **Admin:** two responsive KPI cards per row, full-width summary directly below metrics, horizontally scrollable management tabs, responsive refresh control, loading and empty states, read-only support-message presentation instead of a knowingly disabled reply action. Admin API behavior and RLS are unchanged.
- **Login:** compact, equal-width role chips to prevent Support wrapping, and narrower card padding. Selected role remains cosmetic; server authorization is unchanged.
- **Catalog / wishlist:** product cards stretch to adaptive grid-cell widths; product titles can use two lines.
- **Reviews:** zero real reviews now displays 'No reviews yet' instead of an unsupported high star rating. Review recommendation percentage is computed from data instead of always claiming 100%. The review-submit dialog no longer remains in a permanent 'Submitting' state.
- **Currency:** shared `formatPrice` helper formats USD values with two decimal places across browse, cart, wishlist, checkout, orders, kits, workshops, search, and admin screens.
- **Notifications:** suppresses stale startup snackbar messages that were obscuring the Home hero; the notification inbox remains available.

## One-pass visual regression checks after build

1. Login on Pixel 9 Pro and a smaller emulator: all 3 role chips fully readable, authentication works.
2. Home: image, buttons, notification bell, bottom nav; no stale toast covering product section.
3. Candles and Wishlist: adaptive cards don't overflow; product titles wrap; price amounts always include two decimals; no stars with 0 reviews.
4. Product dialog: images, note details, cart/wishlist buttons, review submission and dismissal.
5. Cart: transparent/ghosted content no longer appears after a normal navigation settle; quantity updates, removal, and subtotal sync.
6. Checkout: delivery inputs scroll, bottom 'Place Demo Order' remains reachable, empty cart disables action, successful simulated order persists.
7. Admin: KPI cards fit screen, tabs scroll horizontally, Overview appears without excessive whitespace, inventory and order status updates still persist.
8. Review smaller text scaling, long labels, keyboard appearance and back navigation.

## Exclusions and release gates

- Bundled third-party candle photography has **NOT** been replaced. The existing candle photos and promotional hero imagery must be licensed or replaced before a public GitHub release. Do not superimpose AMBRYN logos over other brands' photographs.
- This UI-only patch did not introduce payments, workshop backends or verified live REST/JWT denial testing. The subsequent October 26 engineering pass adds JVM JUnit tests and GitHub Actions but does not change those other limitations.
- No access-control / RLS policy / Supabase migration changes. No secret keys added.
- The original package `com.moonglow.candles` ID is deliberately untouched to preserve existing installation / data compatibility.
- The phone number field in the demo checkout is validated in the UI but is **not persisted** by the current `placeOrder` API. This should not be presented as complete shipping-data handling.

## Build in the user's environment

From the extracted `AMBRYN` project folder on Windows 10:

```bat
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
gradlew.bat clean assembleDebug
```

The consolidated UI/UX build did succeed in the developer's environment. New JUnit/CI additions still require `gradlew.bat clean testDebugUnitTest assembleDebug` for verification.
