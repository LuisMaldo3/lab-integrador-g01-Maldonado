# PROMPTS.md — Tienda Tecsup · Fase 2: Mejora con IA

**Fecha del trabajo:** 9 de noviembre de 2026  
**Proyecto base:** `MIERCOLES (2).zip`  
**Tecnologías:** Kotlin, Jetpack Compose, Material 3, MVVM, Retrofit, Room, Firebase Authentication y Firestore  
**Rama de entrega:** `mejora-ia` — **un solo commit de interfaz**  
**Alcance:** aplicación de consulta y comparación de smartphones. **No hay pagos, carrito ni envíos**.

## 0. Alcance y registro de trabajo con IA

Este documento reúne **en un solo archivo** el prompt maestro detallado de la interfaz solicitado por el usuario, las instrucciones de las iteraciones anteriores y una descripción del código incorporado al proyecto para la Fase 2. Sirve como bitácora de la mejora con asistencia de IA y como prompt reutilizable.

**Herramienta:** ChatGPT (GPT-6), asistencia para análisis y generación de código Android.  
**Solicitudes del usuario resumidas fielmente:**

1. Rediseñar Login, Registro y Cuenta Creada tomando como referencia las tres pantallas azules con teléfonos grandes; mantener el azul marino/celeste, botones legibles, espacios equilibrados y sin autenticación con Google.
2. Rediseñar el Catálogo siguiendo una referencia azul y violeta con banner, buscador, marcas, fotografías de teléfonos y tarjetas a dos columnas.
3. Poner imágenes diferentes en los tres banners: iPhone, Samsung y dos teléfonos, con archivos separados y `R.drawable` distintos.
4. Mejorar el Detalle, porque la ficha original se veía básica: fotografía mucho más grande, galería interactiva, zoom, características, productos relacionados y comparar.
5. Ampliar Favoritos y Administración, agregar Perfil y Comparador y mejorar navegación, filtros y estados sin conexión, sin pagos ni envíos.
6. Mantener las seis pruebas existentes del ViewModel de lista y preparar toda la mejora de interfaz para **un único commit en `mejora-ia`**.

### Resultado de la asistencia incorporado al código fuente

- `ui/lista/ListaScreen.kt`: mejor cabecera, tres banners, filtros por marca, búsqueda, rango de precio, ordenamiento y guía por presupuesto; tarjetas con fotografías reales de la API y estados sin resultados.
- `ui/detalle/DetalleScreen.kt`: galería grande de 323dp, miniaturas táctiles, foto ampliada con gestos, compartir por Android, favorito real, ficha jerarquizada, descripción expandible y modelos similares obtenidos de Room.
- `ui/detalle/DetalleViewModel.kt`: flujo observable de favoritos y catálogo, estado de detalle y fallback al modelo básico guardado cuando la API no responde.
- `ui/comparador/CompararScreen.kt` y `CompararViewModel.kt`: selección observable de hasta tres productos reales, selección inicial desde Detalle, tabla de precio, marca, categoría y otros datos solo cuando están disponibles, quitar y vaciar selección.
- `ui/favoritos/FavoritosScreen.kt`: búsqueda, ordenamiento, estados vacíos y acceso a comparar.
- `ui/perfil/PerfilScreen.kt`: correo y rol reales, accesos a Favoritos, Comparar, Admin solo para el rol correspondiente, cierre de sesión y cambio de apariencia.
- `data/preferences/PreferenciasRepository.kt` y `MainActivity.kt`: persistencia local del modo Sistema/Claro/Oscuro mediante SharedPreferences y aplicación del tema al recomponer.
- `ui/admin/AdminScreen.kt` y `AdminViewModel.kt`: dashboard de métricas locales, distribución por marcas, consulta del catálogo, sincronización y limpieza de caché con confirmación.
- `ui/navigation/AppNav.kt`, `ui/ViewModelFactory.kt` y `AppContainer.kt`: nuevas rutas, factories e inyección de preferencias.
- Autenticación: se conservan Login, Registro y Recuperación Firebase; se refinan el tamaño del recurso del hero, Cuenta Creada y la confirmación de recuperación.
- Se mantienen **sin modificar las seis pruebas unitarias de `ListaViewModel`** y sus dobles de prueba.

### Límites reales de la entrega

- Las ilustraciones PNG de los banners son renderizados fotorrealistas incluidos en el proyecto, **no fotografías oficiales de Apple/Samsung**.
- El catálogo de DummyJSON es **de solo lectura**. No se agregó CRUD remoto de productos, inventario editable, pedidos, ventas, pagos ni entregas.
- Room guarda título, imagen, subtítulo, precio y favoritos. El detalle online obtiene campos extendidos; offline muestra el registro básico disponible. Comparar también se limita a lo que está guardado localmente.
- Los textos de términos y privacidad todavía necesitan documentos legales reales; no se inventaron políticas.
- No se pudo compilar ni ejecutar el emulador en el entorno de elaboración: faltaban Gradle descargado y Android SDK. Se realizó revisión de estructura y sintaxis, no verificación binaria de funcionamiento. **Ejecutar Gradle en Android Studio antes de hacer el commit**.
- No se incluye `google-services.json` por seguridad. Para correr la app, colocar la configuración privada legítima en `app/google-services.json` en la máquina local.

### Verificación académica y commit único

La mejora visual debe quedar como **un solo commit de interfaz**. El usuario ya tiene un commit anterior con seis pruebas unitarias. `PROMPTS.md` puede registrarse por separado como commit de documentación. Para preparar la aplicación en la rama original `mejora-ia`:

```powershell
# En la carpeta ORIGINAL con el historial Git de la pareja
 git switch mejora-ia
 git status --short
 .\gradlew.bat :app:testDebugUnitTest
 .\gradlew.bat :app:assembleDebug
 # Tras corregir errores y revisar la UI, agregar solo cambios deseados
 git add -A app/src/main
 git add app/build.gradle.kts gradle/gradle-daemon-jvm.properties
 # Revisar git diff --cached --stat y comprobar que PROMPTS.md NO esté preparado
 git commit -m "feat(ui): renovar integralmente interfaz de Tienda Tecsup con IA"
 # En un segundo commit distinto, solamente la documentación
 git add PROMPTS.md
 git commit -m "docs: documentar prompts y mejoras realizadas con IA"
 git push origin mejora-ia
```

**Nota de comando:** `AppContainer.kt` vive dentro de `app/src/main/java/com/tecsup/productos/`. Revisa `git status --short` antes de cada commit. No añadir `google-services.json`, `local.properties` ni carpetas de compilación. El cambio local en `gradlew` debe revisarse antes de decidir incluirlo.

### Referencias visuales incluidas

- `referencias_visual/01_LOGIN_REGISTRO_EXITO.png`: composición visual original para autenticación.
- `referencias_visual/02_CATALOGO_PREMIUM.png`: composición visual original para catálogo.
- `app/src/main/res/drawable/`: imágenes de teléfonos, logo y banners que realmente carga Compose.

---

## 1. Prompt completo y detallado para reproducir o continuar el rediseño

