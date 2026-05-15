# =====================================================
#  NUCLEAR PUSH - Reset toan bo git history va push lai
#  Repo: https://github.com/vietk5/course-projects
# =====================================================

$ErrorActionPreference = "Continue"
$root = $PSScriptRoot
Set-Location -LiteralPath $root

Write-Host ""
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host " NUCLEAR PUSH - course-projects -> GitHub vietk5" -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Script nay se:" -ForegroundColor Yellow
Write-Host "  1. Backup .git hien tai -> .git.backup"
Write-Host "  2. Xoa TAT CA nested .git folders trong sub-folders"
Write-Host "  3. Init lai git (sach hoan toan, mat lich su cu)"
Write-Host "  4. Add toan bo file (loai tru target/, *.jar... theo .gitignore)"
Write-Host "  5. Commit + FORCE PUSH len GitHub"
Write-Host ""
Write-Host "Vi sao can lam vay?" -ForegroundColor Yellow
Write-Host "  - Lich su cu chua file SPKT_Lab_2020_v1.1.pdf (176MB) > gioi han 100MB"
Write-Host "  - Co 3 nested .git lam git chi push gitlink, khong push code"
Write-Host ""
$confirm = Read-Host "Tiep tuc? (Y/N)"
if ($confirm -ne "Y" -and $confirm -ne "y") {
    Write-Host "Da huy." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 0
}

# --- BUOC 1: Backup .git ---
Write-Host ""
Write-Host "[BUOC 1/6] Backup .git hien tai..." -ForegroundColor Green
if (Test-Path ".git") {
    if (Test-Path ".git.backup") {
        Write-Host "  - Xoa .git.backup cu..."
        Remove-Item -LiteralPath ".git.backup" -Recurse -Force -ErrorAction SilentlyContinue
    }
    Write-Host "  - Move .git -> .git.backup"
    Move-Item -LiteralPath ".git" -Destination ".git.backup" -Force
    Write-Host "  OK. Neu can phuc hoi: rename .git.backup thanh .git" -ForegroundColor Gray
} else {
    Write-Host "  - Khong co .git, bo qua."
}

# --- BUOC 2: Xoa cac nested .git ---
Write-Host ""
Write-Host "[BUOC 2/6] Tim va xoa cac nested .git folders..." -ForegroundColor Green
$nestedGits = Get-ChildItem -LiteralPath $root -Recurse -Force -Directory -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -eq ".git" -and $_.FullName -ne (Join-Path $root ".git.backup") }

if ($nestedGits.Count -eq 0) {
    Write-Host "  - Khong tim thay nested .git nao."
} else {
    foreach ($g in $nestedGits) {
        Write-Host "  - Xoa: $($g.FullName)"
        try {
            Remove-Item -LiteralPath $g.FullName -Recurse -Force -ErrorAction Stop
        } catch {
            Write-Host "    [LOI] $($_.Exception.Message)" -ForegroundColor Red
            # Thu lai bang cmd
            $cmdPath = $g.FullName -replace '/', '\'
            cmd /c "rmdir /s /q `"$cmdPath`""
        }
    }
}

# --- BUOC 3: Init git moi ---
Write-Host ""
Write-Host "[BUOC 3/6] Init git moi va cau hinh..." -ForegroundColor Green
git init | Out-Null
git branch -M main
git config user.email "leducviet060305@gmail.com"
git config user.name "vietk5"
git config core.quotepath false
git config core.autocrlf true
git remote add origin https://github.com/vietk5/course-projects.git
Write-Host "  - Remote: https://github.com/vietk5/course-projects.git"

# --- BUOC 4: Add toan bo ---
Write-Host ""
Write-Host "[BUOC 4/6] Add toan bo file (.gitignore loai bo target/, *.jar...)..." -ForegroundColor Green
Write-Host "  Co the mat 1-2 phut..." -ForegroundColor Gray
git add -A 2>&1 | Where-Object { $_ -notmatch "warning:" } | Select-Object -First 5

# --- BUOC 4.5: Kiem tra file > 95MB ---
Write-Host ""
Write-Host "[BUOC 4.5] Kiem tra file > 95MB co the bi GitHub reject..." -ForegroundColor Green
$bigFiles = Get-ChildItem -LiteralPath $root -Recurse -File -Force -ErrorAction SilentlyContinue |
    Where-Object { $_.Length -gt 95MB -and $_.FullName -notlike "*\.git\*" -and $_.FullName -notlike "*\.git.backup\*" }

if ($bigFiles.Count -gt 0) {
    Write-Host "  CANH BAO: Co $($bigFiles.Count) file > 95MB. Dang loai khoi git va them vao .gitignore:" -ForegroundColor Yellow
    foreach ($f in $bigFiles) {
        $rel = $f.FullName.Substring($root.Length + 1).Replace('\', '/')
        $sizeMB = [math]::Round($f.Length / 1MB, 2)
        Write-Host "    - $rel  ($sizeMB MB)"
        git rm --cached -- "$rel" 2>$null | Out-Null
        Add-Content -LiteralPath ".gitignore" -Value $rel -Encoding UTF8
    }
    git add .gitignore | Out-Null
} else {
    Write-Host "  - Khong co file lon."
}

# --- BUOC 5: Commit ---
Write-Host ""
Write-Host "[BUOC 5/6] Tao commit..." -ForegroundColor Green
git commit -m "Upload toan bo course projects" | Out-Null
git log --oneline -1

# --- BUOC 6: Push ---
Write-Host ""
Write-Host "[BUOC 6/6] FORCE PUSH len GitHub..." -ForegroundColor Green
Write-Host "  Khi GitHub hoi password, dung Personal Access Token!" -ForegroundColor Yellow
Write-Host "  Token: https://github.com/settings/tokens (tick scope 'repo')" -ForegroundColor Yellow
Write-Host ""

git push -u origin main --force

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host " [THANH CONG] Da push toan bo len GitHub!" -ForegroundColor Green
    Write-Host "=========================================================" -ForegroundColor Green
    Write-Host " Mo: https://github.com/vietk5/course-projects" -ForegroundColor Cyan
    Write-Host ""
    Write-Host " Neu moi thu OK, ban co the xoa thu muc .git.backup" -ForegroundColor Gray
} else {
    Write-Host ""
    Write-Host "=========================================================" -ForegroundColor Red
    Write-Host " [LOI] Push that bai. Xem thong bao loi o tren." -ForegroundColor Red
    Write-Host "=========================================================" -ForegroundColor Red
    Write-Host " Phuc hoi .git cu: rename .git.backup -> .git" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Press Enter to close"
