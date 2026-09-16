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
