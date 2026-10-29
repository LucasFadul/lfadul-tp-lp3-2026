# Ejecutar la API

## Requisitos

- Java 21 o superior
- Maven Wrapper incluido en el proyecto

## Levantar la aplicacion

Desde la raiz del repositorio:

```bash
cd Minecraft
./mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

## Probar endpoints

Saludo inicial:

```bash
curl http://localhost:8080/
```

Jugador con parametros:

```bash
curl "http://localhost:8080/jugador?id=Alex&saludMaxima=30&experiencia=10&x=1&y=2&z=3"
```

CRUD de entidades (tipos disponibles: `JUGADOR`, `CERDO`, `ESQUELETO`, `CREEPER`, `ALDEANO` y `ZOMBIE`):

```bash
curl http://localhost:8080/api/entidades

curl -X POST http://localhost:8080/api/entidades \
  -H 'Content-Type: application/json' \
  -d '{"id":"Pig-1","tipo":"CERDO","saludMaxima":10,"posicion":{"x":1,"y":64,"z":-2}}'

curl http://localhost:8080/api/entidades/Pig-1

curl -X PUT http://localhost:8080/api/entidades/Pig-1 \
  -H 'Content-Type: application/json' \
  -d '{"id":"Pig-1","tipo":"CERDO","saludMaxima":12,"salud":8,"posicion":{"x":2,"y":64,"z":-2}}'

curl -X DELETE http://localhost:8080/api/entidades/Pig-1
```

Para crear un `JUGADOR`, se puede enviar `experiencia`. Para un `ALDEANO`, `profesion` es opcional y acepta los valores de `Profesion` (por ejemplo `GRANJERO`). Si se omiten `salud` o `posicion`, se usan la salud máxima y la posición `(0, 0, 0)`. H2 guarda los datos en `~/.lp3-minecraft/minecraftdb`; la consola está disponible en `/h2-console` (usuario `sa`, contraseña vacía).

## Ejecutar tests

```bash
cd Minecraft
./mvnw test
```

## Detener la aplicacion

En la terminal donde esta corriendo Spring Boot:

```text
Ctrl + C
```
