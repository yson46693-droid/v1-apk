@echo off
SETLOCAL

echo 🔍 Checking project root...

IF NOT EXIST "settings.gradle" IF NOT EXIST "build.gradle" (
    echo ❌ لا يمكن إيجاد settings.gradle أو build.gradle
    exit /b 1
)

echo ✔ Found project root.

echo 🔧 Generating Gradle Wrapper...
gradle wrapper --gradle-version 8.0.2
IF NOT EXIST "gradlew.bat" (
    echo ❌ فشل إنشاء gradlew!
    exit /b 1
)

echo 🚀 Building APK (Release)...
gradlew.bat assembleRelease

REM البحث عن ملف APK
for /R %%i in (*.apk) do (
    set APK_PATH=%%i
)

IF "%APK_PATH%"=="" (
    echo ❌ فشل العثور على APK!
    exit /b 1
)

echo 🎉 تم إنشاء APK بنجاح!
echo 📦 موقع الملف:
echo %APK_PATH%

ENDLOCAL
pause
