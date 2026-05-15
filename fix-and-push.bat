@echo off
REM =====================================================
REM   FIX nested .git + RE-COMMIT + FORCE PUSH
REM   Repo: https://github.com/vietk5/course-projects
REM =====================================================
chcp 65001 >nul
setlocal EnableDelayedExpansion

cd /d "%~dp0"

echo.
echo =========================================================
echo  FIX nested .git va PUSH lai len GitHub vietk5
echo =========================================================
echo.
echo  Script nay se:
echo    1. Xoa cac .git con (nested) trong cac sub-folder
echo    2. Xoa file index.lock neu co
echo    3. Reset index, add lai toan bo
echo    4. Commit + Force push len GitHub
echo.
echo  CANH BAO: Force push se GHI DE lich su tren GitHub.
echo            Nhung vi repo dang trong, dieu nay an toan.
echo.
set /p CONFIRM="Tiep tuc? (Y/N): "
if /i not "!CONFIRM!"=="Y" (
    echo Da huy.
    pause
    exit /b 0
)

echo.
echo [BUOC 1/6] Xoa cac nested .git folders...

if exist "ATMKD_CK\wirespy\.git" (
    echo   - Xoa ATMKD_CK\wirespy\.git
    rmdir /s /q "ATMKD_CK\wirespy\.git"
)
if exist "An toàn web\NHOM1_ATW\NHOM1_ATW\DACK_WEB_NHOM1\.git" (
    echo   - Xoa An toàn web\NHOM1_ATW\NHOM1_ATW\DACK_WEB_NHOM1\.git
    rmdir /s /q "An toàn web\NHOM1_ATW\NHOM1_ATW\DACK_WEB_NHOM1\.git"
)
if exist "DACK_WEB_NHOM1\.git" (
    echo   - Xoa DACK_WEB_NHOM1\.git
    rmdir /s /q "DACK_WEB_NHOM1\.git"
)
if exist "Tấn Công Mạng\key logger\web\.git" (
    echo   - Xoa Tấn Công Mạng\key logger\web\.git
    rmdir /s /q "Tấn Công Mạng\key logger\web\.git"
)

echo [BUOC 2/6] Xoa file index.lock neu co...
if exist ".git\index.lock" (
    del /f /q ".git\index.lock"
    echo   - Da xoa .git\index.lock
)

echo [BUOC 3/6] Cau hinh git...
git config --global core.quotepath false
git config user.email "leducviet060305@gmail.com"
git config user.name "vietk5"
git config core.autocrlf true

echo [BUOC 4/6] Reset cached index de bo cac gitlink cu...
git rm -r --cached -f --quiet . 2>nul

echo [BUOC 5/6] Add lai toan bo file (co the mat 1-2 phut)...
git add -A

echo.
echo [BUOC 6/6] Commit + Push...
git commit -m "Upload toan bo course projects (fix nested git)" 2>nul
if errorlevel 1 (
    echo   - Khong co thay doi moi de commit, dung commit cu.
)

echo.
echo Dang FORCE PUSH len origin/main...
echo Khi GitHub hoi password, dung Personal Access Token!
echo.
git push -u origin main --force

if errorlevel 1 (
    echo.
    echo =========================================================
    echo  [LOI] Push that bai!
    echo =========================================================
    echo Kiem tra:
    echo   1. Internet ket noi duoc khong
    echo   2. Dung Personal Access Token thay vi password
    echo   3. Token co quyen "repo"
    echo   4. URL remote dung: https://github.com/vietk5/course-projects.git
    echo.
) else (
    echo.
    echo =========================================================
    echo  [THANH CONG] Da push toan bo len GitHub!
    echo =========================================================
    echo  Mo trinh duyet va vao: https://github.com/vietk5/course-projects
    echo  Bay gio se thay day du cac thu muc va file ben trong.
    echo.
)

pause
endlocal
