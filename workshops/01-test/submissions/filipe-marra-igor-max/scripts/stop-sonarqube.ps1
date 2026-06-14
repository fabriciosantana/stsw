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

Write-Host "Stopping SonarQube stack..."
& $docker compose down
Write-Host "SonarQube stack stopped."
