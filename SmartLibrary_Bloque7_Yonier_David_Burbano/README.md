# SmartLibrary — Taller práctico Bloque 7

**Integrante único: Yonier David Burbano**  
Diseño de Software · Universidad Cooperativa de Colombia · 12 de noviembre de 2026.

API REST para registrar y consultar préstamos. Java 21, Spring Boot 3.5.16, Maven, Spring Web, Spring Data JPA, Validation y H2.

## Entrega

- [Informe final](docs/Entrega_SmartLibrary_Bloque7.pdf): análisis inicial, tabla de responsabilidades, UML por capas, secuencia con los dos caminos, resultados y decisiones de diseño en una página.
- [Diagramas individuales](docs/diagramas): únicamente los cuatro PNG requeridos para el modelo inicial, el modelo por capas y los dos caminos de la secuencia.
- [Evidencias reales de ejecución](evidencias/REGISTRO_DE_PRUEBAS.md): transcripciones y tres capturas originales.
- `src/` y `pom.xml`: implementación y nueve pruebas de integración.

**Resultado registrado:** 9 pruebas ejecutadas, 0 fallos, 0 errores, 0 omitidas; `BUILD SUCCESS`. Las pruebas HTTP registraron 201, 409, 404, 404 y 200. El registro de evidencias distingue las capturas, transcripciones y confirmaciones disponibles.

## Abrir y ejecutar en VS Code

Abre esta carpeta completa, la que contiene `pom.xml`, y utiliza una terminal PowerShell. Si aún está ejecutándose la copia anterior de SmartLibrary, detén esa ejecución con `Ctrl+C` antes de iniciar esta.

En el equipo utilizado para las pruebas, Java 21 y Maven se habilitaron solo para la terminal de trabajo. Al abrir una terminal nueva, prepara el entorno con:

```powershell
$jdk21 = Get-ChildItem "$env:ProgramFiles\Eclipse Adoptium" -Directory -Filter 'jdk-21*' |
    Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $jdk21) { throw 'No se encontró Temurin JDK 21.' }
$mavenBin = Join-Path $env:USERPROFILE 'herramientas\apache-maven-3.10.0\bin'
if (-not (Test-Path (Join-Path $mavenBin 'mvn.cmd'))) { throw 'No se encontró Maven en la ruta instalada.' }
$env:JAVA_HOME = $jdk21.FullName
$env:Path = "$env:JAVA_HOME\bin;$mavenBin;$env:Path"
mvn -version
```

Este bloque reutiliza las instalaciones existentes y solo afecta a la terminal actual. En otro equipo basta con tener JDK 21 y Maven configurados.

Desde la carpeta que contiene `pom.xml`:

```powershell
mvn clean test
mvn spring-boot:run
```

Mantén abierta la terminal del servidor. La API utiliza `http://localhost:8080`.

## Operaciones y datos iniciales

| Método | Ruta | Resultado |
|---|---|---|
| POST | `/api/prestamos` | 201 al crear; 400 si el cuerpo es inválido; 404 si falta estudiante/libro; 409 si no se puede prestar |
| GET | `/api/prestamos` | 200 y lista de préstamos |

Cuerpo del registro:

```json
{"estudianteId":1,"libroId":1}
```

`data.sql` carga estudiantes 1 (Ana Gomez) y 2 (Luis Mora); libros 1 y 2 disponibles y libro 3 no disponible. No hay préstamos precargados. **H2 está en memoria: los préstamos se reinician al detener y volver a iniciar la aplicación.**

Para repetir los cinco escenarios, importa `postman/SmartLibrary_Bloque7.postman_collection.json` y ejecútala en orden después de un arranque limpio. No es necesario usar Postman para ejecutar las nueve pruebas Maven.

## Organización

```text
src/main/java/co/edu/ucc/smartlibrary/
  controller/   Entrada y salida HTTP
  service/      Reglas y coordinación transaccional
  repository/   Interfaces de acceso a datos JPA
  model/        Estudiante, Libro y Prestamo
  dto/          Entrada y salida de la API
  exception/    Respuestas de error 400, 404 y 409
src/test/       PrestamoApiTest (9 pruebas)
```

El taller se limita al registro y consulta de préstamos. Los estudiantes y libros ya están registrados; no se agregan registro de usuarios, autenticación ni endpoints de devolución. `fechaDevolucion = null` identifica un préstamo activo.

El código fuente, las pruebas, las dependencias y los datos iniciales se conservan respecto de la versión que fue ejecutada. El informe y el registro de evidencias están actualizados. `.gitignore` excluye `target/`, archivos temporales y configuraciones locales al subir el repositorio a GitHub.
