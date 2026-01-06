$ErrorActionPreference = "Stop"

# 0. Habilitar TLS 1.2 (Soluciona error de descarga)
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

# 1. Comprobar Java y Version
if (-not (Get-Command "java" -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR CRITICO: NO SE ENCUENTRA JAVA." -ForegroundColor Red
    Write-Host "Necesitas instalar Java JDK 17 o superior." -ForegroundColor Red
    Read-Host "Presione Enter para salir..."
    exit
}

# Relax error preference temporarily because java -version outputs to stderr
$oldPreference = $ErrorActionPreference
$ErrorActionPreference = "Continue"
$javaVerInfo = java -version 2>&1 | Out-String
$javaVerInfo = java -version 2>&1 | Out-String
$ErrorActionPreference = $oldPreference

# Intentar buscar Java 17 manualmente si la version global es vieja
if ($javaVerInfo -match '"1\.8' -or $javaVerInfo -match '"1\.7') {
    Write-Host "Java global es antiguo. Buscando instalaciones de Java 17+..." -ForegroundColor Yellow
    
    $possiblePaths = @(
        "C:\Program Files\Java\jdk-17*\bin\java.exe",
        "C:\Program Files\Eclipse Adoptium\jdk-17*\bin\java.exe",
        "C:\Program Files\Microsoft\jdk-17*\bin\java.exe"
    )
    
    $foundJava = $null
    foreach ($path in $possiblePaths) {
        $matches = Get-Item $path -ErrorAction SilentlyContinue
        if ($matches) {
            $foundJava = $matches[0].FullName
            break
        }
    }
    
    if ($foundJava) {
        Write-Host "Encontrado Java 17 en: $foundJava" -ForegroundColor Green
        $env:JAVA_HOME = (Get-Item $foundJava).Directory.Parent.FullName
        $env:PATH = "$((Get-Item $foundJava).Directory.FullName);$env:PATH"
        Write-Host "Entorno actualizado para usar esta version." -ForegroundColor Cyan
    } else {
        Write-Host "-------------------------------------------------------" -ForegroundColor Red
        Write-Host "ERROR: TIENES UNA VERSION ANTIGUA DE JAVA (Java 8/1.8)" -ForegroundColor Red
        Write-Host "-------------------------------------------------------" -ForegroundColor Red
        Write-Host "El enlace anterior fallo. Prueba este nuevo enlace mas fiable:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host ">>> https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "1. Entra al enlace."
        Write-Host "2. Descarga el archivo .msi para Windows (x64)."
        Write-Host "3. Instalalo y asegúrate de marcar 'Set JAVA_HOME variable' si te pregunta."
        Write-Host ""
        Read-Host "Presione Enter para abrir la web..."
        Start-Process "https://adoptium.net/temurin/releases/?version=17"
        exit
    }
}

# 2. Definir version y URLs
$mavenVersion = "3.9.6"
# Usar el archivo oficial estable (menos problemas de mirrors)
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$installDir = "$PSScriptRoot\maven_portable"
$mavenBinDir = "$installDir\apache-maven-$mavenVersion\bin"
$mvnCommand = "$mavenBinDir\mvn.cmd"

# 3. Descargar Maven si no existe
if (-not (Test-Path $mvnCommand)) {
    Write-Host "Maven no encontrado. Descargando version portable..." -ForegroundColor Yellow
    
    # Crear directorio
    New-Item -ItemType Directory -Force -Path $installDir | Out-Null
    
    # Descargar
    $zipFile = "$installDir\maven.zip"
    
    $downloaded = $false
    
    # Metodo 1: PowerShell Nativo
    try {
        Write-Host "Intento 1: Descarga estandar..." -ForegroundColor Gray
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $mavenUrl -OutFile $zipFile -ErrorAction Stop
        $downloaded = $true
    } catch {
        Write-Host "Fallo metodo estandar. Error: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    
    # Metodo 2: CURL (Windows 10/11 lo suele tener pre-instalado)
    if (-not $downloaded) {
        Write-Host "Intento 2: Usando CURL..." -ForegroundColor Gray
        try {
            curl.exe -L -o "$zipFile" "$mavenUrl"
            if (Test-Path $zipFile) { 
                if ((Get-Item $zipFile).Length -gt 1000) { $downloaded = $true }
            }
        } catch {
            Write-Host "Fallo CURL." -ForegroundColor Yellow
        }
    }

    if (-not $downloaded) {
        Write-Host "-------------------------------------------------------" -ForegroundColor Red
        Write-Host "ERROR: NO SE PUDO DESCARGAR MAVEN AUTOMATICAMENTE" -ForegroundColor Red
        Write-Host "-------------------------------------------------------" -ForegroundColor Red
        Write-Host "Parece que hay un bloqueo de red o firewall."
        Write-Host ""
        Write-Host "SOLUCION MANUAL:"
        Write-Host "1. Descarga este archivo: $mavenUrl"
        Write-Host "2. Copialo en esta carpeta: $installDir"
        Write-Host "3. Renombralo a 'maven.zip'"
        Write-Host "4. Ejecuta de nuevo el script."
        Write-Host ""
        Read-Host "Presione Enter para salir..."
        exit
    }
    
    # Descomprimir
    Write-Host "Descomprimiendo Maven..." -ForegroundColor Yellow
    Expand-Archive -Path $zipFile -DestinationPath $installDir -Force
    
    # Limpiar zip
    Remove-Item $zipFile
    
    Write-Host "Maven descargado correctamente." -ForegroundColor Green
} else {
    Write-Host "Usando Maven portable existente." -ForegroundColor Cyan
}

# 4. Establecer JAVA_HOME si falta (intento basico)
if (-not $env:JAVA_HOME) {
    # Intentar adivinar, aunque 'java' este en el path
    Write-Host "Advertencia: JAVA_HOME no esta definida. Maven podria quejarse." -ForegroundColor Yellow
}

# 5. Ejecutar la APP usando el camino absoluto
Write-Host "------------------------------------------------" -ForegroundColor Cyan
Write-Host "EJECUTANDO RENTAL APP..." -ForegroundColor Cyan
Write-Host "------------------------------------------------" -ForegroundColor Cyan

# Usar el operador de llamada '&' para rutas con espacios/variables
& $mvnCommand clean javafx:run

if ($LASTEXITCODE -ne 0) {
    Write-Host "Hubo un error al ejecutar la aplicacion." -ForegroundColor Red
}

Write-Host "Presione Enter para salir..."
Read-Host