El siguiente texto conserva las medidas, colores, márgenes, comportamientos y restricciones del prompt de rediseño. Algunas características son **criterios de diseño ideales**, mientras que la sección «Resultado incorporado» arriba describe lo que se programó efectivamente.

# PROMPT MAESTRO — TIENDA TECSUP · REDISEÑO PREMIUM INTEGRAL (FASE 2, MEJORA CON IA)

> **Especificación visual y funcional de esta entrega, reutilizable como prompt para revisiones futuras.** No es una petición de generar una fotografía ni un mockup aislado: el entregable debe ser un proyecto Kotlin/Compose **real, funcional, compilable**. Si solo puedes responder con código, entrega archivos completos, rutas y cambios comprobables; no inventes que compilaste.

## 00. MISIÓN Y CRITERIO DE ÉXITO

Actúa como arquitecto Android senior, especialista UI/UX, experto Jetpack Compose + Material 3, accesibilidad y arquitectura MVVM. Trabaja **DIRECTAMENTE EN EL PROYECTO ADJUNTO**, sin recrearlo desde cero, sin transformar la aplicación en web, sin cambiar el paquete y sin reemplazar datos reales por listas falsas.

**App:** Tienda Tecsup. **Package/applicationId:** `com.tecsup.productos`. **Proyecto base:** `MIERCOLES (2).zip`. **Curso:** Programación en Móviles, laboratorio 11-12, Fase 2 `mejora-ia`. **Objetivo:** transformación premium de TODAS las interfaces existentes y funciones complementarias de catálogo, comparación y perfil, sin pagos, carrito, boletas, compras ni envíos.

**DIRECCIÓN VISUAL APROBADA:** azul eléctrico + azul marino + celeste, con degradados solamente en los lugares estratégicos, superficies blancas/azul hielo, violetas en promociones. Iconografía limpia, profesional, consistente (Material Symbols/Lucide equivalente), sin iconos que parezcan generados arbitrariamente por IA. Imágenes GRANDES, FOTORREALISTAS, no teléfonos genéricos dibujados. La información real siempre predomina.

**FIDELIDAD VISUAL:** reproducir la composición, proporciones, alineaciones, jerarquía, amplitud de imágenes, separación y personalidad de las referencias incluidas. Las imágenes son la fuente principal para Login, Registro, Confirmación y Catálogo. Las dimensiones dp siguientes son metas de implementación a una referencia de **393 × 852 dp**; deberán adaptarse a 320–480 dp, pantallas altas/bajas, tamaño de letra aumentado, teclado abierto y barras de sistema. No emplear capturas de pantalla como fondo completo ni dibujar formularios falsos; cada elemento debe ser un Composable interactivo real. **La igualdad absoluta píxel a píxel no es posible en tamaños diferentes**, pero maximiza la fidelidad en la pantalla de referencia y conserva los flujos de uso.

### ARCHIVOS DE REFERENCIA QUE DEBES ABRIR ANTES DE PROGRAMAR
1. `referencias_visual/01_LOGIN_REGISTRO_EXITO.png` = tres pantallas: Login, Registro, Cuenta creada. Usar composición original, no improvisar otro diseño.
2. `referencias_visual/02_CATALOGO_PREMIUM.png` = Catálogo azul/violeta con banner de teléfonos, chips por marca, tarjetas dos columnas, navegación inferior.
3. `app/src/main/res/drawable/` = imágenes que realmente existen en el proyecto: `hero_login_telefonos.png`, `hero_registro_telefonos.png`, `logo_tienda_tecsup_ajustado.png`, `iphone_pro_realista.png`, `samsung_galaxy_realista.png`, `banner_dos_telefonos.png`. Reutilizar antes de generar o buscar nuevas.
4. Si otra IA no puede abrir los PNG, pedir que el usuario los adjunte como imágenes, **sin sustituir su contenido por suposiciones**.

## 01. ALCANCE NEGOCIADO — UN ÚNICO COMMIT DE INTERFAZ

Implementar todo lo siguiente como **una sola unidad de trabajo**: diseño global, autenticación visual, catálogo, galería y detalle, favoritos, comparador, perfil, dashboard de administración, estados cargando/error/vacío/offline, navegación, recursos y pruebas de integración visual. **No crear commits parciales por pantalla**; una vez terminado, compilado y revisado, preparar un solo commit con mensaje:
`feat(ui): mejorar integralmente interfaz y experiencia de Tienda Tecsup con IA`

El commit previo de **seis pruebas unitarias de ListaViewModel** ya existe y **no debe perderse ni reescribirse**. `PROMPTS.md` puede llevarse en otro commit de documentación si lo pide el flujo académico. **No incluir pagos, envíos, órdenes, carrito, pasarelas de pago ni CRUD remoto ficticio.**

## 02. INVENTARIO REAL DEL PROYECTO — NO INVENTAR ARQUITECTURA

Actualmente existe:
- `app/src/main/java/com/tecsup/productos/ui/login/{LoginScreen,RegisterScreen,AccountCreatedScreen,RecuperarPasswordScreen,RecuperarPasswordExitoScreen}.kt`
- `ui/components/{PremiumUi,BannerCarousel,Cargando,ErrorView}.kt`
- `ui/lista/{ListaScreen,ListaViewModel}.kt`
- `ui/detalle/{DetalleScreen,DetalleViewModel}.kt`
- `ui/favoritos/{FavoritosScreen,FavoritosViewModel}.kt`
- `ui/admin/{AdminScreen,AdminViewModel}.kt`
- `ui/navigation/AppNav.kt`, `ui/ViewModelFactory.kt`, `ui/theme/{Color,Theme,Type}.kt`
- `data/auth/{AuthRepository,FirebaseAuthRepository}.kt`
- `data/remote/{ApiService,RetrofitClient, dto/ItemDto}.kt`
- `data/local/{ItemEntity,ItemDao,AppDatabase}.kt`
- `data/repository/{ItemRepository,ItemRepositoryImpl}.kt`
- `domain/model/{Item,Sesion}.kt`, `AppContainer.kt`, `Notificador*`.

Tecnologías que ya funcionan o están integradas: **Kotlin, Compose, Material 3, Coil 2.7.0, Navigation Compose, Coroutines, Firebase Auth + Firestore, Retrofit, Room, MVVM**. `ApiService` usa `GET products/category/{slug}` (categoría `smartphones`) y `GET products/{id}` de DummyJSON. Los roles se leen de Firestore `usuarios/{uid}`: ADMIN y USUARIO. La lista visible se observa desde **Room** mediante `ItemRepository.items`, Retrofit alimenta Room al sincronizar. Favoritos en Room, notificación local tras sincronizar. No introducir Firebase dentro de Composables ni repositorios concretos directamente en pantallas.

**Modelo `Item` real:** `id: Int`, `titulo`, `subtitulo`, `imagenUrl`, `precio: Double`, `esFavorito`, `descripcion?`, `marca?`, `categoria?`, `calificacion?`, `stock?`, `descuento?`, `imagenes`. **ATENCIÓN:** `ItemEntity` local guarda solamente id, título, subtítulo, URL, precio y favorito. Así que en catálogo cargado de Room muchos campos (rating, descuento, stock, marca) pueden ser nulos; la interfaz debe mostrar solo datos verificados y, si necesitas esos atributos en lista offline, ampliar explícitamente Room + mapper + migración/pruebas. No fingir que existen. **Precio viene de API en USD `$`, nunca convertir a `S/` por copiar cifras ilustrativas de una captura.**

