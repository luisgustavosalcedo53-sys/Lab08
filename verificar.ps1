param([string]$JavaHome = "C:/Users/Gustavo/.jdks/jbr-17.0.14", [string]$Sdk = "$env:LOCALAPPDATA/Android/Sdk")
$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = $JavaHome
$adb = Join-Path $Sdk 'platform-tools/adb.exe'
Push-Location $PSScriptRoot
try {
    & ./gradlew.bat assembleDebug assembleDebugAndroidTest lintDebug
    if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación.' }
    & $adb install -r app/build/outputs/apk/debug/app-debug.apk
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo instalar la aplicación.' }
    & $adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo instalar el APK de pruebas.' }
    $result = & $adb shell am instrument -w com.example.lab08.test/androidx.test.runner.AndroidJUnitRunner
    $result
    if (($result -join "`n") -notmatch 'OK \(2 tests\)') { throw 'Las pruebas no terminaron correctamente.' }
} finally { Pop-Location }
