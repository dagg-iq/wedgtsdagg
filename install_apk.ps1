param(
    [string]$ApkFile = ""
)

$ADB = "C:\Users\ALHADRAWY\.gemini\antigravity-ide\scratch\platform-tools\adb.exe"
$DEVICE = "R5CW40F1QYF"

if (-not $ApkFile) {
    # Find latest APK in Downloads or current folder
    $latestDownload = Get-ChildItem -Path "$env:USERPROFILE\Downloads\*.apk", ".\*.apk" -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($latestDownload) {
        $ApkFile = $latestDownload.FullName
    }
}

if (-not (Test-Path $ApkFile)) {
    Write-Host "Please specify APK file or put it in Downloads folder." -ForegroundColor Yellow
    exit 1
}

Write-Host "Found APK: $ApkFile" -ForegroundColor Cyan
Write-Host "Installing to Samsung Galaxy S23 Ultra ($DEVICE)..." -ForegroundColor Yellow
& $ADB -s $DEVICE install -r $ApkFile

Write-Host "Launching App..." -ForegroundColor Green
& $ADB -s $DEVICE shell monkey -p com.aistudio.glasswidgets.kxvqzn -c android.intent.category.LAUNCHER 1
Write-Host "Done! Application is running on your phone." -ForegroundColor Green
