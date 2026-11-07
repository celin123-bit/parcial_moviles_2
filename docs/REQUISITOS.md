# Requisitos incorporados — Kusi Tours

La aplicación conserva la gestión de paquetes, clientes, reservas y mapas e incorpora las secciones solicitadas.

| Requisito | Implementación |
|---|---|
| Orientación vertical | `android:screenOrientation="portrait"` en todas las Activities del manifiesto. |
| Icono y nombre de presentación | Nombre **Kusi Tours**, icono adaptativo de montañas y sol en `mipmap-anydpi-v26/ic_launcher.xml`. |
| Títulos 28, párrafos 24, interlineado 30 | Dimensiones en `res/values/dimens.xml`, estilos compartidos y `Typography.java` para la altura de línea desde Android 8. |
| Splash de 5 segundos | `SplashActivity`: temporizador no bloqueante, conserva el tiempo restante al recrearse y se pausa en segundo plano. Al terminar abre `MainActivity` y se elimina del historial. |
| Presentación de la empresa | `HomeFragment` y `AboutFragment`: introducción, misión, visión y valores. |
| Contactos y formulario | `ContactFragment`: correo, teléfono, dirección y horario; valida nombre, correo, teléfono de 9 dígitos y mensaje de 10 a 1000 caracteres. |
| Consulta de salarios | `SalaryActivity`: selección de trabajador y consulta de salario mensual base en soles; exige sesión demo. |
| Diferentes Activities | Splash, principal, login, salarios y las Activities existentes de viajes, clientes, reservas y mapa. |
| Navigation Drawer | `MainActivity` + `DrawerLayout` + `NavigationView`, con Inicio, Nosotros, Contactos y Login. |
| Fragments para el Drawer | `HomeFragment`, `AboutFragment`, `ContactFragment` y `LoginFragment`. El borrador de contacto se conserva al cambiar de sección y al recrear la Activity. |

## Probar la aplicación

1. Instala el APK y abre **Kusi Tours**.
2. Observa el splash durante 5 segundos.
3. Abre el botón de menú y recorre Inicio, Nosotros y Contactos.
4. En Contactos, prueba campos vacíos y luego completa nombre, correo, teléfono y mensaje.
5. **Enviar por correo** prepara la consulta en una aplicación de correo instalada. El envío lo confirma el usuario en esa aplicación. Si no hay un cliente de correo, se informa del problema y se conservan los datos. **Copiar consulta** permite copiar los datos validados.
6. En Login, ingresa `demo@kusitours.pe` y `Kusi2026!`.
7. Pulsa **Consultar salarios**, elige un trabajador y pulsa **Ver salario**.
8. Desde **Gestionar viajes** puedes utilizar los paquetes, clientes, reservas y mapas anteriores.
9. En Login, cierra sesión. Los clientes y las reservas permanecen guardados.

## Datos de ejemplo

El usuario eligió contactos y trabajadores de demostración. En pantalla se indica que son ficticios.

- Correo: `contacto@kusitours.example` (no recibe mensajes).
- Teléfono: +51 900 000 000.
- Dirección: Av. de los Viajeros 123, Cusco, Perú.
- Horario: lunes a viernes, 9:00 a 18:00.

| Trabajador | Cargo | Salario mensual |
|---|---|---:|
| Ana Quispe | Guía de turismo | S/ 2500.00 |
| Luis Mamani | Asesor de viajes | S/ 2200.00 |
| Rosa Flores | Coordinadora de operaciones | S/ 3200.00 |
| Diego Torres | Asistente administrativo | S/ 1800.00 |

Son sueldos base de ejemplo, sin descuentos ni bonificaciones. No se implementa una planilla legal ni una conexión a un servidor. Para usar contactos reales, editar `app/src/main/res/values/company.xml`; para cambiar trabajadores, editar `app/src/main/java/pe/edu/moivo/data/Employee.java`.

## Verificación

- `RequirementsTest`: tiempo del splash, pausa, recreación, navegación entre los cuatro Fragments, conservación del borrador, validación y preparación del correo, acceso a salarios y conservación de la consulta.
- Se mantienen las pruebas existentes de validación y persistencia.
- `RequirementsFlowTest`: recorrido en emulador de las cuatro secciones, copia del formulario, recreación, login, salarios, orientación vertical y cierre de sesión.
- `FullFlowTest`: recorrido de clientes, reservas, estados, mapa y persistencia. Actualizado para desplazarse hasta los controles con la tipografía grande.
- Capturas en `verification/requirements/files/`.
- Resultados finales en `verification/requirements/RESULTADOS.md`.

La sesión sigue siendo una demostración local, como en el proyecto original. Android 12 y versiones posteriores también muestran brevemente su animación de lanzamiento del sistema antes de la presentación de cinco segundos.

Referencias oficiales de implementación: [Fragments](https://developer.android.com/guide/fragments/create), [DrawerLayout](https://developer.android.com/reference/androidx/drawerlayout/widget/DrawerLayout) y [pantalla de lanzamiento de Android](https://developer.android.com/develop/ui/views/launch/splash-screen).
