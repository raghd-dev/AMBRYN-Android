# AMBRYN — GitHub publishing checklist (Windows CMD)

**Admin operations and the simulated database non-admin stock-write denial have been reported PASS. The final Android project has now built successfully on Windows using Java 21 / Gradle 8.13 (developer screenshot). Before making the repo public, verify current database role state, image redistribution rights, and staged content. See `FINAL_RELEASE_GATES.md`.**

## 0. Confirm you are using the latest package

Extract the ZIP so the `AMBRYN` directory contains `gradlew.bat`, `settings.gradle.kts`, and `app/`. Open **Windows CMD** in that folder. Don't assume an extraction path if it differs on your PC.

Keep `local.properties` **only in the working copy** and outside Git. See `.gitignore`.

## 1. Clean and build
From the extracted `AMBRYN` project folder:

```bat
RUN_FINAL_CHECK.bat
```

The developer built the consolidated UI/UX revision successfully and reported seven UI test passes. **This new engineering revision with added tests still requires its first `testDebugUnitTest` run.** If your environment fails, check that CMD uses Android Studio JBR Java 21 instead of unsupported Java 26. Confirm no private database dumps, `local.properties`, signing keys, `build/` folders, or screenshots with customer details appear in the staged files. Verify you own or may redistribute the included images.

## 2. Capture safe screenshots
Save screenshots under `docs/screenshots/`: `01-splash.png`, `02-login.png`, `03-home.png`, `04-shop.png`, `05-cart.png`, `06-wishlist.png`, `07-demo-checkout.png`, `08-orders.png`, `09-admin-dashboard.png` and `10-admin-inventory.png`. Use test data and remove private information.

## 3. Create a new GitHub repository
On GitHub, create an **empty**, **PRIVATE first**, repo named `AMBRYN-Android`. Don't check the optional README/.gitignore/license boxes if your local project has them. After final review you can make it public.

In Windows CMD, opened in the local AMBRYN folder:

```bat
git init
git branch -M main
git status
git add .
git diff --cached --name-only
git diff --cached --check
```

Read the staged file list. `local.properties`, credentials, a private SQL data dump, `.backup` files, or customer information **must never be staged**. If any appears, **STOP** and unstage/remove it before committing. The `sql/` folder can include intentionally public migration source files; never include database exports with real data.

To check tracked files for common sensitive patterns before pushing:

```bat
git grep -n -I -E "sb_secret_|service_role|DATABASE_PASSWORD|postgresql://|SUPABASE_PUBLISHABLE_KEY=sb_" --cached
```

Review every match; examples or warnings may match, but real credential values must never be public. Even publishable keys should generally remain in your local config rather than hard-coded into the repository.

When satisfied:

```bat
git commit -m "Release AMBRYN Android portfolio project"
git remote add origin https://github.com/raghd-dev/AMBRYN-Android.git
git push -u origin main
```

Use your real GitHub account. Never put a GitHub password or token in the remote URL. If identity is not configured, set `git config user.name` and `git config user.email` for your git installation.

## 4. First CI run (private repository)

The new `.github/workflows/android-ci.yml` builds and runs 17 JUnit tests automatically with placeholder Supabase values. After the first push, open the repository's **Actions** tab. Only mark CI as passing when the Android CI workflow is green. If GitHub displays a failing log, save the relevant error text without any credentials.

## 5. After push
Verify the GitHub README renders, download the source in a clean folder and build it using a separate test Supabase project, and check that the initial commit contains no private files. Add screenshots/demonstration recording after removing all sensitive details. Include an accurate description of **simulated checkout** and pending limitations.

Do not use GitHub public release as a sign that AMBRYN can accept real commerce transactions.

## Important: SQL historical files
`` is a historical migration draft. `sql/RLS_APPLIED_REFERENCE_DO_NOT_RERUN.sql` mirrors the final security cutover that was applied; **NEVER reapply it to the existing live database**. The release check is read-only and the negative test rolls back.

## Verification before changing repository visibility

After pushing, open the GitHub repository in your browser. Confirm the README renders, the file list has no local secrets, and the screenshots contain no real customer data. Keep the repository private if permission to redistribute any included product photography is unverified. The public university portfolio should clearly mention simulated checkout and other known limitations.
