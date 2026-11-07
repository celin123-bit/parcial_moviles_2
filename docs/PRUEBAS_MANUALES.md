# Guion de aceptación

Usar un emulador dedicado. Comprobar en Android Studio tanto API 26 como API 35 y en un teléfono con Google Play Services si se dispone de él.

| Caso | Acción | Resultado esperado |
|---|---|---|
| Login vacío | Ingresar sin valores | Errores de correo y contraseña |
| Credenciales erróneas | Correo demo y contraseña distinta | No abre el catálogo |
| Login correcto | Credenciales del README | Catálogo con tres paquetes |
| Detalle | Tocar cada tarjeta | Nombre, destino, precio, duración, descripción, inclusiones y encuentro |
| Cliente vacío | Guardar formulario vacío | Errores en campos |
| DNI duplicado | Registrar dos veces un mismo DNI | Segundo registro rechazado, primero conservado |
| Lista clientes | Registrar y volver | Cliente visible inmediatamente; tocar abre sus datos |
| Sin selección | Guardar reserva sin cliente/paquete | Mensaje de selección obligatoria |
| Fecha pasada | Intentar elegir ayer | Calendario la bloquea; dominio también la rechaza |
| Cantidad | Vacío, 0, 31 | Rechazo; valores 1–30 aceptados |
| Total | Cambiar paquete/personas | Total de tarifa × personas |
| Reserva válida | Guardar | Listado actualizado con PENDIENTE |
| Doble toque | Pulsar guardar repetidamente | Una sola escritura en curso |
| Rotación | Girar durante el formulario y al guardar | Datos seleccionados conservados, sin duplicados |
| Confirmación | Confirmar una pendiente | CONFIRMADO en detalle y lista |
| Finalización | Finalizar una confirmada | FINALIZADO, sin botón para avanzar |
| Cierre de proceso | Forzar detención y abrir | Datos y estados conservados |
| Precarga repetida | Abrir varias veces | Tres paquetes, sin sobrescritura |
| Salir de sesión | Menú > Cerrar sesión | Login; datos conservados al ingresar |
| Mapa integrado | Clave válida y red | Google Maps interactivo y marcador correcto |
| Sin clave | Abrir mapa sin MAPS_API_KEY | Mensaje de configuración y alternativa externa operativa |
| Sin permiso | Denegar ubicación | Encuentro accesible, reservas operativas |
| Denegación permanente | Denegar repetidamente | Explicación y botón para ajustes |
| Permiso aproximado | Conceder solo aproximada | Posición cuando Google Play Services la entregue |
| Ubicación precisa | Conceder con GPS activo | Coordenadas, precisión y marcador azul |
| GPS apagado | Solicitar ubicación | Espera acotada, mensaje y posibilidad de abrir ajustes |
| Sin Internet | Crear cliente/reserva | Operaciones locales funcionando; mapas pueden no cargar |
| Volver del mapa | Retroceder | Reserva conservada sin modificar |
| Texto grande | Fuente a 150 % | Formularios con scroll y acciones accesibles |

Para comprobar muerte real de proceso sin borrar la base:

```powershell
adb shell am force-stop pe.edu.moivo
adb shell am start -n pe.edu.moivo/.ui.LoginActivity
```

Para simular GPS en el emulador: **Extended Controls > Location > Set location**. Por terminal (longitud primero):

```powershell
adb emu geo fix -77.0348 -12.0517
```

No usar `pm clear` ni desinstalar entre las dos acciones de una prueba de persistencia.
