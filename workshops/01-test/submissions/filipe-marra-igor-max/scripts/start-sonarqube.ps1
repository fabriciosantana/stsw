param(
    [int]$TimeoutSeconds = 300
)

$ErrorActionPreference = "Stop"

function Get-DockerCommand {
    $dockerCommand = Get-Command docker -ErrorAction SilentlyContinue
    if ($null -ne $dockerCommand) {
        return $dockerCommand.Source
    }

    $commonPaths = @(
        "C:\Program Files\Docker\Docker\resources\bin\docker.exe",
        "C:\Program Files\Docker\Docker\resources\docker.exe"
    )

    foreach ($path in $commonPaths) {
        if (Test-Path $path) {
            return $path
        }
    }

    throw "Docker CLI not found. Install Docker Desktop and ensure 'docker' is in PATH."
}

$docker = Get-DockerCommand

Write-Host "Starting SonarQube stack with Docker Compose..."
& $docker compose up -d

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
$lastStatus = "UNAVAILABLE"

while ((Get-Date) -lt $deadline) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:9000/api/system/status" -TimeoutSec 5
        $lastStatus = $response.status
        Write-Host "Current SonarQube status: $lastStatus"
        if ($lastStatus -eq "UP") {
            Write-Host "SonarQube is ready at http://localhost:9000"
            exit 0
        }
    } catch {
        Write-Host "Waiting for SonarQube to be available..."
    }

    Start-Sleep -Seconds 5
}

Write-Error "SonarQube did not become ready in $TimeoutSeconds seconds. Last status: $lastStatus"
exit 1
