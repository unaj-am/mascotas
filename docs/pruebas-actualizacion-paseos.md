# Pruebas de actualización de paseos

El proyecto usa Hilt en producción y JUnit 4 para pruebas locales. Para esta corrección se usan repositorios falsos en memoria y kotlinx-coroutines-test, con la misma versión de Coroutines del catálogo. No se consulta Firebase real.

LiveWalkUpdatesTest verifica que una solicitud aceptada se transforme en un paseo programado, que un paseo finalizado actualice la lista y el detalle sin volver a cargar, y que cambiar de usuario cancele la observación anterior. El ViewModelStore se limpia al finalizar cada prueba.

Ejecutar: `gradlew.bat :features:walks:testDebugUnitTest --tests "*LiveWalkUpdatesTest"`.

La compilación de la aplicación verifica además el enlace de Hilt y los consumidores. La comprobación con dos dispositivos y Firestore real queda como validación de integración: aceptar una solicitud y finalizar un paseo desde el paseador mientras el dueño mantiene abierta la pantalla correspondiente.
