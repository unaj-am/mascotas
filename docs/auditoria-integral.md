# Auditoría integral de Mascotas

Fecha: 2026-11-08

## Alcance y método

Se inspeccionó el árbol versionado y los 17 módulos declarados en `settings.gradle.kts`; configuración Gradle, manifiesto, entrada de aplicación, navegación de ambos roles, sesión, diseño compartido, dependencias, contrato y parte de la implementación de repositorio, persistencia, carga de imágenes, notificaciones y servicio de ubicación. Se revisaron rutas/pantallas organizadas en features y patrones de ViewModel/UI State. El proyecto contiene una cantidad de pantallas que excede una revisión visual real de cada estado solo desde código; no se hizo una inspección por dispositivo, por lo que los hallazgos visuales son sobre consistencia y estructura, no un dictamen pixel a pixel.

## Arquitectura y módulos actuales

- `:app`: composición de aplicación, navegación Navigation 3 por rol, sesión autenticada y ensamblado de features.
- `:core:domain`: modelos, contratos de repositorio y casos de uso. Mantiene dependencia de Coroutines y `javax.inject`.
- `:core:data`: repositorios Firebase/Firestore y fuentes remotas; integra Firebase Auth, Firestore y FCM.
- `:core:database`: Room, DAOs, entidades, mappers y migración 1→2.
- `:core:ui`: tema Material 3 (variantes Owner/Walker y claro/oscuro), tipografía, dimensiones y componentes Compose compartidos.
- `:core:location`, `:core:cloudinary`: seguimiento GPS y carga de imágenes.
- `:features:auth/home/notifications/pets/profile/tracking/walkers/walks`: pantallas, componentes, estados y ViewModels por capacidad.
- Hilt enlaza repositorios y servicios. Hay contratos/casos de uso en dominio y la UI recoge StateFlow con lifecycle en los puntos principales.

## Hallazgos

### Crítico
No se confirmó un fallo crítico reproducible con la inspección estática realizada. La compilación y pruebas base están ejecutándose para conocer el estado real.

### Alto
- `CloudinaryPhotoUploader` hacía `readBytes()` antes de comprobar el máximo de 5 MB. Un URI grande o malicioso podía provocar presión severa de memoria/OOM aunque finalmente se rechazara. La subida se efectúa en IO y verifica MIME, preset unsigned, respuesta HTTP y extracción de URL.
- `ProfilePhotoPicker` conservaba el URI creado para cámara en `remember`. Una recreación de Activity (por ejemplo, rotación mientras la cámara externa está activa) puede perder la referencia y descartar el resultado. La pantalla no solicita permiso CAMERA porque delega captura en el intent de cámara.
- `MainActivity` solicitaba `POST_NOTIFICATIONS` automáticamente al iniciar la aplicación y descartaba el resultado. Ahora presenta una explicación opcional tras autenticar, recuerda la decisión, registra el resultado y evita interrumpir inicios posteriores; la bandeja interna continúa disponible si no se concede.
- El manifiesto declara ubicación fina/gruesa y un servicio foreground location. La ruta real de permisos y el comportamiento Android 14+ deben validarse en dispositivo; esta auditoría no ejecutó el flujo con permisos denegados.
- Backups están habilitados y las reglas de respaldo están vacías/plantilla. Debe decidirse explícitamente qué preferencias y datos locales son restaurables, especialmente por la presencia de sesión y caché local. No se cambió porque es una decisión de retención/privacidad con efecto observable.

### Medio
- El patrón Clean Architecture está presente, pero parcial: hay ViewModels que reciben casos de uso y módulos UI compartidos, aunque parte de presentación/configuración de sesión permanece en `:app`.
- `PetRemoteDataSource` duplicaba operaciones y mapeo de `PetRepositoryImpl`; no tenía referencias ni binding de Hilt. Se eliminó tras búsqueda global. No se cambió la implementación activa del repositorio.
- La navegación del paseador mantiene stacks por pestaña pero al seleccionar otra pestaña hace `popToRoot`; esto es consistente con los comentarios actuales, aunque pierde posición/destino interno al cambiar pestaña. Debe confirmarse si se busca conservar back stack independiente.
- Los módulos de features repiten dependencias Compose/lifecycle/Hilt en Gradle. El catálogo centraliza la mayoría de versiones; en esta iteración también se centralizó OkHttp. La repetición de dependencias de features sigue pendiente de revisión por uso directo.
- Los tests versionados son mayormente esqueletos `ExampleUnitTest`/`ExampleInstrumentedTest`; no hay cobertura visible suficiente de casos de uso, repositorios, navegación ni estados de error.

### Bajo
- Design System reutilizable ya existe (Material 3, tokens de color/tipografía/forma/dimensiones y componentes). La consistencia visual debe validarse por pantalla y preview/dispositivo antes de cambiar tokens globales.
- La navegación/los componentes manejan estados de carga/error en distintas áreas, pero la auditoría estática no demuestra paridad visual ni accesibilidad de todos los estados.
- Hay nombres con erratas heredadas (por ejemplo `PetZise.kt` y paquetes de pruebas `wolkers`); corregirlos tiene bajo valor frente a cambios de contrato.
- El release build tiene optimización deshabilitada explícitamente; conviene revisar R8/keep rules antes de habilitarla, con un build release y pruebas de humo.

## Plan priorizado

