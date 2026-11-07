# Verificación de Moivo

Fecha: **9 de octubre de 2026**. Proyecto y APK de depuración reales.

## Entorno utilizado

- Windows 11, JDK 17.0.12, Gradle 8.11.1, Android Gradle Plugin 8.9.2.
- Android SDK 35, Build-Tools 35.0.0.
- Emulador dedicado **Moivo_API35**, Android 15 / API 35, imagen Google APIs x86_64.
- Emulador 37.2.12 con WHPX, sin ventana.
- **Sin API key de Google Maps**. No se ha comprobado la descarga de cartografía ni el marcador sobre el mapa integrado.

## Resultados obtenidos

| Comprobación | Resultado | Evidencia |
|---|---|---|
| Compilación de app y APK de pruebas | BUILD SUCCESSFUL | build-and-tests.log |
| Proyecto ZIP extraído en carpeta nueva | BUILD SUCCESSFUL; 36 tareas ejecutadas | archive-build.log |
| Firma del APK | Verificada con apksigner | apk-certificate.txt |
| Validaciones JUnit | 4 pruebas, 0 fallos, 0 errores | junit/TEST-pe.edu.moivo.domain.ValidationTest.xml |
| Room y repositorio con Robolectric | 5 pruebas, 0 fallos, 0 errores | junit/TEST-pe.edu.moivo.data.RepositoryTest.xml |
| Android Lint | 0 errores; advertencias no bloqueantes | lint-results-debug.txt |
| Recorrido instrumentado | OK (1 test) en emulador API 35 | instrumentation-flow.log |
| Ubicación concedida | OK (1 test), coordenadas de proveedor del emulador | instrumentation-location.log; location-granted.png |
| Terminación real del proceso y arranque en frío | Reserva FINALIZADA y cliente conservados | process-restart.txt; process-restarted.png; reopen-reservations.xml |
| Revisión visual | Pantallas de reserva, listado y ubicación inspeccionadas | Capturas PNG de esta carpeta |

Las advertencias de Lint corresponden a versiones más recientes de dependencias, textos españoles incrustados en Java y reglas de extracción de datos de Android 12. Las versiones están fijadas; no se ignoraron errores de compilación ni de Lint. La UI está diseñada en español y no incluye otras traducciones.

Las pruebas de Room comprobaron: precarga sin duplicados ni sobrescritura; DNI único; claves foráneas; tarifas históricas; cambios de estado válidos y rechazo de cambios con estado anterior obsoleto; conservación de cliente, reserva y estados al cerrar y reabrir la base.

El recorrido de UI probó: campos vacíos, contraseña incorrecta, acceso correcto, RecyclerView y detalle de paquete, validación de cliente, alta de cliente desde una reserva, selección automática, rotación, selector de fecha, cantidad inválida, total, creación de reserva, listado inmediato, detalle, CONFIRMADO, FINALIZADO, pantalla de encuentro, solicitud/denegación del permiso y reapertura de la tarea.

La prueba adicional de proceso ejecutó `am force-stop pe.edu.moivo` y después `am start -W -n pe.edu.moivo/.ui.LoginActivity`; el arranque fue COLD y la UI recuperó el cliente y el estado FINALIZADO. No se borraron datos entre esas acciones.

La ubicación fue recibida de **Google Play Services en un emulador**, con permiso concedido y GPS simulado. Esto verifica permisos, petición y presentación de coordenadas, **no una medición GPS física ni la cartografía de Google Maps**.

## Lista de verificación solicitada

Los tres estados utilizados son: **comprobado**, **implementado pendiente de prueba**, **pendiente de configuración externa**.

| Requisito | Estado | Alcance / pendiente |
|---|---|---|
| 1. Login demo con credenciales y validación | comprobado | Campos vacíos, contraseña incorrecta y login válido en emulador |
| 2. Paquetes en RecyclerView | comprobado | Catálogo y apertura de tarjeta |
| 3. Información de un paquete | comprobado | Nombre, precio, horario, inclusiones y encuentro |
| 4. Alta de cliente en Room | comprobado | Alta desde UI y persistencia; DNI duplicado probado en repositorio |
| 5. Cliente, paquete, fecha y personas | comprobado | Selección, fecha, total y validaciones en UI |
| 6. Guardar reserva y listado inmediato | comprobado | Reserva PENDIENTE visible sin refresco manual |
| 7. Detalle de reserva | comprobado | Datos relacionados y total |
| 8. PENDIENTE → CONFIRMADO → FINALIZADO | comprobado | UI, actualizaciones Room y reapertura de DB |
| 9. Google Maps integrado con encuentro | pendiente de configuración externa | Código SDK y marcador implementados; necesita API key, Maps SDK habilitado, facturación y restricciones correctas |
| 10. Solicitud y denegación de ubicación | comprobado | Diálogo del sistema y continuación sin ubicación |
| 10. Coordenadas actuales disponibles | comprobado | Proveedor real de Play Services con ubicación simulada del emulador |
| 10. Marcador de ubicación sobre cartografía | pendiente de configuración externa | Validar después de configurar Maps |
| 11. Datos conservados al cerrar y abrir | comprobado | Cierre/reapertura de DB y cierre forzado real del proceso |
| Precarga sin duplicados ni reemplazo | comprobado | Prueba de tres cargas y cambio preexistente conservado |
| Rotación de formulario | comprobado | Cliente/paquete preservados; reserva guardada correctamente |
| APK instalable | comprobado | Instalado y ejecutado en Android 15 |
| Permiso permanente denegado, GPS apagado y ausencia de Play Services | implementado pendiente de prueba | Ramas de manejo implementadas; seguir guion manual |
| Ubicación aproximada y GPS de teléfono físico | implementado pendiente de prueba | Aún no se probó en hardware físico |
| Compatibilidad en dispositivo API 26 y accesibilidad con texto grande | implementado pendiente de prueba | minSdk y Lint correctos; falta recorrido en esos entornos |
| Cierre de sesión y todos los enlaces externos/ajustes | implementado pendiente de prueba | Acciones conectadas; no incluidas en el recorrido automatizado |

## Repetir las pruebas

1. Abrir el proyecto y sincronizar, como indica el README.
2. Ejecutar `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest`.
3. Usar un emulador dedicado. Instalar `app/build/outputs/apk/debug/app-debug.apk` y `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk` con `adb install -r`.
4. Restablecer **solo la instalación de prueba** con `adb shell pm clear pe.edu.moivo` para que el permiso se encuentre sin conceder.
5. Ejecutar:
   ```text
   adb shell am instrument -w -r -e class pe.edu.moivo.FullFlowTest pe.edu.moivo.test/androidx.test.runner.AndroidJUnitRunner
   ```
6. Activar ubicación del emulador y enviar una coordenada con Extended Controls > Location o `adb emu geo fix -77.0348 -12.0517`.
7. Ejecutar:
   ```text
   adb shell am instrument -w -r -e class pe.edu.moivo.LocationTest pe.edu.moivo.test/androidx.test.runner.AndroidJUnitRunner
   ```
8. Alternativamente, con el emulador recién preparado y ubicación simulada, usar `.\gradlew.bat :app:connectedDebugAndroidTest`. En esta entrega se ejecutó directamente AndroidJUnitRunner por ADB; no se afirma haber generado el reporte HTML de connectedDebugAndroidTest.
9. Seguir `docs/PRUEBAS_MANUALES.md` para la cartografía, los casos pendientes y otros dispositivos.

El APK entregado no lleva API key. Añadir `MAPS_API_KEY` a `local.properties` requiere **recompilar y reinstalar**; el README identifica la ruta exacta y la configuración de Cloud.
