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

En Swagger de Mateo (`POST /entidades`) crear un creeper de prueba:

```json
{"tipo":"Creeper","vida":20,"danoBase":10,"velocidad":2}
```

Anotar el `id` numérico. En Swagger de Santiago crear dos entidades con `POST /entidades`:

```json
{"tipo":"creeper","id":"creeper-a3","salud":20,"saludMaxima":20,"experiencia":0,"x":0,"y":0,"z":0}
```

```json
{"tipo":"jugador","id":"steve-prueba","salud":20,"saludMaxima":20,"experiencia":0,"x":2,"y":0,"z":0}
```

Para recorrer los tres casos de la colección, crear con `POST /bloques` un bloque de prueba y anotar su ID (en una invocación directa a la API el bloque es opcional):

```json
{"tipo":"tierra","x":1,"y":0,"z":0}
```

La explosión puede dañar ese jugador y destruir ese bloque en la base de Santiago. Acordar con él que son de prueba. En Lucas, crear cerdo y TNT con Insomnia 01 y 02.

## 3. Ejecutar los tres casos

Importar `insomnia/minecraft-poo09.json` y ajustar URLs, IDs y radio en el entorno. `creeper_a2` y `bloque_a3` deben ser los IDs numéricos devueltos al crearlos; `jugador_a3` y `creeper_a3` son los IDs de Santiago. Ejecutar 05 con creeper inexistente (422 A2) y 06 con un bloque inexistente (Santiago responde 404; A1 traduce a 422 A3). Verificar con 03 y 04 que el objetivo y TNT de Lucas no cambiaron. Después ejecutar 07 con el bloque real y consultar la jugada mediante 08.

Con el ejemplo, Mateo simula 10 puntos de daño sin escribir su base; Santiago aplica 12 de daño al jugador y destruye el bloque en su base; Lucas aplica 8 al cerdo y consume su TNT. Para repetir el éxito usar otro jugador con salud completa, otro bloque y otra TNT. Si se pierde la conexión después de llamar a Santiago, revisar sus datos antes de reintentar: su cambio podría haber quedado guardado sin registro en Lucas.

## 4. Qué explicar en la defensa

Una API ofrece operaciones HTTP; OpenAPI las describe y Swagger permite probarlas. El controller recibe el pedido, `JugadaCruzadaService` coordina los clientes WebClient, y los repositorios guardan en H2. La transacción garantiza los cambios de Lucas como unidad, no puede revertir cambios ya confirmados en otra base. Explicar también los límites: el jugador simulado tiene salud completa, Mateo calcula daño hipotético, mientras que Santiago guarda el daño y la destrucción del bloque en su propia base.

`POST /api/entidades/{id}/explosiones/validar` permite que los compañeros consulten la regla de Lucas sin escribir. `POST /api/entidades/{id}/explosiones` sí cambia la salud de la entidad local. `POST /api/explosiones` además usa y consume una TNT existente.
