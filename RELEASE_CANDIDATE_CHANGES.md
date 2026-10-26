# Release candidate changes — October 20, 2026

Based on the Phase 1C source and the successfully applied RLS cutover.

- Admin stock/order PATCH now requests PostgREST `Prefer: return=representation`, and only reports success when **exactly one** row was updated. Previously it could treat a 204 / zero-row write as success.
- Aligned order-status validation with the visible options in the Admin UI (including Packed, Out for delivery, Cancelled).
- Failed Admin dashboard reads now show error text instead of silently displaying empty lists.
- Replaced the outdated README and added test records, admin acceptance instructions, screenshot guidelines and GitHub publishing steps.
- Preserved brand assets, existing internal package name, JWT session handling, and RLS migration source. **No live database changes or GitHub pushes were performed.**

## Known verification limits
This release candidate was source-patched and checked for expected modifications, but not compiled or run in this environment. Build/test in Android Studio before public publication. Do not rerun an already applied RLS migration; the backend state is separate from the ZIP.
