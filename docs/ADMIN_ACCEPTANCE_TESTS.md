# AMBRYN — Administrator acceptance tests

**Use fictional demo orders only.** Do not change products, stock, or statuses in a real store. Create notes of original values before editing, and never share passwords/JWTs/API secret keys.

## 1. Admin authentication and inventory
1. Open the release-candidate app and log in with the existing verified **admin** account.
2. Open **Inventory**; verify there are six inventory entries. Record the original stock count of ONE test product.
3. Change the test item's stock by +1 and tap **Update Stock**. The app must not show success if no row was updated.
4. Tap **Refresh Dashboard**, navigate away/back, and confirm the edited value persists.
5. Restore the original stock count. Refresh and confirm original value is restored.

## 2. Demo order status
1. Using the two demo customer accounts, ensure at least one new test order exists.
2. As admin, open **Orders** and locate that demo order.
3. Set its status to `Packed` (or another offered status). The UI should show success ONLY when one backend record is updated.
4. Refresh, navigate away/back, and confirm the new status persists.
5. Ensure invalid status inputs don't cause silent success.

## 3. Other admin features
- Open **Support**; existing test messages, if any, should display. Reply functionality is intentionally disabled and should show a clear message.
- Test **Marketing** only with harmless, non-personal demo text. The feed may be visible to all logged-in users.
- Log out and restart; normal customer credentials must not open the admin dashboard.
- A customer's inability to open Admin UI is NOT a substitute for verifying server-side RLS denies forbidden writes. This requires a separate protected API negative test with a real non-admin authenticated session.

## 4. Record outcomes
| Check | PASS / FAIL | Notes (no private details) |
|---|---|---|
| Admin login and six inventory rows | | |
| Stock update persisted | | |
| Stock restored | | |
| Demo order status update persisted | | |
| Support tab appropriate state | | |
| Marketing demo notification | | |
| Non-admin protected write rejected by server | | |

If any write fails: capture a sanitized error message and stop; don't weaken RLS policies to make it work.

## Developer update — 2026-10-20
The developer reported Inventory management, Order management, and non-admin Admin UI access protection **PASS** on the release candidate. This does NOT replace the direct database negative test in `sql/NONADMIN_WRITE_NEGATIVE_TEST.psql`.
