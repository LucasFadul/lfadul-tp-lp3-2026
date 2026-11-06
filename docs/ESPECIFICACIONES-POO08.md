# Especificaciones aplicadas — POO-08 Minecraft

## Objetivo

Publicar un servicio REST de Minecraft con entidades polimórficas persistidas en H2 y una simulación de explosión que conserva el estado de la TNT, el objetivo y el resultado.

## Arranque

Con Java 21 o superior, desde la raíz ejecutar `./mvnw spring-boot:run`. H2 usa `jdbc:h2:file:${user.home}/.lp3-minecraft/minecraftdb`; la consola está en `http://localhost:8080/h2-console`, usuario `sa` y contraseña vacía. En la consola reemplazar `${user.home}` por la ruta absoluta.

## Recursos HTTP

- `GET /`: disponibilidad; `GET /explosivos`: protocolo `Explosivo` para Creeper y TNT.
- `POST /api/entidades`, `GET /api/entidades`, `GET /api/entidades/{id}`, `PUT /api/entidades/{id}` y `DELETE /api/entidades/{id}`: CRUD de entidades; `tipo` identifica la hija.
- `POST /api/tnts` y `GET /api/tnts/{id}`: crear y consultar TNT.
- `POST /api/explosiones` con `tntId` y `objetivoId`: detonar y guardar el resultado; `GET /api/explosiones/{id}` consulta una simulación.
- `POST /api/entidades/{id}/explosiones` conserva la simulación previa por posición; la simulación con consumo persistido es `/api/explosiones`.

## Invariantes y reglas

- El ID es obligatorio y único; la salud máxima es positiva y la salud está entre cero y el máximo. Las coordenadas son finitas y la experiencia no es negativa.
- Una TNT causa 8 de daño a una entidad viva a una distancia de hasta 4 bloques; la salud no baja de 0.
- Una TNT se consume al detonar y no puede volver a usarse; una operación rechazada no consume la TNT.
- La creación responde 201, las lecturas y actualizaciones 200 y el borrado 204. Datos inválidos responden 400, recursos inexistentes 404 y duplicados, objetivo muerto o TNT consumida 409, con un mensaje JSON `error`.

## Cómo probar

Importar `insomnia/minecraft-poo08.json`, usar la URL base `http://localhost:8080` y ejecutar los pedidos numerados. Crear cerdo y TNT, simular, consultar ambos estados, intentar usar la TNT otra vez y observar el 409. Copiar el ID de explosión devuelto a la variable `explosion_id`; consultar la tabla `ENTIDADES`, `TNTS` y `EXPLOSIONES` desde H2. El README describe el resto del recorrido, los errores y las decisiones de diseño.
