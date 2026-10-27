# AMBRYN portfolio review — October 27, 2026

**Status:** Good engineering proof, but **not cleared for a public release**.

**Strengths:** Consistent cream/burgundy UI; a clearly labeled non-payment checkout; functional cart and Admin Orders views; demonstrated successful CI for the cart fix; visible improvement in the Admin Orders card controls.

**Corrections applied in this pack:**

1. Checkout screenshot is still a genuine emulator capture, but entered name, phone, and location values are covered by opaque, solid-color redaction blocks. This changes only the image, not the app data.
2. Updated Admin > Orders replaces the outdated empty Admin Overview image. The displayed delivery-location value is fully covered; the short order reference and status selector are left intact.
3. Portfolio README and screenshot checklist explicitly identify the privacy edits and the unresolved third-party image-license issue.

**Other findings:**

- Main candle catalog, detail modal, and home banner show third-party-branded photography. Even if screenshots are polished, the repository's original photo assets and their Git history must be licensed or replaced before going Public.
- The provided `05-product-final.png` is a full-height product-detail dialog over the catalog. It is a valid feature screenshot, but it is not a separate product page.
- The provided `06-cart-final.png` shows four rows while the count is five, consistent with more content being scrollable; there is no evidence here of a total-calculation bug.
- The original Checkout screenshot included a floating circular menu overlay and omitted a clean full-order-summary view. A fresh capture with fictional data is recommended for final professional showcase.
- The Home file `03-home-final.png` was not uploaded as a standalone asset in the supplied image set. It exists on the user's machine according to their CMD listing, but this review did not visually validate it.
- The old Admin screenshot is an Overview screen with zero activity; use the corrected Admin Orders capture from this package.

**What the patch DOES NOT do:** Modify Kotlin, database records, user accounts, image asset licenses, or history. It does not prove the most recent Admin GitHub CI passed. Keep the repository Private pending rights, privacy and security review.
