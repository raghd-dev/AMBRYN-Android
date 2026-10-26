# Final security verification — reported October 20, 2026

This file records the test output supplied by the developer. It is **not** an independently performed security audit.

- PostgreSQL transaction began.
- `request.jwt.claim.sub` was set to a temporary random UUID (no real customer account).
- `request.jwt.claim.role` was set to `authenticated`.
- `SET LOCAL ROLE authenticated` succeeded.
- `current_user = authenticated`.
- `auth.uid() IS NOT NULL` returned `true`.
- `public.ambryn_is_admin()` returned `false`.
- `SELECT COUNT(*) FROM public.product_stock` returned `6`.
- Attempted same-value update of first stock item returned **`UPDATE 0`**.
- Transaction ended with **`ROLLBACK`**; no changes were retained.

**Conclusion:** The database-layer RLS policy blocked the tested unauthorized stock update under a simulated non-admin session. This does **not** prove all admin endpoints resist attacks, and a real token-driven API test remains recommended.

The latest recorded app tests were: six customer features, separate customer accounts, review-email column permissions, and stock/order admin operations, all reported PASS by the developer. The SQL cutover verification returned 0 legacy anonymous write policies. One or more admin user-role records may have been repaired after initial tests, so run `sql/READ_ONLY_RELEASE_CHECK.sql` against the live database for current counts.
