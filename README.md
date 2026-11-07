# Kusi Tours — reservas turísticas Android

Aplicación académica nativa en **Java 17 + XML**, con RecyclerView, ViewModel, LiveData, Room y Google Maps SDK for Android. Android mínimo: **8.0 / API 26**. Compila con SDK 35, AGP 8.9.2 y Gradle Wrapper 8.11.1.

## Requisitos de presentación incorporados

La aplicación inicia con **SplashActivity durante 5 segundos** y abre **MainActivity** con un Navigation Drawer: **Inicio, Nosotros, Contactos y Login**. Cada opción usa un Fragment independiente. Todas las Activities están fijadas en orientación vertical; el launcher muestra el nombre **Kusi Tours** y un icono adaptativo.

Los títulos usan **28 sp**, los párrafos y campos **24 sp**, y la altura de línea es **30 sp**. Las pantallas extensas permiten desplazamiento vertical.

- **Inicio:** presentación breve, acceso a gestión de viajes y consulta de salarios.
- **Nosotros:** presentación, misión, visión y valores de la empresa.
- **Contactos:** correo, teléfono, dirección, horario y formulario validado de nombre, correo, teléfono y mensaje. Prepara un correo en la aplicación de email del dispositivo; el usuario confirma el envío allí. También permite copiar la consulta. El borrador se conserva al cambiar de sección y al recrear la pantalla.
- **Login:** inicio y cierre de la sesión demo. Las Activities de gestión y salarios requieren esa sesión.
- **Consultar salarios:** formulario con cuatro trabajadores ficticios y su salario mensual base en soles. Accesible desde Inicio, Nosotros y Login después del acceso.

Los datos de contacto y salarios son **de demostración**, como se indica en pantalla. El destinatario `contacto@kusitours.example` no recibe mensajes; para un envío real, reemplazar los contactos en `app/src/main/res/values/company.xml`. Los trabajadores se editan en `app/src/main/java/pe/edu/moivo/data/Employee.java`.

Detalle de implementación y verificación: `docs/REQUISITOS.md`.

## Abrir y ejecutar

1. Abre esta carpeta (la que contiene `settings.gradle`) con **File > Open** en Android Studio.
2. Instala **Android SDK Platform 35**, **Build-Tools 35.0.0** y **Android SDK Platform-Tools** desde SDK Manager.
3. En **Settings > Build, Execution, Deployment > Build Tools > Gradle**, selecciona JDK **17** como Gradle JDK. También se puede ejecutar Gradle con JDK 21.
4. Android Studio genera `local.properties` con tu `sdk.dir`. La ruta local utilizada durante la entrega no es portable: deja que Studio la ajuste o reemplázala con tu ruta.
5. Sincroniza Gradle. La primera sincronización necesita Internet para descargar dependencias.
6. Configura Maps como se indica abajo. Sin clave se pueden probar login, clientes, reservas y estados; la pantalla explica la configuración faltante y permite abrir el punto en Google Maps externo.
7. Crea un emulador API 35 con imagen **Google APIs** o **Google Play**, o conecta un teléfono Android con Google Play Services.
8. Selecciona `app` y pulsa **Run**.

### Acceso de demostración

- Correo: **demo@kusitours.pe**
- Contraseña: **Kusi2026!**

El correo no distingue mayúsculas y se recortan espacios externos. La contraseña sí distingue mayúsculas. La sesión es una bandera local en SharedPreferences; **no es autenticación segura para producción**. Cerrar sesión conserva los clientes y reservas. No se envían credenciales a un servidor.

## Configuración exacta de Google Maps

En **Google Cloud Console**:

1. Crea o selecciona un proyecto y habilita la facturación exigida por Google Maps Platform.
2. Habilita **Maps SDK for Android**.
3. Crea una API key en **APIs & Services > Credentials**.
4. En restricciones de aplicación, selecciona **Android apps** y agrega:
   - Paquete: `pe.edu.moivo`.
   - SHA-1 del certificado usado para firmar la instalación.
5. Restringe la clave a **Maps SDK for Android**.
6. Obtén tu SHA-1 con `.\gradlew.bat :app:signingReport` (macOS/Linux: `./gradlew :app:signingReport`). Si instalas el APK entregado, usa el SHA-1 de `verification/apk-certificate.txt`; si recompilas con tu equipo, usa el de tu propio certificado.
7. En **`local.properties`, en la raíz de este proyecto**, añade una línea sin comillas:
   ```properties
   MAPS_API_KEY=TU_CLAVE_REAL
   ```
8. Sincroniza y vuelve a compilar/instalar. Un APK ya generado no incorpora una clave añadida después.

`app/build.gradle` lee esta propiedad (o la variable de entorno `MAPS_API_KEY`) y la inserta en el metadato `com.google.android.geo.API_KEY` de `app/src/main/AndroidManifest.xml`. No se incluye una clave de terceros. `local.properties` está excluido del control de versiones.

La integración usa **SupportMapFragment**, un marcador del encuentro y Google Maps real. El permiso de ubicación solo es necesario para “Mostrar mi ubicación”. Se aceptan ubicación precisa o aproximada. Se contempla denegación, denegación permanente, GPS desactivado, falta de Google Play Services y posición no disponible. La búsqueda espera hasta 20 segundos y rechaza ubicaciones de más de dos minutos. No se solicita ubicación en segundo plano.