## 03. SISTEMA DE DISEÑO: TOKENS EXACTOS

### 03.1 Paleta (crear/reutilizar nombres en `ui/theme/Color.kt`)

| Token | Hex | Uso |
|---|---|---|
| `BrandBlue` / `Azul` | `#0864F8` | CTA, seleccionados, enlaces |
| `BrandRoyal` | `#0756E8` | botón gradient/acento |
| `BrandDeepBlue` | `#0638AF` | degradado hero |
| `BrandNavy` / `Marino` | `#0B1D55` | titulares en azul oscuro |
| `BrandCyan` / `Celeste` | `#25B6FF` | reflejos, gradiente, ondas |
| `BrandSky` | `#73D7FF` | luces y gradientes |
| `BrandPurple` / `Violeta` | `#7E4EF6` | promoción secundarias |
| `BrandCoral` / `Coral` | `#FF526E` | CTA secundario en banners, descuento solo real |
| `BgLight` / `Nieve` | `#F6F8FF` | fondo general |
| `BgSoftBlue` / `Suave` | `#EAF2FF` | chips, fondos de iconos |
| `TextPrimary` / `Grafito` | `#101A37` | texto principal |
| `TextSecondary` | `#63718B` | textos de soporte |
| `StrokeLight` | `#DCE5F3` | líneas sutiles |
| `SuccessGreen` | `#16A34A` | éxito/disponibilidad real |
| `StarGold` | `#FFB700` | puntuación real |
| `PureWhite` | `#FFFFFF` | tarjetas, formularios |

**Gradiente Hero:** de `#0638AF` a `#0864F8` a `#25B6FF` en diagonal aprox. 135°. **Gradiente CTA:** horizontal `#0983FF` → `#003FD6`. **Banner iPhone:** `#071A46` → `#1259CB` → `#437CE8`. **Banner Samsung:** `#151043` → `#4A23B9` → `#2275DB`. **Banner general:** `#073B90` → `#1169E8` → `#18A4ED`. No introducir arcoíris, sombras rosas por toda la app ni 10 colores saturados en una vista.

### 03.2 Tipografía (escala recomendada)
- Familia `Roboto` o la tipografía sistema Material3 si no existe una licenciada incluida. No descargar archivos de fuente sin necesidad.
- Encabezado grande 30–32 sp, 36–39 sp de línea, peso ExtraBold (700–800).
- Título de pantalla 26–28 sp, 32–34 sp línea, Bold.
- Título sección 20–22 sp, 26–28 sp línea, Bold.
- Título tarjeta producto 14–16 sp, lineHeight 19–21, SemiBold, máximo 2 líneas.
- Precio destacado detalle 27–30 sp Bold; precio tarjeta 19–22 sp ExtraBold.
- Cuerpo 14–16 sp, lineHeight 20–24.
- Texto secundario 12–13 sp, línea 16–18.
- Chips y botón pequeño 12–14 sp, semibold.
- Evitar `fontSize` minúsculo 10sp para información importante. Permitir `fontScale >= 1.3` sin cortar acciones.

### 03.3 Rejilla y espaciado en dp
- Base de diseño móvil **393 × 852 dp**, escala adaptable (mínimo ancho 320 dp, objetivo 360–430 dp).
- Contenedor horizontal general **16 dp** de margen en catálogo/favoritos, **20–24 dp** en formularios de autenticación.
- Escala de espaciado reutilizable: **4 / 8 / 12 / 16 / 20 / 24 / 32 dp**.
- Separación entre tarjetas de catálogo **12 dp** horizontal y **14–16 dp** vertical.
- Separación entre títulos y contenido **12–16 dp**; entre secciones **20–28 dp**.
- Radio de tarjetas producto **20–22 dp**; banner **24–26 dp**; chips **50% / pill**; inputs y botones **16–18 dp**; paneles **22–24 dp**; miniaturas **10–12 dp**.
- Sombra de tarjeta producto: 2–4 dp, leve; no sombras negras densas. Borde claro de 1 dp cuando ayude contraste.
- Campos de texto **52–56 dp** de alto; botones primarios **54–56 dp** alto; área táctil mínima **48 × 48 dp**.
- Bottom bar con altura de contenido **68–76 dp** + `navigationBarsPadding()`, 4 acciones, indicador activo con pastilla azul clara.
- Respetar status bar/navegación del sistema con `WindowInsets`, `statusBarsPadding`, `navigationBarsPadding`, `imePadding`, evitar doble padding.
- En pantallas cortas/teclado: scroll de formulario, hero adaptativo `heightIn`, mantener CTA accesible. No forzar alturas combinadas que excedan pantalla. En tablets no estirar tarjetas a ancho infinito: `widthIn(max = 520.dp)` formulario, `maxWidth=1100.dp` grids.

### 03.4 Identidad de componentes
- Botón principal: fondo degradado azul, texto blanco 15–16sp Bold, radio 18 dp, alto 54 dp, sombra ligera, estado cargando con spinner.
- Botón secundario: blanco, borde 1dp `#CBDEFC`, icono azul, radio 18dp, 54dp.
- Campo texto: superficie blanca, borde `#DCE5F3` 1dp, radio 16dp, icono azul de 20–22dp, texto 15–16sp, hint `#63718B`, estados foco/borde azul, error visible bajo el campo, icono de ojo para contraseña.
- Chips: 38–42dp alto, borde gris suave; seleccionado azul con texto blanco; texto 12–14sp.
- Iconos: coherentes, grosor 1.8–2dp, 20–24dp; Material Icons, evitando iconos de “robot/IA”, emojis en controles formales, logos de marcas falsos.
- Animaciones: 180–280ms para fade/scale/selección; pager banner cada 5 s con pausa mientras se arrastra y cuando la pantalla no está visible. Respeto a preferencia de reducción de movimiento cuando sea posible.

## 04. PANTALLA A — INICIO DE SESIÓN (PARECIDA A REFERENCIA 01, PANEL IZQUIERDO)

**Objetivo visual:** reproducir esa composición clara: cabecera azul eléctrico con logo blanco/celeste en esquina superior izquierda y **tres smartphones reales gigantes superpuestos** centrados/derecha, corte inferior de onda blanca; cuerpo sobre blanco/hielo, título muy grande, subtítulo, dos campos, enlace, botones.

