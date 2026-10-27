param([string]$ProjectRoot = 'D:\AMBRYN_Release\AMBRYN')
$ErrorActionPreference = 'Stop'
$results = New-Object System.Collections.Generic.List[string]
$critical=0
function Info([string]$s) { Write-Host $s; $script:results.Add($s) }
function Fail([string]$s) { Write-Host "FAIL: $s" -ForegroundColor Red; $script:results.Add("FAIL: $s"); $script:critical++ }
function Note([string]$s) { Write-Host "NOTICE: $s" -ForegroundColor Yellow; $script:results.Add("NOTICE: $s") }
try {
    Set-Location -LiteralPath $ProjectRoot
    $top = (& git rev-parse --show-toplevel 2>$null).Trim()
    if ($LASTEXITCODE -ne 0) { throw 'Not a Git repository' }
    Info "AMBRYN Local Phase 1 Audit -- $top"
    $branch = (& git branch --show-current).Trim()
    if ($branch -ne 'main') { Fail "Expected main branch; got $branch" } else { Info 'PASS: main branch' }
    $remote = (& git remote get-url origin).Trim()
    if ($remote -notmatch 'raghd-dev/AMBRYN-Android') { Fail 'Unexpected Git origin' } else { Info 'PASS: expected Git origin' }
    & git merge-base --is-ancestor '76bf2f5' HEAD 2>$null
    if ($LASTEXITCODE -ne 0) { Fail 'Confirmed portfolio commit 76bf2f5 is not in local HEAD ancestry' } else { Info 'PASS: expected portfolio commit is included' }
    $names = @('03-home-final.png','04-candles-final.png','05-product-final.png','06-cart-final.png','07-checkout-final.png','08-admin-final.png')
    $readmeText = [IO.File]::ReadAllText((Join-Path $ProjectRoot 'README.md'))
    foreach ($name in $names) {
        $path = Join-Path $ProjectRoot ('docs\screenshots\' + $name)
        if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { Fail "Screenshot missing: $name"; continue }
        $bytes = [IO.File]::ReadAllBytes($path)
        if ($bytes.Length -lt 1000 -or $bytes.Length -lt 8 -or $bytes[0] -ne 137 -or $bytes[1] -ne 80 -or $bytes[2] -ne 78 -or $bytes[3] -ne 71) { Fail "Invalid PNG: $name" }
        elseif (-not $readmeText.Contains(('docs/screenshots/' + $name))) { Fail "README missing image link: $name" }
        else { Info "PASS: screenshot $name" }
    }
    $jpg=@(& git ls-files -- 'app/src/main/res/drawable/*.jpg')
    if ($jpg.Count -ne 24) { Note "Expected 24 tracked JPG assets; found $($jpg.Count). Confirm before release." } else { Info 'PASS: 24 original JPG resource paths tracked' }
    $imgDiff = @(& git diff HEAD --name-only -- 'app/src/main/res/drawable/*.jpg' 'app/src/main/res/drawable/*.png')
    if ($imgDiff.Count -gt 0) { Fail 'There are local changes to the original JPG/PNG resources' } else { Info 'PASS: original app images/logo resources unchanged relative to HEAD' }
    $riskPatterns=@{
        'Supabase secret key'='(?i)sb_secret_[A-Za-z0-9_-]{8,}'
        'Legacy service-role literal'='(?i)service[_ -]?role\s*[=:]\s*["'']?[A-Za-z0-9_.-]{25,}'
        'GitHub access token'='gh[pousr]_[A-Za-z0-9_]{20,}'
        'Private key header'='-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----'
        'AWS access key'='AKIA[0-9A-Z]{16}'
        'Embedded JWT'='eyJ[A-Za-z0-9_-]{30,}\.eyJ[A-Za-z0-9_-]{25,}'
    }
    $allowed = @('.kt','.kts','.md','.sql','.psql','.xml','.json','.yml','.yaml','.txt','.properties','.example','.bat','.cmd','.ps1','.gradle','.gitignore')
    $history = @(& git rev-list --objects --all)
    $seen = New-Object 'System.Collections.Generic.HashSet[string]'
    $inspected = 0
    foreach ($line in $history) {
        if ($line -notmatch '^([0-9a-f]{40,64})\s+(.+)$') { continue }
        $oid=$Matches[1]; $path=$Matches[2]
        if ($allowed -notcontains [IO.Path]::GetExtension($path).ToLowerInvariant() -and -not $path.EndsWith('.gitignore')) { continue }
        if (-not $seen.Add($oid)) { continue }
        $sizeText = & git cat-file -s $oid 2>$null
        if ($LASTEXITCODE -ne 0) { continue }
        $size=[int64]($sizeText | Select-Object -First 1)
        if ($size -gt 512000) { continue }
        $data = ((& git cat-file -p $oid) -join "`n")
        $inspected++
        foreach ($k in $riskPatterns.Keys) {
            if ([regex]::IsMatch($data, $riskPatterns[$k])) { Fail "Possible $k in historical Git blob at path: $path (content suppressed)" }
        }
    }
    Info "HISTORY: scanned $inspected unique small text blobs from local Git history against common credential signatures."
    $historicalImages = @($history | Where-Object { $_ -match '(?i)\.(?:jpg|jpeg|png)(?:\s|$)' })
    if ($historicalImages.Count -gt 0) { Note "Git history contains $($historicalImages.Count) named image blobs. Original third-party imagery remains uncleared for PUBLIC redistribution." }
    $status = @(& git status --short)
    if ($status.Count -gt 0) { Note 'Working tree has local changes; expected if you have just extracted this finishing pack.' } else { Info 'PASS: working tree clean' }
    Note 'This pattern scan does NOT prove complete security and cannot establish image licenses or identify private data in historical pixels.'
} catch { Fail $_.Exception.Message }
$log=Join-Path $env:TEMP 'AMBRYN_PHASE1_AUDIT.txt'
[IO.File]::WriteAllLines($log, $results, [Text.UTF8Encoding]::new($false))
Info "Audit result file: $log"
if ($critical -gt 0) { Write-Host "STOP: $critical blocking issue(s). Do not push/tag; share the redacted audit log." -ForegroundColor Red; exit 2 }
Write-Host 'PASS: basic local preflight. PRIVATE demo only; asset rights still outstanding.' -ForegroundColor Green
exit 0
