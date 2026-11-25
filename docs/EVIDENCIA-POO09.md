# Evidencia de prueba POO-09

El 25/11/2026 se probaron los tres proyectos mediante HTTP, con las versiones de los compañeros fijadas en el README y bases H2 temporales. Los tres OpenAPI respondieron 200. No se usaron ni modificaron las bases reales.

Preparación: creeper de Mateo `1`; creeper de Santiago en `(0,0,0)`; jugador de Santiago `steve-prueba` con salud 20 en `(2,0,0)`; bloque de Santiago `1` en `(1,0,0)`; cerdo local con salud 20 y TNT local en `(3,0,0)`.

- A2: creeper inexistente; A1 respondió 422 con `origen: A2` al inferir su ausencia de la lista de Mateo.
- A3: bloque inexistente; la explosión de Santiago respondió 404 y A1 devolvió 422 con `origen: A3`. La transacción de Santiago dejó el jugador con salud 20; el bloque válido siguió intacto.
- Tras ambos rechazos el cerdo de Lucas seguía con salud 20 y la TNT sin detonar. Las pruebas automatizadas también verifican que no aumentan jugadas ni explosiones locales.
- Caso feliz HTTP 201: `{"creeperA2":1,"jugadorA3":"steve-prueba","explosionId":1,"distanciaJugadorA3":2.0,"danoSimuladoA2":10,"saludSimuladaA3":10,"creeperA3":"creeper-a3","radioA3":5.0,"danoAplicadoA3":12,"saludFinalA3":8,"bloqueA3":1,"bloqueDestruidoA3":true,"id":1}`. `GET /api/jugadas/1` devolvió el registro.
- Después del éxito, en A1 el cerdo quedó con salud 12 y la TNT detonada. En A3 el jugador quedó con salud 8 y el bloque destruido. Mateo solo simuló y su base no fue modificada por la jugada.

Los cambios entre A1 y A3 no comparten transacción; un fallo posterior a la respuesta de A3 o un timeout pueden dejar un resultado parcial entre servidores.
