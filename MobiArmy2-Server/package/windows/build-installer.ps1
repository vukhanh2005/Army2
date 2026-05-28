$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$buildDir = Join-Path $root "build\server-package"
$installerDir = Join-Path $root "build\installer"
$classesDir = Join-Path $root "build\classes"
$jarPath = Join-Path $buildDir "MobiArmy.jar"

Remove-Item -LiteralPath $buildDir -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -LiteralPath $installerDir -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $buildDir, $installerDir, $classesDir | Out-Null

$sources = Get-ChildItem -LiteralPath (Join-Path $root "src") -Filter "*.java" -Recurse |
    Where-Object { $_.FullName -notlike "*\src\mobiarmy\Test.java" } |
    ForEach-Object { $_.FullName }

$libs = Get-ChildItem -LiteralPath (Join-Path $root "lib") -Filter "*.jar" |
    ForEach-Object { $_.FullName }

$classpath = [string]::Join([IO.Path]::PathSeparator, $libs)
& javac -encoding UTF-8 -source 20 -target 20 -cp $classpath -d $classesDir @sources
if ($LASTEXITCODE -ne 0) {
    throw "javac failed"
}

$manifest = Join-Path $root "build\manifest-headless.mf"
$classPathLine = "Class-Path: " + ([string]::Join(" ", ($libs | ForEach-Object { "lib/" + (Split-Path $_ -Leaf) })))
Set-Content -LiteralPath $manifest -Encoding ASCII -Value @(
    "Manifest-Version: 1.0",
    "Main-Class: mobiarmy.MobiArmy",
    $classPathLine,
    ""
)

& jar cfm $jarPath $manifest -C $classesDir .
if ($LASTEXITCODE -ne 0) {
    throw "jar failed"
}

Copy-Item -LiteralPath (Join-Path $root "lib") -Destination (Join-Path $buildDir "lib") -Recurse -Force
Copy-Item -LiteralPath (Join-Path $root "res") -Destination (Join-Path $buildDir "res") -Recurse -Force
Copy-Item -LiteralPath (Join-Path $root "cache") -Destination (Join-Path $buildDir "cache") -Recurse -Force
Copy-Item -LiteralPath (Join-Path $root "army.sql") -Destination $buildDir -Force
Copy-Item -LiteralPath (Join-Path $root "server.properties") -Destination $buildDir -Force
Copy-Item -LiteralPath (Join-Path $root "package\windows\run-server.bat") -Destination $buildDir -Force

$runtimeDir = Join-Path $buildDir "runtime"
& jlink --add-modules java.se,jdk.crypto.ec,jdk.unsupported --strip-debug --no-header-files --no-man-pages --output $runtimeDir
if ($LASTEXITCODE -ne 0) {
    throw "jlink failed"
}

$payload = Join-Path $installerDir "payload.zip"
Compress-Archive -Path (Join-Path $buildDir "*") -DestinationPath $payload -Force

$setupPath = Join-Path $installerDir "setup.exe"
$setupApp = Join-Path $root "package\windows\SetupApp\SetupApp.csproj"
$nugetConfig = Join-Path $root "package\windows\SetupApp\NuGet.Config"
& dotnet publish $setupApp -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true -p:PublishTrimmed=false --configfile $nugetConfig -o $installerDir
if ($LASTEXITCODE -ne 0) {
    throw "dotnet publish failed"
}

$publishedExe = Join-Path $installerDir "setup.exe"
if (!(Test-Path -LiteralPath $publishedExe)) {
    throw "setup.exe was not created"
}

Write-Host "Created $setupPath"