| Prioridad | Trabajo | Archivos/módulos | Criterio |
|---|---|---|---|
| Alto | Limitar lectura de fotos durante el streaming, no después de cargar el archivo en RAM | `:core:cloudinary`, `CloudinaryPhotoUploader.kt` | Rechazar >5 MiB mientras se transmite; preservar MIME, endpoint y resultado URL |
| Alto | Restaurar URI de cámara en recreación de Activity | `:core:ui`, `ProfilePhotoPicker.kt` | Guardar estado Parcelable restaurable; conservar selección actual |
| Alto | Validar permiso de notificaciones con interacción contextual y texto UX | `:app`, `MainActivity.kt` | Explicar el beneficio, permitir continuar sin permiso, no volver a pedir tras rechazo; completado |
| Medio | Definir política de backups y caché por usuario | manifiesto/XML, `:core:database` | Decidir qué datos se restauran; añadir reglas y probar actualización/restauración |
| Medio | Medir duplicación de fuentes de datos y dependencias | `:core:data`, `:core:cloudinary`, Gradle | Eliminada fuente sin consumidores; retiradas dependencias UI sin uso y centralizado OkHttp |
| Medio | Agregar pruebas de lógica/errores con fakes y cobertura de navegación | módulos feature/domain/data | Probar invariantes sin backend/dispositivo; pruebas de ubicación en emulador |
| Bajo | Recorrido visual y accesibilidad en pantallas y tamaños | `:core:ui` + features | Revisar estados loading/empty/error/success y contraste en previews/dispositivo |
| Bajo | Habilitar optimización release con reglas verificadas | `:app`, keep rules | Build release y smoke test antes de publicar |

## Implementación de esta iteración

Se corrigieron tres problemas de prioridad alta sin cambiar reglas de negocio ni contratos: lectura de fotos con límite de memoria, persistencia del URI de cámara al recrear la Activity y solicitud contextual del permiso de notificaciones. Además, se eliminó `PetRemoteDataSource` tras confirmar que no tenía consumidores, se retiraron AppCompat/Material de `:core:data` y AppCompat/core-ktx/Material/Coroutines Android de `:core:cloudinary`, y se centralizó OkHttp 4.12.0 junto con la dependencia explícita de Coroutines Core. La decisión de política de backups permanece pendiente por su impacto sobre restauración de caché y preferencias.

## Validación

- Línea base: `gradlew test` — BUILD SUCCESSFUL.
- Validación de iteración anterior: `gradlew test :app:assembleDebug` — BUILD SUCCESSFUL.
- Esta iteración: `gradlew :core:cloudinary:test :core:data:test :app:assembleDebug` — BUILD SUCCESSFUL en 22m 54s.
- `git diff --check` y `git diff --cached --check` — limpios.
- La sincronización de Android Studio no pudo resolver classpath inicialmente porque C: no tenía espacio libre; tras limpiar artefactos generados, la compilación CLI completa pasó.
- Siguen avisos de compatibilidad del toolchain JDK 25/Kotlin, deprecaciones Compose/Hilt y casts no verificados en otros módulos; no son fallos de esta validación.
- No se ejecutaron pruebas instrumentadas ni flujos manuales de cámara, carga Cloudinary, ubicación o permisos; requieren dispositivo/emulador y backend.

## Revisión de seguimiento — 2026-11-14

Se revalidó la estructura y el estado actual sin encontrar evidencia que justifique una reescritura arquitectónica. Los 17 módulos mantienen separación por `:app`, `:core:*` y `:features:*`; se observaron 24 ViewModels, aproximadamente 24 pantallas y componentes/tema compartidos en `:core:ui`. Las integraciones de Firebase/Firestore, Room, Cloudinary, OSRM/MapLibre y Fused Location Provider permanecen encapsuladas en data/core y no se introdujeron cambios de contrato.

Los tres fixes de prioridad alta y la limpieza de dependencias descritos arriba ya están presentes en los archivos inspeccionados, por lo que no se repitieron ni se hicieron refactors cosméticos. Se verificó además que `PetRemoteDataSource` no aparece en el árbol actual. `backup_rules.xml` y `data_extraction_rules.xml` siguen como plantillas: definir qué preferencias y caché se restauran requiere una decisión explícita de retención; se conserva por ahora el comportamiento existente.

Validación en esta revisión: la tarea Gradle `test` terminó correctamente con 22 tests reportados por Android Studio (incluye 6 de mascotas y 4 de tracking); `:app:assembleDebug` terminó correctamente. El resumen de `test` no listó `:core:domain`, aunque sus pruebas de casos de uso están presentes en el código fuente. No se ejecutaron pruebas instrumentadas ni pruebas manuales de hardware/backend. La inspección de `git status` fue rechazada por el revisor automático porque el comando de shell guarda buffers del IDE antes de ejecutarse; no se intentó eludir ese control, por lo que este informe no afirma el estado del diff.

### Continuación del plan — pruebas de mapeos

Se añadieron pruebas unitarias en `:core:data` para fijar los formatos Firestore de `Pet`, `User`, `Walker`, `Walk`, `WalkRequest` y `WalkLocation`. Cubren campos opcionales, estados, `rating` omitido en updates y la diferencia existente entre serialización de paseos con nulos o valores por defecto. No requieren conexión Firebase.

- `:core:data:testDebugUnitTest` — BUILD SUCCESSFUL, 7 tests (incluye las 6 nuevas pruebas de mapeadores).
- `:app:assembleDebug` — BUILD SUCCESSFUL.

Siguiente trabajo pendiente del plan: definir la política de backup/restauración, luego ampliar pruebas de lectura de snapshots/repositorios con fakes y hacer revisión visual/accesible en dispositivo.