Documentación oficial: [configuración de Maps](https://developers.google.com/maps/documentation/android-sdk/config), [API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key), [compatibilidad de AGP 8.9](https://developer.android.com/build/releases/agp-8-9-0-release-notes).

## Recorrido de demostración

1. Espera el splash de 5 segundos. Abre el menú lateral, revisa Nosotros y Contactos y entra en Login. Intenta ingresar con campos vacíos; aparecen errores. Ingresa las credenciales demo y pulsa Gestionar viajes.
2. En **Paquetes**, abre **Cusco esencial**. Consulta precio, horario, descripción, inclusiones y encuentro.
3. Pulsa **Reservar este paquete** y **Registrar cliente**.
4. Registra, por ejemplo, Ana Estudiante / 12345678 / ana@example.com / 987654321. El DNI es único; repetirlo produce un error y no reemplaza al cliente.
5. Al volver, el cliente recién guardado queda seleccionado. Elige fecha actual o futura y entre 1 y 30 personas.
6. Revisa el total y pulsa **Guardar reserva**. Se abre el listado actualizado, con estado **PENDIENTE**.
7. Abre la reserva y revisa cliente, paquete, fecha, personas, tarifa, total, encuentro y fecha de registro.
8. Pulsa **Confirmar reserva**, acepta y comprueba **CONFIRMADO**. Pulsa **Finalizar reserva**, acepta y comprueba **FINALIZADO**. El estado final no tiene otra transición.
9. Abre **Ver punto de encuentro**. Con clave válida, aparece el mapa de Google y el marcador. “Abrir en Google Maps” abre la app o el navegador.
10. Pulsa **Mostrar mi ubicación** y prueba tanto denegar como permitir. Denegar no impide reservar ni consultar el encuentro.
11. Fuerza el cierre, vuelve a abrir y revisa clientes y reservas. También prueba cerrar sesión e ingresar otra vez.

No hay compras, cobros reales ni disponibilidad de operadores turísticos: los paquetes, horarios y precios son datos académicos precargados.

## Arquitectura para sustentar

- `data/`: entidades, DAO, base Room, precarga y repositorio.
- `domain/Validation.java`: validación de clientes, reservas, login demo y transición de estados.
- `ui/`: Activities, adaptador RecyclerView, ViewModel de tareas, sesión y formatos.
- `res/layout/`: pantallas XML con View Binding.
- `app/schemas/`: esquema Room versionado.
- `src/test/`: validaciones y persistencia con JUnit/Robolectric.
- `src/androidTest/`: recorrido real de interfaz con Espresso/UI Automator.

Relaciones: **Cliente 1 → N Reserva N ← 1 Paquete**. Las reservas usan claves foráneas con RESTRICT. Cada reserva conserva una copia de la tarifa original. El dinero se guarda en **céntimos como long**, evitando errores de coma flotante. La fecha del viaje es ISO `yyyy-MM-dd` en calendario local.

La precarga usa IDs fijos y `INSERT IGNORE` dentro de una transacción: repetirla no duplica ni sobrescribe. No se usa migración destructiva. Las escrituras se ejecutan en un Executor fuera del hilo de UI y Room publica cambios mediante LiveData. TaskViewModel conserva las escrituras al rotar y bloquea dobles envíos. El cambio de estado compara el estado esperado en un UPDATE atómico.

Los datos sobreviven al cierre y reinicio de la app en el mismo dispositivo. **Desinstalar o borrar los datos de Android elimina la base**. No hay servidor ni sincronización entre dispositivos.

Distribución sugerida entre tres integrantes:

| Integrante | Responsabilidad | Demostración |
|---|---|---|
| 1 | Login, navegación, catálogo, RecyclerView y XML | Acceso, validaciones y detalle de paquetes |
| 2 | Room, clientes, reservas, reglas y estados | Crear datos, persistencia y pruebas |
| 3 | Maps, permisos, ubicación y entrega | Marcadores, permiso denegado, configuración y compilación |

## Compilar y probar por terminal

En PowerShell, desde esta carpeta:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:signingReport
```

En macOS/Linux usa `chmod +x gradlew` una vez y sustituye `.\gradlew.bat` por `./gradlew`.

APK: `app/build/outputs/apk/debug/app-debug.apk`.
Instalación: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
Pruebas locales: `app/build/reports/tests/testDebugUnitTest/index.html`.
Lint: `app/build/reports/lint-results-debug.html`.
Pruebas en dispositivo: `app/build/reports/androidTests/connected/debug/index.html`.

**Las pruebas instrumentadas limpian los datos de la instalación de prueba**. Ejecútalas en un emulador dedicado y con el permiso de ubicación sin conceder para comprobar el diálogo. `adb shell pm clear pe.edu.moivo` restablece esa instalación si necesitas repetir el recorrido desde cero.

Consulta **`verification/VERIFICACION.md`** para los resultados efectivamente obtenidos, las evidencias y la lista de verificación con tres estados.
