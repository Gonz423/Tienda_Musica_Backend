# Compila los 5 microservicios y el frontend, los copia a la EC2 y ejecuta la instalación (guía 18).
#
# Uso (PowerShell, desde Tienda_Musica_Backend):
#   .\deploy\subir_a_ec2.ps1 -Ip 1.2.3.4 -Pem "C:\Users\gonza\Downloads\sonido-vivo.pem"
param(
    [Parameter(Mandatory = $true)][string]$Ip,
    [Parameter(Mandatory = $true)][string]$Pem,
    [switch]$SinCompilar
)
# 'Continue': en PowerShell 5.1 la salida de error de ssh/apt/maven no debe cortar el script;
# cada comando externo se valida con su código de salida en Revisar().
$ErrorActionPreference = 'Continue'

$backend   = Split-Path -Parent $PSScriptRoot
$frontend  = Join-Path (Split-Path -Parent $backend) 'Tienda_Musica_Frontend'
$servicios = 'auth', 'usuario', 'producto', 'pedido', 'metrica'
$destino   = "ubuntu@$Ip"
$sshOpts   = @('-i', $Pem, '-o', 'StrictHostKeyChecking=accept-new')
$temporal  = Join-Path $env:TEMP 'tienda-musica-deploy'

function Paso($texto) { Write-Host "`n==> $texto" -ForegroundColor Cyan }
function Revisar($que) { if ($LASTEXITCODE -ne 0) { throw "Falló: $que (código $LASTEXITCODE)" } }

Paso '1/5 Permisos de la llave .pem (Windows exige que solo tú puedas leerla)'
icacls $Pem /inheritance:r | Out-Null
icacls $Pem /grant:r "$($env:USERNAME):(R)" | Out-Null
icacls $Pem /remove:g 'Users' 'Authenticated Users' 'Everyone' 'Usuarios' 'Usuarios autentificados' 'Todos' 2>$null | Out-Null

New-Item -ItemType Directory -Force $temporal | Out-Null

if (-not $SinCompilar) {
    Paso '2/5 Compilando los 5 JAR (mvnw clean package)'
    foreach ($s in $servicios) {
        Push-Location (Join-Path $backend "$s-service")
        & .\mvnw.cmd -q clean package
        Revisar "$s-service"
        Pop-Location
        $jar = Get-ChildItem (Join-Path $backend "$s-service\target\*.jar") -ErrorAction Stop | Select-Object -First 1
        Copy-Item $jar.FullName (Join-Path $temporal "$s-service.jar") -Force -ErrorAction Stop
        Write-Host "  [OK] $s-service.jar"
    }

    Paso '3/5 Build de producción del frontend (usa .env.production)'
    Push-Location $frontend
    npm run build
    Revisar 'npm run build'
    tar -czf (Join-Path $temporal 'tienda-frontend.tar.gz') -C dist .
    Revisar 'empaquetar dist'
    Pop-Location
}

Paso "4/5 Copiando archivos a $Ip"
$archivos = @($servicios | ForEach-Object { Join-Path $temporal "$_-service.jar" }) + (Join-Path $temporal 'tienda-frontend.tar.gz')
scp @sshOpts $archivos "${destino}:/tmp/"
Revisar 'scp de JAR y frontend'
ssh @sshOpts $destino 'rm -rf /tmp/deploy'
scp @sshOpts -r (Join-Path $backend 'deploy') "${destino}:/tmp/deploy"
Revisar 'scp de la carpeta deploy'

Paso '5/5 Instalando en la EC2 (MariaDB, systemd, Nginx)'
ssh @sshOpts $destino "find /tmp/deploy -type f -exec sed -i 's/\r$//' {} + && sudo bash /tmp/deploy/instalar_en_ec2.sh $Ip"
Revisar 'instalación remota'

Write-Host "`nAplicación publicada en: http://$Ip" -ForegroundColor Green
