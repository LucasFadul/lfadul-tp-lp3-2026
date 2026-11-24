# POO-09 - Guía de coordinación y pruebas

> **Estado al 23/11/2026:** el escenario de Lucas consulta dos listas y guarda una explosión local. El caso exitoso se ejecutó con las tres APIs accesibles, pero falta acordar cómo aplicar las reglas de explosión de los compañeros a la misma jugada. Las direcciones y los IDs que aparecen abajo pueden cambiar.

## 1. Quién es quién

Cada integrante entrega su propio proyecto y ocupa el rol A1 en su entrega:

- **Lucas:** A1 Lucas; A2 Mateo (`MatRF04`); A3 SaDez7.
- **Mateo:** A1 Mateo; A2 y A3 son Lucas y SaDez7 en el orden que él documente.
- **SaDez7:** A1 SaDez7; A2 y A3 son Lucas y Mateo en el orden que él documente.

El [enunciado POO-09](https://github.com/alefq/afq-taller-git-2024/blob/main/docs/ejercicio-poo-09-apis-cruzadas-2026-11-16.md) pide que la jugada de A1 consulte una regla de cada compañero, se guarde en H2 solo si ambas la permiten y que, ante un rechazo, la base de A1 quede igual. Cada estudiante prepara su propio escenario, pruebas y entrega.

## 2. Conexión por Hamachi

Direcciones observadas en la sesión del 23/11/2026:

- Lucas: Swagger <http://25.30.124.10:8080/swagger-ui/index.html>; OpenAPI <http://25.30.124.10:8080/v3/api-docs>.
- Mateo: Swagger <http://25.28.15.25:8080/swagger-ui.html>; OpenAPI <http://25.28.15.25:8080/v3/api-docs>.
- SaDez7: Swagger <http://25.29.140.0:8080/swagger-ui.html>; OpenAPI <http://25.29.140.0:8080/v3/api-docs>.

Cada uno debe tener Hamachi conectado y su servicio iniciado. Para arrancar el servicio de Lucas con esas direcciones, desde la raíz de su repositorio:

```bash
API_A2_BASE_URL=http://25.28.15.25:8080 \
API_A3_BASE_URL=http://25.29.140.0:8080 \
./mvnw spring-boot:run
```

En distintas computadoras los tres pueden usar el puerto 8080. Si las IP cambian, se deben actualizar las variables y los enlaces. Para probar los tres en una sola computadora, consultar los ejemplos de puertos 8080, 8081 y 8082 en el README.

## 3. Estado actual de la jugada de Lucas

`POST /api/jugadas` recibe:

```json
{
  "creeperA2": 5,
  "jugadorA3": "steve",
  "tntId": "tnt-prueba-01",
  "objetivoId": "cerdo-prueba-01"
}
```

Los IDs son ejemplos de la sesión, no valores garantizados. A1 consulta la lista de creepers listos de Mateo y después la lista de entidades vivas de SaDez7. Si encuentra los IDs indicados, simula una TNT sobre el objetivo **de Lucas** y guarda una explosión y una jugada **en H2 de Lucas**. No ejecuta la explosión remota ni modifica las bases de los compañeros.

Si no aparece el creeper o el jugador, A1 devuelve 422 con `origen: A2` o `origen: A3`, sin ejecutar la explosión local. **Esa decisión la toma A1 a partir de listas con HTTP 200**; no es un rechazo explícito emitido por las APIs remotas. Un fallo de comunicación se informa como 502, no como una regla rechazada.

El 23/11 se comprobó por Hamachi un caso exitoso con HTTP 201: jugada 1 y explosión 5. Las pruebas de ausencia de A2 y A3 se ejecutaron antes contra las APIs reales usando una base temporal de A1. No sustituye una nueva prueba con las versiones definitivas.

### Límite importante

El creeper de Mateo y el jugador de SaDez7 se usan como condiciones; la explosión local afecta a otra entidad de Lucas. Esto **no demuestra por sí solo que las dos reglas de explosión de los compañeros se apliquen a la misma jugada**. El escenario se conserva como provisional hasta acordar o confirmar una integración que cumpla la consigna. Tampoco hay confirmación de que la cátedra acepte inferir rechazos de listas.

## 4. Preparar objetivos en las bases de los compañeros

Antes de escribir datos en una API ajena, acordar qué entidades se usarán y quién puede modificarlas. Las simulaciones exitosas de los compañeros pueden guardar cambios en sus propias bases. Crear entidades de prueba **no ejecuta** ninguna explosión.

1. **Mateo:** su documentación de explosión admite `Player` o `Zombie` como objetivos. Cada solicitud de `POST /entidades` debe ajustarse al OpenAPI de la versión que esté ejecutando. Su API asigna un ID numérico. Para la explosión, el contrato recibe la distancia de cada objetivo; crear el objetivo no establece por sí solo esa distancia.
2. **SaDez7:** su documentación indica que se puede crear un `jugador` mediante `POST /entidades`, con ID y posición, y después referenciarlo en `POST /simulacion/explosiones`. Para su simulación también pueden hacer falta un creeper y bloques propios, según el cuerpo solicitado.
3. **Lucas:** puede crear una TNT y un objetivo local con `POST /api/tnts` y `POST /api/entidades`. La ubicación y salud determinan el resultado de su regla local.

Anotar en un lugar compartido los IDs, tipos, posiciones, salud inicial y commit de cada API. Los IDs de distintos proyectos son independientes: un `steve` de SaDez7 no es automáticamente el mismo registro que una entidad de Lucas.

**No se conocen todavía los IDs definitivos ni la jugada final.** No asumir que la operación provisional de Lucas va a dañar a los objetivos remotos que se creen.

## 5. Rutas de Lucas que pueden usar los compañeros

### Validar una explosión sin modificar la base

```http
POST /api/entidades/{id}/explosiones/validar
Content-Type: application/json

{"posicion":{"x":0,"y":0,"z":0}}
```

La entidad debe existir en la base de Lucas. La ruta calcula el daño hipotético de TNT (radio 4 bloques, hasta 8 de daño), sin modificar su salud, guardar una explosión ni consumir una TNT. Responde 200 si valida, 400 por posición inválida, 404 si la entidad no existe y 409 si no tiene vida. La respuesta `saludActual` es **simulada**. Esta ruta no busca ni comprueba el estado de una TNT persistida.

### Aplicar una explosión y guardar el cambio

`POST /api/entidades/{id}/explosiones` recibe el mismo cuerpo, pero actualiza la salud de la entidad de Lucas. No necesita un `tntId` ni registra una TNT consumida.

`POST /api/explosiones` es otra operación: recibe `tntId` y `objetivoId` de Lucas, consume la TNT local, actualiza el objetivo y registra la explosión en la base de Lucas. Esa TNT debe existir y no haber sido detonada.

Una transacción en la base de un compañero **no revierte automáticamente** los cambios ya confirmados en la base de otro. Incluso si se validan todos los participantes antes, una falla posterior durante los cambios puede dejar efectos parciales entre servidores. Acordar el orden y los efectos antes de ejecutar operaciones de escritura remotas.

## 6. Cómo repetir la prueba provisional de Lucas

Importar `insomnia/minecraft-poo09.json`, ajustar las tres URLs e IDs y ejecutar las solicitudes según su nombre:

1. Consultar OpenAPI y listas de A2/A3; anotar IDs realmente disponibles.
2. Crear un objetivo y una TNT en A1 con IDs nuevos. Consultar `GET /api/entidades/{id}` y `GET /api/tnts/{id}` y anotar salud y estado.
3. Solicitar una jugada con un `creeperA2` positivo que no esté en la lista: A1 debe devolver 422 indicando A2. Comprobar que la salud y la TNT de A1 no cambiaron.
4. Repetir con un creeper válido y un `jugadorA3` que no esté en la lista: A1 debe devolver 422 indicando A3. Comprobar nuevamente el estado.
5. Enviar IDs presentes en ambas listas y usar una TNT local nueva. Si las condiciones locales son válidas, A1 devuelve 201 con `id` y `explosionId`. Consultar `/api/jugadas/{id}`, `/api/explosiones/{explosionId}`, la entidad y la TNT locales.

Para el caso de ejemplo con objetivo local en `(0,0,0)`, salud 20 y TNT local en `(3,0,0)`, la regla de Lucas causa 8 de daño: salud esperada 12 y TNT detonada. No usar la misma TNT para repetir el caso exitoso.

La verificación completa debe comparar también la cantidad de filas en `JUGADAS_CRUZADAS` y `EXPLOSIONES` de A1 antes y después de los rechazos. Un `GET` de lista externa que devuelve HTTP 200 no debe presentarse como rechazo explícito de la API externa.

## 7. Qué falta acordar y entregar

- Elegir una acción concreta con los mismos datos de jugada para las reglas de Mateo y SaDez7; identificar qué respuestas representan aceptación o rechazo.
- Confirmar si sus endpoints de simulación modifican datos y si pueden exponer validaciones sin guardar. Las validaciones sin efectos reducen cambios prematuros, pero no hacen atómicas tres bases de datos.
- Adaptar la implementación de A1 si la cátedra requiere validaciones explícitas de las reglas de explosión; no afirmar que la implementación provisional ya lo cumple.
- Confirmar los commits definitivos y OpenAPI de cada compañero; repetir éxito y ambos rechazos con esas versiones y conservar evidencia.
- Completar README, bitácora, colección Insomnia y el documento aplicado para Classroom. Agregar el enlace al **commit final propio**, que aún no existe.
- Practicar la defensa oral individual: explicar escenario, flujo A1/A2/A3, reglas, errores, H2, transacción local y lo que cambia en cada base. El enunciado del challenge no especifica la rúbrica detallada de la defensa; consultar Classroom.
