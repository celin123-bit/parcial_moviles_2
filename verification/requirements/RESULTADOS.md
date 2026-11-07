# Verificacion final — 31/10/2026

- Compilacion APK: **correcta** con Gradle 8.11.1 y JDK 21, codigo fuente Java 17.
- Pruebas locales: **16 aprobadas**, 0 fallos. Incluyen 7 pruebas nuevas y 9 existentes.
- Lint: **0 errores**, 77 avisos (dependencias, orientacion y recomendaciones de interfaz, entre otros).
- Emulador Android 15 / API 35, sesion temporal con `-read-only`.
- `RequirementsFlowTest`: **OK (1 test)**, 23.665 s. Drawer, Fragments, borrador, login, salarios, retrato y cierre de sesion.
- `FullFlowTest`: **OK (1 test)**, 29.508 s. Clientes, reservas, estados, permiso de ubicacion denegado y persistencia.
- Las pruebas instrumentadas finales se ejecutaron directamente con `adb shell am instrument`; el reporte de `connectedDebugAndroidTest` en build corresponde a una ejecucion anterior. El resultado final de reservas esta en `full-flow-result.txt`.
- Capturas revisadas de Inicio, Drawer, Contactos y Salarios; capturas adicionales en `files/` y `splash.png`.
- El temporizador se prueba con reloj controlado: no navega antes de 5000 ms, pausa en segundo plano y conserva el tiempo restante al recrearse.
- Contactos y salarios son ejemplos solicitados por el usuario. El formulario prepara un correo y no afirma que se haya enviado. No se envio ningun mensaje real.

APK: `artifacts/kusi_tours/Kusi_Tours_Actualizado.apk`

SHA-256: `d637635b8e3bf4109b9e7d2c43c113ec33b956878604ab7754f0607bb4c50a65`
