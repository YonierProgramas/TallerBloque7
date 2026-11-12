# Evidencias de pruebas — SmartLibrary

**Integrante:** Yonier David Burbano  
**Fecha:** 12 de noviembre de 2026  
**Entorno:** Windows 11, terminal PowerShell de VS Code, Java 21.0.13 y Maven 3.10.0.  
**Aplicación:** Spring Boot con H2 en memoria, puerto 8080.

## Resultados funcionales

| Escenario | Solicitud | Resultado registrado | Soporte |
|---|---|---|---|
| Libro disponible | POST `/api/prestamos`, estudianteId=1, libroId=1 | **201 Created**; préstamo id=1 | Salida de terminal transcrita abajo |
| Libro ya prestado | Repetir POST con estudianteId=1, libroId=1 | **409 Conflict** | Resultado confirmado por el estudiante durante la ejecución; sin captura adjunta |
| Estudiante inexistente | POST con estudianteId=9999, libroId=2 | **404 Not Found** | [Captura original](03_estudiante_inexistente_404.png) |
| Libro inexistente | POST con estudianteId=1, libroId=9999 | **404 Not Found** | [Captura original](04_libro_inexistente_404.png) |
| Consulta de préstamos | GET `/api/prestamos` | **200 OK**; incluye préstamo id=1 | [Captura original](05_consulta_prestamos_200.png) |

Las capturas son las obtenidas durante la ejecución local. Los resultados enviados como texto se conservan como transcripciones; no se presentan como capturas. El contenido JSON de las respuestas 201 y 200 fue abreviado por el formato de tabla de PowerShell; estas evidencias no permiten contar toda la colección.

## Maven: nueve pruebas de integración

Comando ejecutado: `mvn clean test`.

Salida compartida desde la terminal, sin reconstruir un registro completo:

```text
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.431 s -- in co.edu.ucc.smartlibrary.PrestamoApiTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  13.757 s
[INFO] Finished at: 2026-11-12T09:56:12-05:00
[INFO] ------------------------------------------------------------------------
```

El test `impedirSegundoPrestamoActivoDevuelve409YNoCreaOtro` comprueba, además del 409, que la cantidad de préstamos permanezca en uno. Esta verificación pertenece a la suite automatizada, no a la captura abreviada de GET.

## Inicio de la aplicación

Fragmento de la salida real de `mvn spring-boot:run`:

```text
2026-11-12T09:57:10.291-05:00 INFO ... Tomcat started on port 8080 (http) with context path '/'
2026-11-12T09:57:10.296-05:00 INFO ... Started SmartLibraryApplication in 4.053 seconds (process running for 4.351)
```

Se abrevia únicamente el prefijo del logger. La salida también indicó `jdbc:h2:mem:smartlibrary`.

## Creación del préstamo: HTTP 201

Petición ejecutada:

```http
POST /api/prestamos
Content-Type: application/json

{"estudianteId":1,"libroId":1}
```

Fragmento de salida compartido (el texto de Content estaba truncado):

```text
StatusCode Content
---------- -------
       201 {"id":1,"estudianteId":1,"estudianteNombre":"Ana Gomez","libroId":1,"libroTitulo":"Ingenieria de...
```

## Repetición del préstamo: HTTP 409

Se volvió a enviar el mismo cuerpo del POST anterior. El resultado `Código HTTP: 409` fue confirmado durante la prueba. No se dispone de una captura original de esta ejecución. La suite automatizada aporta una comprobación adicional del rechazo y de la ausencia de un segundo registro.
