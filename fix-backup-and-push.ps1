# =====================================================
#  Sua loi .git.backup va push lai
# =====================================================

$ErrorActionPreference = "Continue"
$root = $PSScriptRoot
Set-Location -LiteralPath $root

Write-Host ""
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host " FIX .git.backup + RE-PUSH" -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host ""

# --- BUOC 1: Move .git.backup ra ngoai thu muc cha ---
Write-Host "[BUOC 1/5] Di chuyen .git.backup ra ngoai..." -ForegroundColor Green
$backupSrc = Join-Path $root ".git.backup"
$parent = Split-Path -Parent $root
$backupDest = Join-Path $parent ".git.backup-course-projects-old"

if (Test-Path -LiteralPath $backupSrc) {
    if (Test-Path -LiteralPath $backupDest) {
        Write-Host "  - Xoa backup cu o $backupDest"
        Remove-Item -LiteralPath $backupDest -Recurse -Force -ErrorAction SilentlyContinue
    }
    Write-Host "  - Move: $backupSrc"
    Write-Host "        ->  $backupDest"
    Move-Item -LiteralPath $backupSrc -Destination $backupDest -Force
    Write-Host "  OK. Backup van con an toan o thu muc cha."
} else {
    Write-Host "  - Khong co .git.backup (co the da xoa truoc do)."
}

# --- BUOC 2: Bo .git.backup khoi git index neu co ---
Write-Host ""
Write-Host "[BUOC 2/5] Xoa .git.backup khoi git index..." -ForegroundColor Green
git rm -r --cached --ignore-unmatch .git.backup 2>&1 | Out-Null
Write-Host "  OK"

# --- BUOC 3: Add lai (.gitignore da co .git.backup/) ---
Write-Host ""
Write-Host "[BUOC 3/5] Stage lai voi .gitignore moi..." -ForegroundColor Green
git add -A
Write-Host "  OK"

# --- BUOC 4: Amend commit ---
Write-Host ""
Write-Host "[BUOC 4/5] Amend commit (gop vao commit truoc)..." -ForegroundColor Green
git commit --amend -m "Upload toan bo course projects" --no-edit 2>&1 | Out-Null
git log --oneline -1

# --- BUOC 5: Force push ---
Write-Host ""
Write-Host "[BUOC 5/5] FORCE PUSH len GitHub..." -ForegroundColor Green
Write-Host "  Khi GitHub hoi password, dung Personal Access Token!" -ForegroundColor Yellow
Write-Host ""

git push -u origin main --force

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host " [THANH CONG] Da push toan bo!" -ForegroundColor Green
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host " Mo: https://github.com/vietk5/course-projects" -ForegroundColor Cyan
    Write-Host ""
    Write-Host " Backup .git cu nam o:" -ForegroundColor Gray
    Write-Host "   $backupDest" -ForegroundColor Gray
    Write-Host " Xoa di neu khong can phuc hoi history cu." -ForegroundColor Gray
} else {
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Red
    Write-Host " [LOI] Push that bai. Doc thong bao loi o tren." -ForegroundColor Red
    Write-Host "=========================================================" -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to close"
