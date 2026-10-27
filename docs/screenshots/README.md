# AMBRYN screenshots — visual review and privacy notes

Review conducted against the eight emulator image files provided on October 27, 2026.

| Repository filename | Review result | Action |
|---|---|---|
| `03-home-final.png` | File exists locally based on CMD output; exact image was **not uploaded** for inspection | Keep current local file; check final image rights and framing |
| `04-candles-final.png` | Proper emulator capture; contains visible third-party candle labels | Use privately only until photo rights are resolved |
| `05-product-final.png` | Product detail is a Compose modal; visible third-party labeling | Accurate modal screenshot; licensing unresolved |
| `06-cart-final.png` | Actual cart capture, four visible lines and a 5-item total with scroll | Suitable for private portfolio review |
| `07-checkout-final.png` | Actual demo-checkout capture included in this pack, with contact fields **covered by opaque replacement pixels** | Replace local screenshot using ZIP; better to recapture with fictitious details and no floating overlay |
| `08-admin-final.png` | Updated Admin > Orders capture from `08-admin-final(2).png`, with displayed delivery location **covered by opaque replacement pixels** | Replace outdated admin-overview screenshot using ZIP |

The submitted `08-admin-final(1).png` shows an older, empty Overview dashboard and must not be used as the final Admin Orders gallery image.

The submitted `README.md` containing only a screenshot checklist belongs at `docs/screenshots/README.md`, **not** at the repository root. The top-level `README.md` in this package is the portfolio README.

## Publication and privacy gates

- Current image provenance is unresolved; product images still show third-party candle labels. A private repository is not automatically licensed for public redistribution, and disclaimers cannot substitute for permission. Historical Git commits must also be checked before making the repo public.
- Opaque screenshot redactions conceal entered fields but do not edit the actual customer account, cart or database.
- Avoid screenshot manipulation that invents functionality or changes the product UI. A fresh emulator capture with fictional fields is preferable to a redacted capture for the final public gallery.
- Screenshots are documentation of a **demo university app**, not evidence of real payments or live commerce.
- Before staging images, visually inspect each final file, including the existing Home capture not supplied in this review.

## Verify on Windows CMD

```bat
cd /d D:\AMBRYN_Release\AMBRYN
for %f in (03-home-final.png 04-candles-final.png 05-product-final.png 06-cart-final.png 07-checkout-final.png 08-admin-final.png) do @if not exist "docs\screenshots\%f" echo MISSING: %f
start "" "docs\screenshots\07-checkout-final.png"
start "" "docs\screenshots\08-admin-final.png"
```

## Scope of this private-repository pack

This ZIP supplies the five reviewed `04`–`08` screenshots and updated Markdown documentation. Your existing local `03-home-final.png` is **not included or overwritten**; verify it exists before committing. Product photos are left untouched and may not be redistributed publicly without appropriate rights. For a cleaner final portfolio, recapture Checkout using entirely fictional entered details rather than relying on redaction.
