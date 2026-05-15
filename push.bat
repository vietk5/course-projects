@echo off
REM =====================================================
REM   Auto push course-projects to GitHub
REM   Repo: https://github.com/vietk5/course-projects
REM =====================================================
chcp 65001 >nul
setlocal EnableDelayedExpansion

cd /d "%~dp0"

echo.
echo ========================================
echo  PUSH course-projects -> GitHub vietk5
echo ========================================
echo.

REM --- Kiem tra git da cai chua ---
where git >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay Git. Vui long cai dat Git tu https://git-scm.com/
    pause
    exit /b 1
)

REM --- Cau hinh git de hien thi dung ten file Tieng Viet ---
git config --global core.quotepath false

REM --- Xoa nested .git folder cua "Tan Cong Mang/key logger/web" neu co ---
if exist "Tấn Công Mạng\key logger\web\.git" (
    echo [INFO] Phat hien nested .git trong "Tấn Công Mạng\key logger\web\.git"
    echo [INFO] Dang xoa de tranh xung dot submodule...
    rmdir /s /q "Tấn Công Mạng\key logger\web\.git"
)

REM --- Init git neu chua co ---
if not exist ".git" (
    echo [INFO] Khoi tao repository moi...
    git init
    git branch -M main
) else (
    echo [INFO] Repository da ton tai.
)

REM --- Cau hinh remote ---
git remote remove origin >nul 2>nul
git remote add origin https://github.com/vietk5/course-projects.git
echo [INFO] Da set remote origin: https://github.com/vietk5/course-projects.git

REM --- Cau hinh user (neu chua co) ---
for /f "tokens=*" %%i in ('git config user.email 2^>nul') do set USER_EMAIL=%%i
if "!USER_EMAIL!"=="" (
    git config user.email "leducviet060305@gmail.com"
    git config user.name "vietk5"
    echo [INFO] Da set git user.
)

REM --- Stage tat ca file ---
echo.
echo [INFO] Dang stage tat ca file ^(co the mat 1-2 phut voi project lon^)...
git add .

REM --- Commit ---
echo.
echo [INFO] Dang tao commit...
git commit -m "Initial commit: upload course projects"
if errorlevel 1 (
    echo [WARN] Khong co thay doi de commit ^(co the da commit truoc do^).
)

REM --- Push ---
echo.
echo [INFO] Dang push len GitHub... ^(co the mat vai phut^)
echo [INFO] Neu duoc hoi password, hay dung Personal Access Token cua GitHub.
echo.
git push -u origin main

if errorlevel 1 (
    echo.
    echo ========================================
    echo  [LOI] Push that bai!
    echo ========================================
    echo Co the do:
    echo   1. Repo tren GitHub khong rong - chay: git pull origin main --allow-unrelated-histories
    echo   2. Sai credential - dang nhap lai bang Personal Access Token
    echo   3. Khong co quyen push vao repo vietk5/course-projects
    echo.
) else (
    echo.
    echo ========================================
    echo  [THANH CONG] Da push len GitHub!
    echo ========================================
    echo Xem repo tai: https://github.com/vietk5/course-projects
    echo.
)

pause
endlocal
