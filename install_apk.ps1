param(
    [string]$ApkFile = ""
)

$ADB = "C:\Users\ALHADRAWY\.gemini\antigravity-ide\scratch\platform-tools\adb.exe"
$DEVICE = "R5CW40F1QYF"

if (-not $ApkFile) {
    # Check if a zip artifact was downloaded
    $zip = Get-ChildItem -Path "$env:USERPROFILE\Downloads\*GlassWidgets*.zip" -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($zip) {
        Write-Host "Extracting APK from $($zip.FullName)..." -ForegroundColor Cyan
        Expand-Archive -Path $zip.FullName -DestinationPath "$env:USERPROFILE\Downloads\GlassWidgetsExtracted" -Force
        $latestExtracted = Get-ChildItem -Path "$env:USERPROFILE\Downloads\GlassWidgetsExtracted\*.apk" | Select-Object -First 1
        if ($latestExtracted) { $ApkFile = $latestExtracted.FullName }
    }
    
    if (-not $ApkFile) {
        $latestDownload = Get-ChildItem -Path "$env:USERPROFILE\Downloads\*.apk", ".\*.apk" -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1
        if ($latestDownload) {
            $ApkFile = $latestDownload.FullName
        }
    }
}

if ([string]::IsNullOrWhiteSpace($ApkFile) -or -not (Test-Path $ApkFile)) {
    Write-Host "Error: No APK file found in Downloads folder yet." -ForegroundColor Red
    Write-Host "Please download 'GlassWidgets-debug-apk' first, then run this script." -ForegroundColor Yellow
    exit 1
}

Write-Host "Found APK: $ApkFile" -ForegroundColor Cyan
Write-Host "Installing to Samsung Galaxy S23 Ultra ($DEVICE)..." -ForegroundColor Yellow
& $ADB -s $DEVICE install -r $ApkFile

Write-Host "Launching App..." -ForegroundColor Green
& $ADB -s $DEVICE shell monkey -p com.aistudio.glasswidgets.kxvqzn -c android.intent.category.LAUNCHER 1
Write-Host "Done! Application is running on your phone." -ForegroundColor Green