**Jerarquía por orden vertical** (en referencia 393 × 852dp; ajustar por altura real):
1. Lienzo fondo `#F6F8FF`; usar `Scaffold` sin bottom bar; contenido con scroll y `imePadding`.
2. Hero superior **280–310 dp** alto según dispositivo (alto usable), `fillMaxWidth`; gradiente `HeroGradient`. Status bar de color compatible, iconos blancos.
3. Logo `logo_tienda_tecsup_ajustado.png` ancho aprox. **150–164 dp**, altura 60dp, margen x 20–24dp, y bajo barra sistema 12–16dp. Que no se vea diminuto.
4. Imagen `hero_login_telefonos.png` con teléfonos grandes: área **85–95% del ancho** y aprox. **210–245dp** de alto, alineada centro/derecha dentro del hero, `ContentScale.Fit` sin cortar cámaras ni pantallas; superposición controlada sobre la onda, no desenfocada. El `Fit` puede dejar márgenes transparentes; evaluar el recurso y ajustarlo sin deformar relación de aspecto.
5. Onda clara inferior (Canvas Path) con amplitud aprox 28–48dp; transición suave a blanco sin línea dura.
6. Título **«Bienvenido de nuevo»** 28–30sp Bold/ExtraBold, grafito oscuro; margen x24dp, top 20–28dp.
7. Subtítulo **«Tu próximo smartphone está aquí»** 15–16sp `#42557A`, espacio 6–8dp.
8. Campo correo 54dp, margen superior 22dp, icono correo, tipo teclado Email, capitalización apagada, autocorrect off.
9. Separación 12–14dp; campo contraseña 54dp, icono candado, ojo mostrar/ocultar 48dp.
10. Enlace recuperar alineado a derecha, 13–14sp azul, top 10dp, área táctil ampliada.
11. Botón **«Iniciar sesión»** 54–56dp, full width, top 22–26dp. Loading y bloquea doble toque.
12. Separador fino + texto “o” centrado, top 17–22dp.
13. Botón contorno **«Crear cuenta»**, full width 54dp, top 16dp, icono persona +.
14. Padding inferior mínimo 24dp. No insertar botón Google ni otras redes sociales (el usuario NO quiere social login).

**Funcionamiento**: validar vacíos, email, errores Firebase en español, prevenir botón en Loading, navegar según sesión y roles, conservar flujo existente. No crear AuthRepository paralelo.

## 05. PANTALLA B — REGISTRO (REF. 01, PANEL CENTRAL)

**Composición:** top con banda azul **105–130 dp** y onda celeste/blanca hacia el formulario, botón volver blanco de 48dp en x14dp. Título «Crear cuenta» 28–30sp azul marino, subtítulo en dos líneas de 15–16sp. Gráfico ilustrativo de teléfonos y bolsa `hero_registro_telefonos.png` centrado, **160–190dp** alto o 135–165dp en móvil compacto. Fondo blanco/hielo.

**Formulario**: 4 campos de **52–54dp**: «Nombre completo», «Correo electrónico», «Contraseña», «Confirmar contraseña». Separaciones verticales **10–12dp**. Iconos usuarios/correo/candado en azul; contraseñas con ojo. Márgenes laterales **20–24dp**. Recuadro requisitos de contraseña `#EAF2FF`, radio 18dp, padding 14–16dp: mínimo 8 caracteres, letra + número, coincidencia; iconos check dinámicos de 16dp, texto 12–13sp. Checkbox aceptar términos + privacidad, enlaces solo si existen textos/documentos reales; **NO inventar políticas legales**, mantener aviso de documento pendiente si no se dispone. Botón «Crear mi cuenta» azul 54dp y enlace inferior «¿Ya tienes una cuenta? Iniciar sesión». Scroll, sin teclado que tape CTA. Al confirmar, transicionar a cuenta creada; manejo de email existente, débil clave, Internet.

**Importante:** no asignar rol admin desde registro. Firestore determina rol con reglas seguras; usuario nuevo por defecto usuario, nunca admin autodeclarado. Si los documentos del usuario no están implementados correctamente, especificar y corregir lo que sea seguro sin prometer CRUD de roles.

## 06. PANTALLA C — CUENTA CREADA (REF. 01, PANEL DERECHO)

Fondo hielo `#F6F8FF`, header gradiente azul con ondas 135–160dp, centro visual espacioso. Icono check **verde** grande (círculo 115–125dp) centrado con destellos verdes discretos; NO emoji gigante ni clip art de baja resolución. Título **«¡Cuenta creada!»** 28–30sp ExtraBold, centrado. Texto informativo 15–16sp centrado, máximo 3 líneas, ancho 310–340dp. Tarjeta de correo con icono 23dp, fondo blanco, radio 20dp, padding 18dp, 2 líneas etiqueta/correo y posibilidad de elipsis/selección. CTA «Ir a iniciar sesión», alto 54dp, margen horizontal 24dp. Ondas decorativas celestes en el pie; CTA accesible incluso en móviles compactos. No afirmar verificación de correo si no está activada.

## 07. PANTALLA D — RECUPERAR CONTRASEÑA Y CONFIRMACIÓN

**Solicitar recuperación:** top hero azul **155–190dp**, back arrow blanco y icono minimalista de candado/envío. Título «Recuperar contraseña» 26–28sp, descripción de dos líneas, un campo de correo 54dp, botón principal «Enviar enlace» 54dp y secundario «Volver a iniciar sesión» 54dp. Espaciado x24dp, 20–24dp entre secciones. Errores claros, spinner y bloquear doble envío. Usar método real `FirebaseAuth.sendPasswordResetEmail` por repositorio.

**Éxito de solicitud:** ilustración de sobre/check de 84–110dp, título «Revisa tu correo», explicación neutra: «Si existe una cuenta asociada, recibirás instrucciones…» (no exponer existencia de cuentas), botón volver; no mostrar URLs ni enlaces falsos. Separación grande, estructura coherente con confirmación de registro.

## 08. PANTALLA E — CATÁLOGO / HOME (REF. 02)

**Mantener identidad fotográfica.** La referencia usa un hero superior azul/violeta, logo superior izquierda, favoritos y perfil como botones blancos circulares, texto «¡Hola, Usuario!» y un banner oscuro con dos smartphones grandes. Adaptar la composición respetando el modelo de navegación.

