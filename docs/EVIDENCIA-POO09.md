# Evidencia de prueba POO-09

El 25/11/2026 se ejecutaron las versiones públicas indicadas en el README de Mateo y Santiago junto con el código de Lucas, cada una en una base H2 en memoria y puertos locales. Los tres OpenAPI respondieron 200. No se usaron las bases reales de los compañeros.

Preparación: creeper de Mateo `2`, jugador de Santiago `steve-prueba2` con salud 20 y posición `(2,0,0)`, cerdo local con salud 20 y TNT local en `(3,0,0)`.

- Creeper inexistente: HTTP 422, `{"origen":"A2","error":"El creeper no figura entre los listos para explotar"}`.
- Jugador inexistente con creeper válido: HTTP 422, `{"origen":"A3","error":"El jugador no figura entre las entidades vivas"}`.
- Tras ambos rechazos, el cerdo local seguía con salud 20 y la TNT sin detonar. Las pruebas automatizadas comprueban además que no aumenta la cantidad de jugadas ni explosiones.
- Caso feliz: HTTP 201, `{"creeperA2":2,"jugadorA3":"steve-prueba2","explosionId":1,"distanciaJugadorA3":2.0,"danoSimuladoA2":10,"saludSimuladaA3":10,"id":1}`. `GET /api/jugadas/1` devolvió el mismo registro. El cerdo local quedó en salud 12 y la TNT detonada; el jugador de Santiago permaneció con salud 20.

Las dos respuestas 422 son generadas por Lucas al no encontrar participantes en las listas HTTP 200 de los compañeros. La validación explícita de A3 se probó en el camino feliz y los errores explícitos se comprueban con respuestas simuladas en pruebas automatizadas. El resultado del daño de Mateo es una simulación, no una modificación de Santiago.
