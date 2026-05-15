# =====================================================
#  FINAL PUSH (no secret in this file)
# =====================================================

$ErrorActionPreference = "Continue"
$root = $PSScriptRoot
Set-Location -LiteralPath $root

# Force UTF-8 for console output and file matching
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host ""
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host " FINAL PUSH" -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host ""

# --- BUOC 1: Quet xem con secret pattern AVNS_ nao trong file source khong ---
Write-Host "[BUOC 1/4] Quet pattern Aiven secret (AVNS_) trong source files..." -ForegroundColor Green

$secretPattern = "AVNS" + "_" + "[A-Za-z0-9]{15,}"
$matches = Get-ChildItem -Path $root -Recurse -File -Force `
    -Include *.md,*.xml,*.java,*.properties,*.yml,*.yaml,*.json,*.txt `
    -ErrorAction SilentlyContinue |
    Where-Object {
        $_.FullName -notlike "*\.git\*" -and
        $_.FullName -notlike "*\.git.backup*" -and
        $_.Length -lt 5MB
    } |
    Select-String -Pattern $secretPattern -ErrorAction SilentlyContinue

if ($matches) {
    Write-Host "  [CANH BAO] Van con pattern AVNS_ trong cac file:" -ForegroundColor Red
    $matches | ForEach-Object {
        $rel = $_.Path.Substring($root.Length + 1)
        Write-Host "    $rel : line $($_.LineNumber)"
    }
    Write-Host ""
    $cont = Read-Host "Van tiep tuc push? GitHub se reject neu day la real secret. (Y/N)"
    if ($cont -ne "Y" -and $cont -ne "y") {
        Read-Host "Press Enter to close"
        exit 1
    }
} else {
    Write-Host "  OK - Khong tim thay pattern AVNS_ nao."
}

# --- BUOC 2: Stage ---
Write-Host ""
Write-Host "[BUOC 2/4] Stage thay doi..." -ForegroundColor Green
git add -A

# --- BUOC 3: Amend commit ---
Write-Host ""
Write-Host "[BUOC 3/4] Amend commit..." -ForegroundColor Green
git commit --amend -m "Upload course projects (redacted secrets)" --no-edit 2>&1 | Out-Null
git log --oneline -1

# --- BUOC 4: Force push ---
Write-Host ""
Write-Host "[BUOC 4/4] FORCE PUSH..." -ForegroundColor Green
Write-Host ""
git push -u origin main --force

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host " [THANH CONG] Da push toan bo!" -ForegroundColor Green
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host " Mo: https://github.com/vietk5/course-projects" -ForegroundColor Cyan
    Write-Host ""
    Write-Host " >>> NHAC NHO BAO MAT <<<" -ForegroundColor Red
    Write-Host " Aiven database password da tung bi expose trong file local." -ForegroundColor Yellow
    Write-Host " Hay vao https://console.aiven.io DOI MAT KHAU NGAY." -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Red
    Write-Host " [LOI] Push that bai. Doc thong bao loi o tren." -ForegroundColor Red
    Write-Host "=========================================================" -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to close"