### Distribución (ancho 393dp)
1. Header con degradado azul/violeta, altura visual **175–205dp** (no cuenta status bar adicional), padding horizontal 16–18dp. Logo **135–150dp × 53–60dp**, alineado izquierda; dos controles circulares blancos 46–48dp (favoritos y perfil), distancia 8dp. El botón Admin aparece solo para rol ADMIN, dentro de perfil o menú, no para usuario.
2. Debajo del logo, saludo **22–24sp Bold** y subtítulo 14–15sp. Si no hay nombre real, usar «¡Hola!» sin inventar nombre ni emoji intrusivo.
3. Banner carrusel en tarjeta de **200–220dp** alto y radio **24–26dp**, margen horizontal 16dp. Composición DOS COLUMNAS SUPERPUESTAS: 55–58% izquierda para texto y CTA, 58–65% derecha para teléfono REAL; dejar las imágenes al frente, notorias, sin perder el botón. Titular 20–23sp Bold blanco hasta 3 líneas; subtítulo 12–14sp blanco; botón coral `#FF526E` 46dp alto, radio pill y texto 12–13sp.
4. **Tres páginas diferentes** cada una con imagen EXACTA de recurso: p0 `iphone_pro_realista.png`, p1 `samsung_galaxy_realista.png`, p2 `banner_dos_telefonos.png`. No reutilizar el mismo `R.drawable` en las tres. Títulos: «Descubre el mundo iPhone», «Explora Samsung Galaxy», «Tu próximo smartphone». Asegurar imagen sin texto incrustado duplicado, usar `ContentScale.Fit` y ajustar márgenes transparentes si existen.
5. Puntos indicadores centrados fuera del banner: seleccionado óvalo 20×7dp azul, otros 7×7dp gris/celeste; debajo margin 9dp. Auto avance cada 5000ms, pausar cuando usuario arrastra y al salir de pantalla; allow swipe.
6. Búsqueda debajo con margen vertical **15–18dp**: alto **54–56dp**, superficie blanca, radio 28dp, icono search azul 22dp; hint «Buscar modelo o marca» 14–16sp. No micrófono decorativo si no hay reconocimiento de voz implementado. Borrar búsqueda con X cuando aplica.
7. Chips `LazyRow` de 39–43dp: Todos, marcas **derivadas del catálogo real**; no fijar Apple/Xiaomi/Realme si no están. Gaps 8dp, margen superior 15dp, bajo 18dp. Por ejemplo «Todos», «Samsung», «Xiaomi», «Oppo», «Realme» si están presentes.
8. Fila de título **«Smartphones destacados»** 20–22sp y contador de modelos verificado a derecha en 12–13sp; acción de «Ver todos» solamente si existe una sección recortada de destacados que expande a catálogo completo.
9. Sección opcional «Ordenar y filtrar» debajo de búsqueda o título: acción de filtros abre `ModalBottomSheet` con radio superior 26dp, ordenar A-Z, precio menor/mayor, marca y rango de precio aplicable, botón limpiar filtros. No incluir rating ni stock offline si Room no guarda campos; ofrecerlos SOLO si persisten y son consistentes.
10. Tarjetas producto en **2 columnas**, margen horizontal 15–16dp, gap 12dp, filas 14dp. El ancho de tarjeta se calcula: `(screenWidth - 2×16 - 12)/2` ≈ 174.5dp a 393dp. Radio 20–22dp, blanco, sombra 2–3dp; imagen contenida en Box `#F3F7FF` altura **145–165dp**, `ContentScale.Fit`, padding interno 8–12dp, sin deformar. Corazón arriba derecha con toque de 48dp; badge descuento arriba izquierda **solo si `descuento` real >0**. Debajo: título 14–15sp Semibold, máximo 2 líneas; marca 12–13sp secundario si existe; rating con estrella amarillo **solo si existe**; precio actual azul 19–21sp ExtraBold, moneda original `$`; si falta precio no inventar. NO reviews contadas (la API actual no provee recuento), NO precios anteriores ficticios, NO stock falso.
11. Bottom bar 4 entradas: **Inicio, Comparar, Favoritos, Mi cuenta**. Alto contenido 70–76dp, superficie blanca, borde superior #DCE5F3, iconos 22–25dp, etiquetas 11–12sp, seleccionado con pastilla/pill azul pálido bajo icono. Si no se implementa comparador o perfil funcional, NO agregar rutas huecas; en este proyecto SÍ se deben implementar.

### Interacciones y datos
- Búsqueda y filtros instantáneos sobre productos reales Room; procesar mediante `derivedStateOf` / ViewModel según arquitectura, evitar UI bloqueada.
- «Explorar ahora» desplaza a la cuadrícula real o activa el filtro correspondiente con datos presentes; **NO navegador externo ni botón sin acción**.
- Favorito actualiza Room, se mantiene tras reiniciar y sincronizar, sin romper seis pruebas existentes.
- Estados: carga skeleton o spinner consistente, Error + «Reintentar», Vacío, Sin resultados con «Limpiar filtros», Offline con aviso discreto y lista local.
- En móvil estrecho (<360dp), tarjetas aún 2 columnas si caben contenido sin clip; si no, adaptar a 1 sin destrozar texto. Tablet 3–4 columnas con ancho máximo por tarjeta 220dp.

## 09. PANTALLA F — DETALLE DEL SMARTPHONE (PRIORIDAD MÁXIMA)

**Problema real a resolver:** el `DetalleScreen.kt` actual es básico; galería HorizontalPager de 295dp y miniaturas de 49dp visibles, pero las miniaturas **NO son pulsables**. Demasiados bloques genéricos sin jerarquía, no hay similares ni comparación ni zoom.

**Nuevo layout de arriba abajo**:
1. `Scaffold` fondo `#F6F8FF`. Topbar 58–64dp más inset: atrás (48dp), título corto «Detalle», favoritos (48dp) y compartir (48dp); los botones funcionan. Botón favorito sincroniza Room correctamente (ideal: derivar estado de favorito desde flujo repositorio; evitar estado local que se desincroniza). Back navegacional real.
2. Galería amplia fondo muy claro o blanco con radio 24dp, margen horizontal 16dp. Imagen principal de producto **320–360dp** alto (móvil 393dp), `ContentScale.Fit`, sin recorte, padding 12–18dp; sombras delicadas, no tapar imagen con rótulos. Pager horizontal con indicador visual de página.
3. Toque sobre imagen abre **pantalla/dialog de fotografía a pantalla completa** con overlay negro o grafito, controles atrás y zoom por pellizco (gestos correctamente limitados), double tap zoom opcional, deslizar entre imágenes. Si no hay imágenes, placeholder de teléfono 80dp con mensaje accesible.
4. Miniaturas reales 56–64dp por lado, `LazyRow`, 8dp gap, borde azul 2dp y fondo suave para seleccionada; **al pulsar miniatura saltar/animar al índice** con `pager.animateScrollToPage()`. Etiquetas de accesibilidad «Ver imagen 2 de X». Mostrar todas, no solo 5 sin scroll.
5. Debajo 16–20dp, panel principal blanco radio 22dp, padding 18–20dp. Cabecera: badge marca 12sp azul sobre `#EAF2FF`; si rating real, estrella amarilla y valor con 1 decimal. Nombre grande **23–26sp** negrita, puede ocupar 2–3 líneas, descripción de categoría pequeña. Precio **28–30sp azul**, descuento real y stock real en chips 12sp; si `stock == null`, mostrar «Disponibilidad no informada», no «Disponible».
6. Bloque **«Vista rápida»**: 2×2 tarjetas pequeñas si hay datos: marca, categoría, puntuación y stock; cada casilla borde tenue/radio 16dp, icono 20dp, etiqueta 12sp y valor 13–14sp. **No inventar almacenamiento, RAM, batería ni cámaras**: esos campos NO existen hoy en `Item`.
7. Bloque **«Características y especificaciones»** con líneas de clave/valor `SpecRow` de altura mínima 44dp. Si hay pocos campos, mostrar los reales; no repetir 3 veces la misma información.
8. Bloque **«Descripción del producto»**: cuerpo 14–15sp lineHeight 21–23; inicialmente 4–5 líneas, botón «Leer más / Mostrar menos» si excede, sin recortes abruptos.
9. Fila de tres acciones reales con iconos: **Guardar favorito**, **Comparar** (añade a comparador con feedback), **Compartir** (`Intent.ACTION_SEND` o chooser Android bien encapsulado, nunca `startActivity` desde ViewModel). Cada botón mínimo 48dp de toque, labels 12–13sp.
10. Sección inferior **«También te pueden interesar»** con `LazyRow` de tarjetas **155–170dp ancho**, imagen **110–125dp alto**, misma marca o rango de precios ±25–35% o categoría, sin incluir producto actual ni repetidos. **Los recomendados deben venir de `ItemRepository.items` (Room), NO de lista fija ni IA inventando marcas.** Al tocar otro producto se navega a su detalle; verificar que `DetalleViewModel` se recrea/carga correctamente al cambiar `id`.
11. Footer con espacio seguro 20–30dp. **NO botones Comprar, Pagar, Añadir carrito, Envío o Delivery**.

