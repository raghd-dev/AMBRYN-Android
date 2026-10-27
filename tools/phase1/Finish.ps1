param([string]$ProjectRoot = 'D:\AMBRYN_Release\AMBRYN')
$ErrorActionPreference='Stop'
function Run([string[]]$cmd) { & $cmd[0] $cmd[1..($cmd.Length - 1)]; if ($LASTEXITCODE -ne 0) { throw "Command failed: $($cmd[0])" } }
Set-Location -LiteralPath $ProjectRoot
$script = Join-Path $ProjectRoot 'tools\phase1\Preflight.ps1'
& powershell.exe -NoProfile -ExecutionPolicy Bypass -File $script -ProjectRoot $ProjectRoot
if ($LASTEXITCODE -ne 0) { throw 'Preflight failed. No GitHub changes made.' }
$expected=@('README.md','docs/PHASE1_PRIVATE_RELEASE_REPORT.md','docs/AMBRYN_PRIVATE_RELEASE_NOTES.md','docs/media/AMBRYN_Private_Demo_Walkthrough.mp4','tools/phase1/Preflight.ps1','tools/phase1/Finish.ps1','RUN_PHASE1_FINISH.cmd','RUN_PHASE1_AUDIT.cmd')
$staged=@(& git diff --cached --name-only)
if ($staged.Count -gt 0) { throw 'Other staged changes exist. Run git diff --cached --stat, then clear/reconcile staged changes before continuing.' }
foreach($f in $expected) { if (-not (Test-Path $f)) { throw "Required kit file missing: $f" } }
& git add -- $expected
if ($LASTEXITCODE -ne 0) { throw 'Could not stage Phase 1 documents' }
& git diff --cached --check
if ($LASTEXITCODE -ne 0) { throw 'Whitespace check failed. Do not commit.' }
$changed=@(& git diff --cached --name-only)
if ($changed.Count -gt 0) {
    & git commit -m 'docs: finish AMBRYN private portfolio demo release'
    if ($LASTEXITCODE -ne 0) { throw 'Git commit failed' }
    & git push origin main
    if ($LASTEXITCODE -ne 0) { throw 'Git push failed: fix connection before tagging' }
} else { Write-Host 'Phase 1 docs already committed; no new commit required.' }
$tag='v1.0.0-private-demo'
$head=(& git rev-parse HEAD).Trim()
$existing=@(& git tag --list $tag)
if ($existing.Count -gt 0) {
    $target=(& git rev-list -n 1 $tag).Trim()
    if ($target -ne $head) { throw "Existing $tag points to another commit. Do not move release tags silently." }
    Write-Host "Tag already exists on HEAD: $tag"
} else {
    & git tag -a $tag -m 'AMBRYN private portfolio demonstration; original demo images retained'
    if ($LASTEXITCODE -ne 0) { throw 'Tag creation failed' }
}
& git push origin $tag
if ($LASTEXITCODE -ne 0) { throw 'Tag push failed' }
$desc='Kotlin & Jetpack Compose e-commerce Android demo with Supabase Auth, PostgreSQL RLS, customer shopping, admin orders and GitHub Actions CI.'
$topics='android,kotlin,jetpack-compose,supabase,ecommerce,postgresql,github-actions,material3,android-app'
$gh=Get-Command gh -ErrorAction SilentlyContinue
if ($gh) {
    & gh auth status *> $null
    if ($LASTEXITCODE -eq 0) {
        $visibility=(& gh repo view raghd-dev/AMBRYN-Android --json visibility --jq '.visibility').Trim()
        if ($visibility -ne 'PRIVATE') { throw "Repository visibility is $visibility. Stop: original asset rights not cleared." }
        & gh repo edit raghd-dev/AMBRYN-Android --description $desc --add-topic $topics
        if ($LASTEXITCODE -ne 0) { throw 'GitHub About update failed' }
        & gh release view $tag --repo raghd-dev/AMBRYN-Android *> $null
        if ($LASTEXITCODE -ne 0) {
            & gh release create $tag --repo raghd-dev/AMBRYN-Android --draft --title 'AMBRYN v1.0.0 — Private Portfolio Demo' --notes-file docs/AMBRYN_PRIVATE_RELEASE_NOTES.md --verify-tag
            if ($LASTEXITCODE -ne 0) { throw 'Could not create GitHub draft release' }
        } else { Write-Host 'Release already exists; not altering it.' }
        Write-Host 'PASS: GitHub About updated, private annotated tag pushed, private draft release created/verified.' -ForegroundColor Green
    } else { Write-Host 'gh CLI is not authenticated. Description/topics and draft release must be configured via GitHub web UI.' -ForegroundColor Yellow }
} else { Write-Host 'gh CLI not installed. Update GitHub About and draft Release manually; tag has been pushed.' -ForegroundColor Yellow }
Write-Host "Repository: https://github.com/raghd-dev/AMBRYN-Android";Write-Host "About description: $desc";Write-Host "Topics: $topics"
Write-Host 'Keep repository PRIVATE. Do not upload an APK with credentials or live data.'
Write-Host 'Now verify that GitHub Actions passes on the new docs commit.'
