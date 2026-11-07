# Guion y Estructura de la Presentación — Moivo
**Archivo PowerPoint generado**: \Moivo_Presentacion.pptx\ (en la raíz del proyecto)
**Formato**: Panorámico 16:9, diseño corporativo moderno, capturas reales de Android 15.

---

## Diapositiva 1: Portada
- **Título**: Moivo — Sistema de Reservas Turísticas Nativo Android
- **Subtítulo**: Gestión integral de catálogos turísticos, clientes, reservas en céntimos y geolocalización con Google Maps SDK.
- **Badges Técnicos**: Java 17 • Android 15 (API 35 / Min 26) • Room SQLite • MVVM + LiveData • Google Maps SDK
- **Imagen incrustada**: Captura real enmarcada de la pantalla principal (kusi-home.png).
- **Notas del orador**: Buenas tardes. Presentamos Moivo, una aplicación móvil nativa Android para reservas turísticas construida en Java 17, arquitectura MVVM, persistencia local con Room SQLite y Google Maps SDK. Destaca por su alta tolerancia a fallos, precisión financiera y experiencia de usuario fluida.

---

## Diapositiva 2: Visión General y Propósito del Aplicativo
- **Contexto y Problemática**: Pérdida de reservas por registros manuales, errores de redondeo en cotizaciones, desinformación sobre puntos de partida y dependencia de Internet.
- **La Solución Moivo**: Operación Offline-First con Room SQLite, congelamiento histórico de precios, geolocalización con Google Maps y máquina de estados.
- **Pilares Técnicos**: Persistencia Confiable, Reactividad MVVM, Precisión Financiera, Resiliencia ante terminación de proceso.
- **Imagen incrustada**: Captura real de la vista de catálogo (kusi-home.png).

---

## Diapositiva 3: Arquitectura de Software y Patrón MVVM
- **Capa UI (Presentación)**: Activities nativas, ViewBinding (seguridad de tipos), RecyclerView y diálogos Material 3.
- **Capa ViewModel**: TaskViewModel, LiveData reactivo, desacoplamiento del ciclo de vida y prevención de envíos dobles ('debounce').
- **Capa Dominio**: Validation.java con reglas de negocio puras (sin dependencias del SDK de Android), validación estricta de DNI, teléfono y fechas.
- **Capa Datos y Persistencia**: MoivoDatabase (Room ORM), consultas DAO con LiveData observable, Repository y ExecutorService en segundo plano.

---

## Diapositiva 4: Catálogo de Experiencias Precargadas
- **Cusco Esencial** (Cusco): 1 día (08:00 a 17:00), S/ 180.00 / pers. Encuentro: Plaza de Armas de Cusco.
- **Paracas y Huacachina** (Ica): 1 día (06:00 a 20:00), S/ 240.00 / pers. Encuentro: Plaza de Armas de Ica.
- **Lima Histórica** (Lima): Medio día (09:00 a 13:00), S/ 95.00 / pers. Encuentro: Plaza San Martín.
- **Imágenes incrustadas**: Fotografías reales de cada destino (photo_cusco.jpg, photo_ica.jpg, photo_lima.jpg).

---

## Diapositiva 5: Gestión de Clientes y Reglas de Validación
- **Validaciones Estrictas**:
  - Nombre: 3 a 80 caracteres.
  - DNI Peruano: 8 dígitos numéricos exactos, índice UNIQUE en Room (rechaza duplicados).
  - Correo Electrónico: RFC-compliant regex, máximo 120 caracteres.
  - Teléfono: 9 dígitos numéricos exactos.
- **Esquema Room Client**: @Entity(tableName = 'clients', indices = {@Index(value = 'document', unique = true)}).
- **Ficha Rápida**: Modal con DNI, correo, teléfono y botón directo 'Crear reserva'.

---

## Diapositiva 6: Motor de Reservas y Precisión Monetaria
- **Almacenamiento en Céntimos (long)**: Elimina pérdida de precisión por redondeo de coma flotante (float/double).
- **Tarifa Histórica Congelada**: unitPriceCents guardado en la reserva para aislarla de cambios de precio futuros.
- **Selección de Personas**: Rango 1 a 30 pasajeros con cálculo en tiempo real.
- **Bloqueo de Fechas Pasadas**: minDate en DatePickerDialog y validación en Validation.java.
- **Integridad Referencial**: ForeignKey.RESTRICT entre Cliente, Reserva y Paquete.

---

## Diapositiva 7: Ciclo de Vida y Máquina de Estados de Reservas
- **Flujo Unidireccional**: PENDIENTE (ámbar) → CONFIRMADO (azul) → FINALIZADO (verde).
- **Transacción Atómica**: UPDATE reservations SET status = :nextStatus WHERE id = :id AND status = :expectedStatus.
- **Imagen incrustada**: Captura real de la reserva finalizada (flow-finalizado.png).

---

## Diapositiva 8: Geolocalización y Google Maps SDK
- **Integración con SupportMapFragment**: Marcador oficial en coordenadas de salida.
- **Gestión Proactiva de Permisos**: ACCESS_FINE_LOCATION y ACCESS_COARSE_LOCATION.
- **Denegación Permanente**: Diálogo explicativo con redirección hacia Ajustes del Sistema de Android.
- **Fallback Inteligente**: Botón 'Abrir en Google Maps' para abrir en la app externa o navegador.
- **Imagen incrustada**: Captura real con coordenadas GPS obtenidas del emulador (location-granted.png).

---

## Diapositiva 9: Resiliencia, Persistencia y Supervivencia de Proceso
- **Prueba de Cold-Start**: 'am force-stop pe.edu.moivo' seguido de arranque en frío. La base SQLite recuperó 100% de datos y estados.
- **Rotación de Pantalla**: onSaveInstanceState conserva cliente, paquete y fecha en el formulario.
- **Imagen incrustada**: Captura real de la reapertura tras force-stop (process-restarted.png).

---

## Diapositiva 10: Aseguramiento de Calidad y Resultados de Pruebas
- **Pruebas Unitarias JUnit**: 4 ejecutadas, 0 fallos (ValidationTest.java).
- **Pruebas con Robolectric**: 5 ejecutadas, 0 fallos (RepositoryTest.java).
- **Pruebas Instrumentadas**: Flujo de interfaz completo (FullFlowTest.java).
- **Pruebas de Ubicación**: Proveedor de Play Services (LocationTest.java).
- **Android Lint**: 0 errores bloqueantes.

---

## Diapositiva 11: Distribución del Equipo para la Sustentación
- **Integrante 1 (UX/UI y Navegación)**: Login, BottomNavigation, RecyclerView, Material 3. Demostración de acceso y catálogo.
- **Integrante 2 (Persistencia, Room y Dominio)**: SQLite, Validation.java, estados de reserva. Demostración de alta de cliente y avance de estados.
- **Integrante 3 (Google Maps, Permisos y Build)**: SupportMapFragment, permisos en runtime, build Gradle y firma APK. Demostración de ubicación y fallback.

---

## Diapositiva 12: Conclusiones y Hoja de Ruta Futura
- **Conclusiones**: Arquitectura limpia, precisión monetaria sin fallas, resiliencia total ante eventos del sistema operativo.
- **Roadmap Futuro**: Backend en la nube (JWT / REST), pasarela de pagos (MercadoPago / Culqi / Yape), notificaciones push (FCM) y vouchers PDF.