**Offline realista:** `obtenerDetalle()` en código actual consume la API y puede fallar sin internet; aunque catálogo Room funcione, el detalle ampliado no. Implementa recuperación desde Room si es necesaria y coherente: interfaz de repository con fallback local, ampliación de Entity/mapper/migración cuando se requiera más info. Si en esta fase no es viable, muestra mensaje explícito «No se pudo cargar el detalle sin conexión» con reintentar y acceso a catálogo, sin pantalla rota ni afirmar modo offline total.

## 10. PANTALLA G — FAVORITOS

Header de 130–155dp con degradado azul sutil y topbar volver; título «Mis favoritos» 24–26sp, subtítulo número verdadero de productos. En estado no vacío: búsqueda local (52dp), selector de orden (más reciente solo si hay fecha persistida; si no, orden por nombre o precio), cuadrícula de cards reutilizando **EXACTAMENTE `ProductCard`** del catálogo. Quitar favorito actualiza Room inmediatamente, animación ligera de corazón, se mantiene tras reiniciar y sincronizar. Empty state: ilustración simple de contorno corazón 80–95dp, título «Todavía no tienes favoritos», explicación de 2 líneas, botón real «Explorar smartphones» 54dp. Sin favoritos falsos para llenar huecos.

## 11. PANTALLA H — COMPARADOR DE SMARTPHONES (NUEVA FUNCIONALIDAD REAL)

Crear `ui/comparador/{CompararScreen.kt, CompararViewModel.kt}` y los composables auxiliares necesarios. Navegación desde catálogo, detalle y favoritos. Almacenar selección de IDs en `SavedStateHandle`/ViewModel compartido o estado persistible, sin acoplamiento global frágil ni `mutableListOf` sin observar.

- Límite de **2–3 smartphones**, impedir 4.º con snackbar «Puedes comparar hasta 3 modelos».
- Header navy/azul 120–145dp, título 24sp «Comparar smartphones» y contador 0–3.
- Vistas vacías: icono de comparación 80dp, texto «Selecciona al menos dos smartphones» y acción «Explorar catálogo».
- Área superior con **2 o 3 tarjetas comparativas** ancho de columna variable (en móvil dos columnas visibles y scroll horizontal si hay 3), imagen 120–145dp, título hasta dos líneas, precio azul, X quitar.
- Tabla de características con filas alternadas leves y valores correspondientes: Precio `$`, Marca, Categoría, Calificación (si existe), Descuento (si existe), Stock (si existe). Si faltan campos, usar «No disponible» en lugar de completar con especificaciones inventadas.
- Resaltar valores comparables en verde/azul, por ejemplo precio menor (siempre explicar criterio); no declarar un teléfono «mejor» por rating desconocido.
- CTA «Vaciar comparación» con confirmación ligera o deshacer. Navegación segura y seleccionados mantenidos al abrir/volver entre pantallas al menos durante la sesión.
- Este módulo NO llama a API extra ni necesita una IA generativa.

## 12. PANTALLA I — MI CUENTA / PERFIL

Crear `ui/perfil/{PerfilScreen, PerfilViewModel}.kt` si justificado y registrar en `AppNav`/ViewModelFactory. Estética limpia con hero pequeño azul **145–170dp**, avatar circular abstracto 64–76dp con inicial del nombre (si nombre real disponible) o icono de usuario; título «Mi cuenta» 24sp. Tarjeta principal blanca superpuesta, radio 22dp: correo real obtenido de `Sesion`, rol **Usuario/Administrador** como texto discreto, no permitir editar UID ni rol desde UI. No mostrar datos personales no conocidos.

Secciones menú en tarjetas 54–60dp por fila: «Mis favoritos», «Comparar smartphones», «Apariencia» (Light/Dark/System si se implementa persistencia local DataStore; si no, usar tema del sistema y no ofrecer control ficticio), «Ayuda / Acerca de Tienda Tecsup», «Cerrar sesión» con diálogo cuando convenga. Acceso «Administración» **solo si sesión rol ADMIN**. En configuración, ningún enlace externo inventado. Cerrar sesión vuelve a Login y elimina pantalla protegida del back stack.

## 13. PANTALLA J — ADMINISTRACIÓN PREMIUM (SIN INVENTAR E-COMMERCE)

**Conservar funciones reales**: total guardados, total favoritos, sincronizar catálogo y limpiar caché; `AdminViewModel` ya implementa esas operaciones. Rediseñar dashboard: top hero navy→azul 145–165dp, título «Panel de administración», subtítulo «Control del catálogo y datos locales», acción volver. Dentro fondo blanco/hielo, tarjetas de métricas 2 columnas con radio 20dp, iconos profesionales 28dp, número 28–30sp, etiqueta 12–14sp.

Añadir por lógica real y repositorios:
- Estado última sincronización **solo si guardado como timestamp real**, si no ocultar/mostrar «Sin registro». No inventar «Sincronizado hace 5 minutos».
- **Distribución por marcas** calculada desde flujo Room, barras horizontales de proporción con contadores; si Room no guarda marca, derivar del subtítulo validado, o hacer ampliación explícita/consulta. Evitar estadísticas de ventas ficticias.
- Resumen de productos disponibles **solo si stock está almacenado**; si no, «Datos de inventario no disponibles en modo local».
- Lista de productos locales en modo consulta (imagen, nombre, precio); no escribir «Editar producto», «Guardar stock», «Crear promociones» sin backend de escritura.
- Acciones **Sincronizar catálogo**, **Limpiar caché** (confirmación clara «Elimina los productos guardados, incluidos los favoritos locales» cuando corresponda), mensajes de resultado Snackbar y loading mientras trabaja.
- No implementar módulos de «Ventas», «Pedidos», «Clientes que compraron», «Ingresos», «Pagos». Es app de consulta y API de lectura. El admin solo puede entrar con rol Firestore autenticado, reforzar verificación de navegación.

## 14. PANTALLAS K — CARGA, ERROR, VACÍO, OFFLINE Y PERMISOS

