$ErrorActionPreference = "Continue"

Write-Host "=== [1/5] Setting up Environment & Virtual Drive ===" -ForegroundColor Cyan
if (-not (Test-Path "W:\")) {
    subst W: "d:\MySecretApp\تطبيقات apk\glass-widgets"
}

$env:JAVA_HOME = "C:\Users\ALHADRAWY\.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$ADB = "C:\Users\ALHADRAWY\.gemini\antigravity-ide\scratch\platform-tools\adb.exe"
$DEVICE = "R5CW40F1QYF"

Write-Host "Java Version:" -ForegroundColor Green
& "$env:JAVA_HOME\bin\java.exe" -version

Write-Host "`n=== [2/5] Downloading Gradle 9.3.1 Distribution ===" -ForegroundColor Cyan
$gradleDistDir = "C:\Users\ALHADRAWY\.gradle\wrapper\dists\gradle-9.3.1-bin\23ovyewtku6u96viwx3xl3oks"
if (-not (Test-Path $gradleDistDir)) {
    New-Item -ItemType Directory -Force -Path $gradleDistDir | Out-Null
}

$gradleZip = Join-Path $gradleDistDir "gradle-9.3.1-bin.zip"
# Remove stale lock if present
Remove-Item -Path (Join-Path $gradleDistDir "*.lck") -Force -ErrorAction SilentlyContinue

$gradleUrl = "https://services.gradle.org/distributions/gradle-9.3.1-bin.zip"
Write-Host "Downloading $gradleUrl to $gradleZip with resume support..." -ForegroundColor Yellow
curl.exe -fSL -C - --retry 10 --retry-delay 5 -o $gradleZip $gradleUrl

# Verify zip
if (Test-Path $gradleZip) {
    $size = (Get-Item $gradleZip).Length
    Write-Host "Downloaded Gradle Zip Size: $([math]::Round($size / 1MB, 2)) MB" -ForegroundColor Green
}

Write-Host "`n=== [3/5] Initializing Gradle Wrapper ===" -ForegroundColor Cyan
Set-Location W:\
cmd.exe /c "gradlew.bat --version"

Write-Host "`n=== [4/5] Building Glass Widgets Debug APK ===" -ForegroundColor Cyan
cmd.exe /c "gradlew.bat assembleDebug --stacktrace"

$apkPath = "W:\app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    Write-Host "`n=== [5/5] Installing APK on Device $DEVICE ===" -ForegroundColor Green
    & $ADB -s $DEVICE install -r $apkPath
    Write-Host "Launching Application..." -ForegroundColor Green
    & $ADB -s $DEVICE shell monkey -p com.aistudio.glasswidgets.kxvqzn -c android.intent.category.LAUNCHER 1
    Write-Host "SUCCESS: Glass Widgets installed and launched on phone!" -ForegroundColor Green
} else {
    Write-Host "APK not found at $apkPath. Check build logs above." -ForegroundColor Red
}
