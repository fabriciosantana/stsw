param(
    [switch]$SkipSonar,
    [string]$SonarToken = $env:SONAR_TOKEN
)

$ErrorActionPreference = "Stop"
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

Push-Location $repoRoot
try {
    Write-Host "Running tests + JaCoCo report..."
    .\mvnw.cmd clean verify

    if ($SkipSonar) {
        Write-Host "SkipSonar enabled. Finishing after JaCoCo."
        exit 0
    }

    if ([string]::IsNullOrWhiteSpace($SonarToken)) {
        throw "SONAR_TOKEN not informed. Set SONAR_TOKEN or use -SonarToken."
    }

    Write-Host "Publishing analysis to SonarQube..."
    .\mvnw.cmd sonar:sonar "-Dsonar.token=$SonarToken" "-Dsonar.host.url=http://localhost:9000" "-Dsonar.qualitygate.wait=true"
}
finally {
    Pop-Location
}

