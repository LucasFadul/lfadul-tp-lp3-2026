# Guía para coordinar y probar POO-09

Cada estudiante entrega su propio proyecto. En el escenario de Lucas, A1 es Lucas, A2 es Mateo y A3 es Santiago. La [descripción completa, versiones y arranque](../README.md#poo-09-grupo-y-reglas) está en el README.

## 1. Iniciar los servicios

En tres computadoras con Hamachi, cada uno inicia su aplicación. Lucas puede configurar las direcciones conocidas así:

```bash
API_A2_BASE_URL=http://25.28.15.25:8080 \
API_A3_BASE_URL=http://25.29.140.0:8080 \
./mvnw spring-boot:run
```

Mateo debe configurar `A2_URL` con la dirección de Santiago porque su simulación consulta al servicio de Santiago. Si las IP cambiaron, actualizar las variables y reiniciar. Para una sola computadora usar puertos y comandos del README.

## 2. Preparar participantes

En Swagger de Mateo (`POST /entidades`) crear un creeper, con permiso del dueño, por ejemplo:

```json
{"tipo":"Creeper","vida":20,"danoBase":10,"velocidad":2}
```

Anotar el `id` numérico de la respuesta. En Swagger de Santiago (`POST /entidades`) crear un jugador de prueba con salud completa:

```json
{"tipo":"jugador","id":"steve-prueba","salud":20,"saludMaxima":20,"experiencia":0,"x":2,"y":0,"z":0}
```

Los nombres deben ser nuevos si ya existen. En Lucas, crear un cerdo y una TNT usando Insomnia 01 y 02. El programa necesita la posición y salud del jugador de Santiago y el creeper listo de Mateo. Los IDs son distintos en cada base.

## 3. Ejecutar los tres casos

Importar `insomnia/minecraft-poo09.json` y ajustar URLs, IDs del creeper y jugador, y nombres de objetivo y TNT. Ejecutar 05 con creeper inexistente (422, A2), 06 con jugador inexistente (422, A3) y comprobar con 03 y 04 que la salud y la TNT de Lucas no cambiaron. Luego ejecutar 07 con datos válidos y consultar la jugada con 08 usando el ID retornado. La TNT se consume y se necesita otra para un nuevo éxito. La evidencia del caso está en [EVIDENCIA-POO09.md](EVIDENCIA-POO09.md).

El creeper y el jugador se consultan mediante GET. La simulación de Mateo calcula un jugador temporal; **no cambia** la salud de Santiago ni consume el creeper de Mateo. El objetivo y la TNT que sí cambian están en la base de Lucas. La ausencia de un participante se deduce de la lista; no es un 422 enviado por el compañero.

## 4. Qué explicar en la defensa

Una API ofrece operaciones HTTP; OpenAPI las describe y Swagger permite probarlas. El controller recibe el pedido, `JugadaCruzadaService` coordina los clientes WebClient, y los repositorios guardan en H2. La transacción garantiza los cambios de Lucas como unidad, no puede revertir cambios ya confirmados en otra base. Explicar también los límites: el jugador simulado tiene salud completa, la regla explícita de A3 valida sus datos y el daño remoto es hipotético.

`POST /api/entidades/{id}/explosiones/validar` permite que los compañeros consulten la regla de Lucas sin escribir. `POST /api/entidades/{id}/explosiones` sí cambia la salud de la entidad local. `POST /api/explosiones` además usa y consume una TNT existente.