Crear componentes compartidos coherentes:
- `PremiumLoadingState`: spinner azul 36dp, label 14sp, skeletons opcionales; no saltos bruscos.
- `PremiumErrorState`: icono sencillo 64dp, título 20sp, mensaje útil, botón «Reintentar» 54dp; distinguir falta internet de 404 y errores de autenticación; no exponer stack traces.
- `PremiumEmptyState`: icono 76dp, título 20–22sp, explicación y CTA según contexto.
- Sin resultados de búsqueda: «No encontramos modelos», botón «Limpiar filtros».
- Offline: banner no modal `#EAF2FF`, icono wifi-off 18dp, texto «Sin conexión: mostrando datos guardados», contraste accesible; NO ocultar catálogo local.
- Sin permiso notificaciones: la app sigue funcionando; no abrir diálogos repetitivos, conservar canal/permiso actual.

## 15. NUEVA FUNCIÓN — ENCUENTRA TU SMARTPHONE IDEAL (SIN IA EXTERNA)

Crear módulo sencillo y real **solo si el tiempo y datos lo permiten**: wizard 3 pasos dentro de Compose `ui/recomendador/`: 1) rango de precio (slider con límites derivados de productos presentes); 2) marca preferida (chips reales); 3) orden de preferencia (precio menor / mejor rating cuando haya datos / cualquiera). Resultado usa **filtro y ranking deterministas de `ItemRepository.items`**, explica «Recomendado según precio y marca», no «IA analizó tu personalidad». Si ningún dato cumple, mostrar alternativas reales cercanas. Nunca conectar API de IA sin claves, no mostrar respuestas inventadas. Ubicar acceso discreto en catálogo «Ayúdame a elegir»; si perjudica prioridad principal, implementar después de Detalle/Comparador.

## 16. ANIMACIONES Y MICROINTERACCIONES

1) Cambio de favorito con escala 0.94→1.05→1 / tint 180ms. 2) selección de chips 200ms con animateColorAsState. 3) apertura de detalle navegación suave 200–280ms, sin flicker. 4) pager imágenes 250ms; miniaturas actualizan selección al deslizar. 5) modal de filtros abre/cierra correctamente, conserva selección. 6) spinner en botones de autenticación y admin, botones deshabilitados mientras operan. 7) pantallas no usan rotación/animación infinita innecesaria. 8) imágenes con placeholder legible y fallback. 9) tomar en cuenta lifecycle para auto-scroll banner.

## 17. ARQUITECTURA DE LOS CAMBIOS

- `AppNav.kt`: añadir rutas `COMPARADOR`, `PERFIL`, opcional `RECOMENDADOR`, mantener originales `LOGIN`, `REGISTRO`, `CUENTA_CREADA`, `RECUPERAR`, `RECUPERAR_EXITO`, `LISTA`, `DETALLE`, `FAVORITOS`, `ADMIN`. Login y rutas protegidas sin loop; al cerrar sesión limpiar backstack.
- `ViewModelFactory.kt`: factorizar ViewModels nuevos respetando interfaces. No `FirebaseAuth.getInstance()` ni `Retrofit.create()` dentro de composables.
- `PremiumUi.kt`: componentes reutilizables CTA, input, títulos, badges, paneles, estados y wrappers; evitar copiar 400 líneas por pantalla.
- `ProductCard.kt` nuevo: única tarjeta reutilizada desde catálogo, relacionados, favoritos.
- `ProductGallery.kt` nuevo: pager + miniaturas funcionales + zoom full-screen.
- `FilterChips.kt` y `FiltersBottomSheet.kt`: filtros accesibles y estados guardados con `rememberSaveable` o ViewModel.
- `theme/Color.kt`, `Type.kt`, `Theme.kt`: tokens centralizados; soporte dark mode con suficiente contraste, sin poner todo blanco o azul.
- `ItemRepository`/Impl: solo modificar si se necesita información real para funciones propuestas. Mantener Room fuente de verdad para lista y favoritos, preservar sincronización + favoritos, aprovechar `Item` real. Si migrar Room, subir versión Database y definir migración de datos, o no introducir cambio invasivo.
- `AppContainer.kt`: mantener inyección existente; no introducir Hilt solo por preferencia si el proyecto no lo usa.
- `drawable/` y `drawable-nodpi/`: usar los PNG disponibles; nombres de recurso solo letras minúsculas, dígitos, guion bajo. PNG fotográficos mejor en `drawable-nodpi` para evitar reescalado automático; no duplicar dos archivos del mismo nombre en carpetas de recursos que compitan.
- Las referencias son instrucciones visuales; no incrustar las capturas como UI.

## 18. RESTRICCIONES CRÍTICAS DE SEGURIDAD Y VERACIDAD

1. NO modificar `google-services.json` ni publicarlo en Git, `.gitignore` debe excluirlo; tampoco subir contraseñas o claves.
2. NO crear registro de administrador desde interfaz; rol lo determina Firestore.
3. NO simular número de reseñas, precios originales, descuento, stock, especificaciones, nombre de usuario, ventas, estadísticas, imágenes oficiales o reseñas.
4. NO escribir `S/` si DummyJSON entrega USD; si se desea soles se requeriría conversión real claramente etiquetada y fuente de tipo de cambio, fuera de este alcance.
5. NO usar logos falsos de Apple/Samsung ni fotos con marca de agua; reutilizar los recursos adjuntos. Las imágenes fotorrealistas incluidas son **renderizados**, NO fotos oficiales, nunca afirmar que son fotos oficiales. Para fotografías oficiales se necesitaría obtener el material permitido por licencias.
6. NO añadir Google Login, carrito, pagos, entregas, dirección de envío, stock editable, CRUD de productos remotos, venta o delivery.
7. NO llenar componentes con texto placeholder, botones “Próximamente” sin funciones ni rutas rotas.
8. NO quitar las seis pruebas unitarias existentes. NO cambiar contratos de repositorio sin actualizar dobles de prueba.
9. NO desactivar Firestore rules ni habilitar escritura pública para que una pantalla funcione.
10. NO cambiar Gradle/Java/Kotlin/AGP salvo necesidad demostrada, y nunca degradar versiones a ciegas.

## 19. PRUEBAS OBLIGATORIAS ANTES DEL COMMIT ÚNICO

**Gradle en Windows/PowerShell**:
```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

**Pruebas manuales funcionales**:
- Login válido rol USUARIO: accede a Catálogo, no ve Admin. Login ADMIN: ve acceso Admin. Contraseña incorrecta/inputs vacíos muestran error. Sesión persiste cuando corresponde, Logout vuelve a Login.
- Registro: validación nombre/correo/contraseña y confirmación, no crea admin, pantalla éxito; recuperación envía correo por repositorio y confirma sin filtrar cuentas.
- Catálogo: cada banner muestra una imagen DISTINTA y no tapa texto, búsqueda funciona, marcas reales funcionan, orden precio funciona, filtros limpiables, scroll fluido.
- Detalle: usa segundo endpoint, foto principal grande, miniatura 2 cambia a foto 2, zoom con back, descripción expandible, similares abren OTRO producto, compartir abre chooser.
- Favoritos: marcar/desmarcar, persiste al cerrar y al sincronizar; vista vacía si no hay favoritos.
- Comparar: añadir 2–3, impedir 4, quitar, navegar y volver, no mostrar campos inventados.
- Perfil: muestra correo y rol reales, los links llevan a pantallas reales, logout funciona.
- Admin: solo ADMIN, métricas coinciden con Room, sync muestra notificación, limpiar caché confirma y actualiza conteos.
- Error/offline: modo avión con datos muestra Room + aviso; sin datos error + reintentar, detalle offline honesto, permiso de notificaciones negado no bloquea app.
- Rotación/fontScale: no pierde estado esencial, no crashea, no reconsulta la API sin necesidad. Teclado no tapa buttons en auth.
- Verificar tamaños de pantalla **320×700dp, 360×800dp, 393×852dp, 412×915dp**; si emulador falta, documentar que no se pudo probar, no declararlo exitoso.

**Si el proyecto no compila**, corregir primero las referencias/imports, API de Compose, assets y migraciones, y entregar el error real si no puede resolverse. No afirmar “100% funcional” sin ejecutar pruebas.

## 20. CRITERIOS DE ACEPTACIÓN VISUAL (CHECKLIST DE REVISIÓN)

- [ ] Inicio: teléfonos ocupan gran parte del hero, logo visible, onda azul/blanco, inputs, botones y enlaces respetan la referencia.
- [ ] Registro: cabecera/onda, ilustración, título, cuatro inputs, check de requisitos y CTA visibles con scroll; misma identidad.
- [ ] Cuenta creada: check verde dominante, título centrado, correo real y CTA, sin fondo vacío desproporcionado.
- [ ] Recuperación: mismo sistema de diseño, validaciones y confirmación completa.
- [ ] Catálogo: banner oscuro y teléfonos grandes, distinta imagen por página, chip brands, search, 2 columnas, bottom bar 4 entradas, header no recorta contenido.
- [ ] Detalle: protagonismo fotográfico, miniaturas realmente clicables, zoom, specs reales, similares.
- [ ] Favoritos: tarjetas consistentes, vacío impecable.
- [ ] Comparador: tabla legible y valores reales, selección persistida dentro de sesión.
- [ ] Perfil: correo/rol, entradas funcionales, cerrar sesión.
- [ ] Admin: conteos reales, herramientas correctas, nada de venta ficticia.
- [ ] States: carga/error/vacío/offline coherentes.
- [ ] Sin widgets inertes, sin fallos evidentes de diseño, sin textos que se pisan.

## 21. ORDEN DE TRABAJO PARA EL AGENTE (NO ABANDONAR A MEDIAS)

**Paso 1.** Inspeccionar el árbol real, datos, navegación, gradle, test, drawables y capturas. Informar brevemente diferencias entre ZIP y lo descrito, sin preguntar datos ya proporcionados.

**Paso 2.** Definir una tabla de tokens concreta y componentes reutilizables. Preparar la jerarquía de pantallas, contemplar responsive y teclado.

**Paso 3.** Implementar primero **Detalle + ProductGallery + ItemCard + recomendados**, que es el mayor problema de calidad actual. Luego Catalogo/Banner/Filtros; luego Comparador/Favoritos; luego Login/Registro/Éxito/Recuperación; luego Perfil/Admin.

**Paso 4.** Mantener compilación incremental por grupos de archivos; reparar errores de imports/composables al terminar cada módulo. Reutilizar el repositorio existente.

**Paso 5.** Ejecutar Gradle tests, comprobar navegación/roles/Firebase/Room/offline. No dejar TODO ni función demo que no navegue.

**Paso 6.** Entregar resumen final **pantalla por pantalla**, lista de archivos creados/modificados, cambios en modelos si hubo, resultados de pruebas reales, errores pendientes, capturas si se obtienen; ZIP de fuente limpio si se puede. Nunca empacar `.git/`, `build/`, `.gradle/`, `.idea/`, `local.properties`, `google-services.json` en el ZIP para compartir.

**Paso 7.** Preparar **UN SOLO commit para TODA la interfaz**, una vez validada. No crear 10 commits por cada pantalla. No reescribir el commit anterior de las seis pruebas.

## 22. INSTRUCCIONES FINALES Y FORMATO DE RESPUESTA DE LA IA PROGRAMADORA

No me entregues solamente ideas, wireframes, imágenes, diagramas, pseudocódigo ni “próximos pasos”. **Implementa el código real en el proyecto adjunto**. Da archivos y rutas concretas; si no puedes editar ZIP directamente, entrega código completo por archivo y un parche aplicable, sin truncar lo esencial. Usa los PNG anexos, no iconografía improvisada. Conserva la navegación y prueba las conexiones. Toda mejora visual debe estar asociada a funcionalidad real. Registra claramente qué estaba antes, qué cambiaste y qué se verificó. Prioriza en este orden: **fidelidad al diseño anterior → funcionalidad real → MVVM correcto → responsive → pruebas → acabado visual**.

La app debe quedar reconocible como **Tienda Tecsup**, elegante, azul marino/celeste, profesional, con **fotografías grandes de smartphones y espacios ordenados**. Ningún pago ni envío. Este rediseño completo equivale a **un solo commit de UI** en `mejora-ia`; la bitácora `PROMPTS.md` puede ir en otro commit de documentación.



---

## 2. Acciones imprescindibles para validar la aplicación en Android Studio

1. Extraer el ZIP del proyecto, abrir la carpeta `MIERCOLES` desde Android Studio y seleccionar un JDK compatible con el Gradle del proyecto. 
2. Conservar/instalar el Android SDK correspondiente y descargar dependencias Gradle; sincronizar el proyecto.
3. Copiar tu configuración de Firebase **sin compartirla** a `app/google-services.json`. Configurar correctamente usuarios de prueba y documentos `usuarios/{uid}` con `rol` en Firestore.
4. Ejecutar ` .\gradlew.bat :app:assembleDebug` y ` .\gradlew.bat :app:testDebugUnitTest` desde PowerShell sin el espacio inicial.
5. Probar roles USUARIO/ADMIN, entrar y salir; registro, recuperación; tres banners; búsqueda, filtros y limpieza; miniaturas, zoom, similares; comparar dos o tres productos; favoritos y sincronización; perfil y apariencia; Admin con caché; conexión interrumpida.
6. Probar diseños a 320dp, 360dp, 393dp y 412dp de ancho, variaciones de densidad y tamaño de letra.
7. Si alguna versión de Gradle, KSP o SDK no está instalada, resolverla en el entorno local sin cambiar versiones a ciegas. **No considerar compilado este proyecto únicamente porque el ZIP sea válido.**
8. Copiar el código final al repositorio de la pareja, conservar el commit previo con pruebas y generar exactamente el commit de interfaz planeado en `mejora-ia`.

### Resumen de requisitos de Fase 2

- Las seis pruebas del `ListaViewModel` ya están dentro del proyecto original y se mantienen.
- El rediseño integral de interfaz va en un único commit con mensaje `feat(ui): renovar integralmente interfaz de Tienda Tecsup con IA`.
- `PROMPTS.md` reúne en este mismo archivo el prompt, los cambios aplicados, los criterios de aceptación y los límites de verificación.
- El laboratorio también solicita README general, PR hacia `main` y tag `v1.0`; por solicitud del usuario este ZIP trae únicamente **PROMPTS.md** como documento de explicación. Gestionar los demás entregables al realizar la entrega académica.
