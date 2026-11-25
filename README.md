# lfadul-tp-lp3-2026

API REST de ejemplo para representar entidades de Minecraft con Spring Boot,
Maven y Java 21.

## Ejecutar

```bash
cd Minecraft
./mvnw spring-boot:run
```

Endpoints principales:

- `GET /`
- `GET /jugador?id=Alex&saludMaxima=30&experiencia=10&x=1&y=2&z=3`
- `GET /entidades`
- `GET /explosivos`
- `GET /ui-capables`

## Pruebas

```bash
cd Minecraft
./mvnw test
```

## Modelo del dominio

```mermaid
classDiagram
    class Entidad {
        <<abstract>>
        -String id
        -Vector posicion
        -int salud
        -int saludMaxima
        +getComportamiento() String*
        +moverse() void
    }

    class Jugador {
        -Inventario inventario
        -int experiencia
        +Jugador(String id)
        +Jugador(String id, int saludMaxima)
        +Jugador(String id, int saludMaxima, int experiencia, Vector posicion)
        +interactuar(Entidad entidad) void
        +construir(Bloque bloque) void
        +getComportamiento() String
    }

    class Mob {
        <<abstract>>
        -ComportamientoIA ia
        +generarBotin() List~Object~
        +deambular() void
    }

    class Hostil {
        <<interface>>
    }

    class ComportamientoIA {
        <<interface>>
        +actuar(Mob mob) void
    }

    class AtacaJugador {
        <<interface>>
        +atacar(Entidad atacante, Entidad objetivo) void
    }

    class Explosivo {
        <<interface>>
        +explotar() String
    }

    class UICapable {
        <<interface>>
        +getImagen() String
        +getPosicion() Vector
        +getSonido() String
        +getVelocidad() double
        +mostrarEnUI() String
    }

    class Pacifico {
        <<abstract>>
    }

    class Esqueleto {
        +Esqueleto(String id)
        +Esqueleto(String id, int saludMaxima)
        +getComportamiento() String
    }

    class Enderman {
        +getComportamiento() String
    }

    class zombie {
        +getComportamiento() String
    }

    class Creeper {
        -String imagen
        -String sonido
        -double velocidad
        +explotar() String
        +mostrarEnUI() String
        +getComportamiento() String
    }

    class Bloque {
        -String tipo
    }

    class Tnt {
        -String imagen
        -Vector posicion
        -String sonido
        -double velocidad
    }

    class Aldeano {
        -Profesion profesion
        +ofrecerTrueques() List~Object~
        +getComportamiento() String
    }

    class Cerdo {
        +getComportamiento() String
    }

    Entidad <|-- Jugador
    Entidad <|-- Mob
    Mob <|-- Pacifico
    Mob <|-- Esqueleto
    Mob <|-- Enderman
    Mob <|-- zombie
    Mob <|-- Creeper
    Pacifico <|-- Aldeano
    Pacifico <|-- Cerdo
    Hostil <|.. Esqueleto
    Hostil <|.. Enderman
    Hostil <|.. zombie
    Hostil <|.. Creeper
    Explosivo <|.. Creeper
    Explosivo <|.. Tnt
    UICapable <|.. Creeper
    UICapable <|.. Tnt
    Bloque <|-- Tnt
    AtacaJugador ..> Entidad
    Mob --> ComportamientoIA
    Jugador --> Inventario
    Inventario --> Bloque
    Entidad --> Vector
```

## Licencia y organización

[Apache License 2.0](LICENSE). Trabajo individual de Lucas Fadul, dominio Minecraft.
Java 21 o superior. El Maven Wrapper está en `Minecraft/`.

Siguiendo el [template](https://github.com/alefq/lp3-template-tp/tree/main/src/main/java/py/edu/uc/lp3), bajo `Minecraft/src/main/java/py/edu/uc/lp3/`:

- `Application.java`: arranque de Spring Boot y escaneo de subpaquetes.
- `domain/`: clases del modelo; `domain/exception/`: excepciones del dominio.
- `persistence/`: entidades JPA y repositorios.
- `service/`: servicios de entidades, simulación y clientes HTTP.
- `rest/controller/`: controladores REST y manejo de errores HTTP.
- `rest/dto/` y `rest/response/`: solicitudes y respuestas JSON.

## Sobrecarga y sobreescritura

**Sobrecarga:** `Jugador(String)` usa salud 20 y experiencia 0;
`Jugador(String, int)` permite elegir la salud máxima;
`Jugador(String, int, int, Vector)` recibe también experiencia y posición.
Las firmas delegan con `this` y la hija llama a `super(id, saludMaxima)`.
`Esqueleto` y `Cerdo` tienen constructores con id solo (salud 20 y 10)
y con id más salud. `Vector()` y `Vector(double, double, double)` se conservan.

Además, `ReglasCombateService.atacarConEsqueleto(Entidad)` delega en la
sobrecarga que recibe distancia: sin distancia se asume 0; hasta 16 bloques
inflige 4 de daño y fuera de alcance no hace daño. Una distancia negativa o no
finita se rechaza en el servicio.

**Sobreescritura:** `Entidad.getComportamiento()` es abstracto.
`Esqueleto` y `Cerdo` implementan la misma firma con textos diferentes;
`Jugador` y las otras entidades concretas también la implementan.
`GET /entidades` devuelve `List<Entidad>`: el JSON incluye `comportamiento`
calculado por cada implementación concreta. El controller usa el tipo padre.

## Interfaz compartida entre familias distintas

`Explosivo` representa una capacidad compartida por clases sin un padre común
del dominio. `Creeper` pertenece a la familia `Entidad -> Mob`, mientras que
`Tnt` pertenece a la familia `Bloque`. Las dos implementan la interfaz y pueden
tratarse como `Explosivo`.

La interfaz define `explotar()` con un mensaje `default`. `Tnt` conserva esa
implementación y `Creeper` la reemplaza con `@Override`. El endpoint
`GET /explosivos` crea ambos objetos, los utiliza mediante el tipo de la interfaz
y devuelve el resultado de `explotar()` como JSON.

`UICapable` modela la capacidad de aparecer en una interfaz de usuario. Define
mensajes para obtener imagen, posición, sonido y velocidad, y el mensaje textual
`mostrarEnUI()`. `Creeper` y `Tnt` guardan sus propios datos: el Creeper reemplaza
el mensaje `default` y la TNT lo conserva. `GET /ui-capables` trata los dos objetos
como `UICapable` y devuelve sus propiedades y mensajes en JSON.

## Reglas y errores HTTP

`ReglasCombateService` valida id no vacío, salud máxima positiva, experiencia
no negativa y coordenadas finitas antes de construir el jugador para `GET /jugador`.
Las entradas inválidas responden HTTP 400 con `{"error":"..."}`; un número con
formato incorrecto también recibe HTTP 400 por Spring. Los servicios rechazan daño
y curación negativos y limitan la salud entre 0 y su máximo.

```bash
curl http://localhost:8080/
curl 'http://localhost:8080/jugador?id=Alex&saludMaxima=30&experiencia=10&x=1&y=2&z=3'
curl http://localhost:8080/entidades
curl http://localhost:8080/explosivos
curl http://localhost:8080/ui-capables
curl -i 'http://localhost:8080/jugador?saludMaxima=0'
```

El último pedido debe responder 400. Las pruebas verifican construcción, JSON,
polimorfismo, errores del dominio, alcance del ataque y encapsulamiento.
Las especificaciones de entrega están en [docs/ESPECIFICACIONES-POO08.md](docs/ESPECIFICACIONES-POO08.md).
La asistencia de IA se registra en [BITACORA.md](BITACORA.md).

## Simulación REST de explosiones

Regla de negocio: una TNT causa 8 puntos de daño a una entidad viva a una distancia de hasta 4 bloques, sin reducir la salud por debajo de cero; fuera del radio no causa daño.

`ReglasExplosionService` aplica la regla de daño y calcula la distancia en tres dimensiones con los datos de `Vector` y la entidad persistida conserva la salud resultante.
El controlador delega en `EntidadService`, que recupera el objetivo y guarda la salud resultante mediante Spring Data JPA en H2.

Crear un objetivo y simular la explosión:

```bash
curl -X POST 'http://localhost:8080/api/entidades' \
  -H 'Content-Type: application/json' \
  -d '{"id":"objetivo-tnt","tipo":"CERDO","saludMaxima":10,"posicion":{"x":0,"y":0,"z":0}}'

curl -X POST 'http://localhost:8080/api/entidades/objetivo-tnt/explosiones' \
  -H 'Content-Type: application/json' \
  -d '{"posicion":{"x":3,"y":0,"z":0}}'
```

La segunda petición devuelve `saludAnterior: 10`, `saludActual: 2` y `danoAplicado: 8`.
Se puede consultar el resultado con `GET /api/entidades/objetivo-tnt` o con `SELECT * FROM ENTIDADES` en H2 Console.
La API responde 400 si falta la posición, 404 si el objetivo no existe y 409 si el objetivo ya no tiene vida.
Esta es una simulación simplificada con radio y daño fijos.

## Jerarquía de excepciones de Minecraft

Las excepciones del dominio están en `domain.exception` y forman esta jerarquía:

```text
RuntimeException
└── MinecraftException (abstracta)
    ├── EntidadException (abstracta)
    │   ├── EntidadInvalidaException
    │   ├── EntidadNotFoundException
    │   ├── EntidadConflictException
    │   └── EntidadSinVidaException
    ├── TntDetonadaException
    └── TntNotFoundException
```

`MinecraftException` agrupa los errores del juego y `EntidadException` los relacionados con sus criaturas y jugadores.
El servicio lanza `EntidadInvalidaException` ante datos inválidos (HTTP 400), `EntidadNotFoundException` cuando el objetivo no existe (404) y `EntidadConflictException` cuando el ID ya está registrado (409).
`ReglasExplosionService` lanza `EntidadSinVidaException` si el objetivo está muerto y `TntDetonadaException` si la TNT ya fue detonada; ambas se traducen a HTTP 409.
Cada tipo concreto tiene su propio método `@ExceptionHandler` en `ApiExceptionHandler`, que responde con `{"error":"mensaje"}`.


## POO-08: CRUD, H2 y simulación persistida

Desde la raíz del repositorio, con Java 21 o superior:

```bash
./mvnw spring-boot:run
```

El wrapper de la raíz ejecuta el proyecto `Minecraft`; también se puede ejecutar `cd Minecraft && ./mvnw spring-boot:run`. `GET /` confirma que el servicio está activo y `http://localhost:8080/h2-console` abre la consola H2.

Las propiedades están en `Minecraft/src/main/resources/application.properties`:

- `spring.application.name` asigna el nombre de la aplicación.
- `spring.datasource.url=jdbc:h2:file:${user.home}/.lp3-minecraft/minecraftdb` guarda H2 en un archivo de la carpeta personal del usuario; en la consola hay que reemplazar `${user.home}` por su ruta absoluta.
- `spring.datasource.driver-class-name=org.h2.Driver` selecciona el controlador JDBC de H2.
- `spring.datasource.username=sa` y `spring.datasource.password=` establecen el usuario y una contraseña vacía para desarrollo local.
- `spring.jpa.hibernate.ddl-auto=update` crea o actualiza el esquema según las entidades JPA sin borrar intencionalmente los datos existentes.
- `spring.jpa.open-in-view=false` cierra la sesión de persistencia al salir de la transacción de servicio.
- `spring.h2.console.enabled=true` habilita la consola web de H2 para desarrollo.

En H2 Console, el JDBC URL es `jdbc:h2:file:/ruta/absoluta/al/home/.lp3-minecraft/minecraftdb`, el usuario es `sa` y la contraseña está vacía. Las tablas `ENTIDADES`, `TNTS` y `EXPLOSIONES` se pueden consultar con `SELECT * FROM ENTIDADES;` y consultas equivalentes. El archivo H2 incluido previamente en `Minecraft/data/` es una copia anterior; la aplicación usa la ruta configurada en `application.properties`.

**Regla 1:** una `Tnt` causa 8 de daño a una `Entidad` viva situada a 4 bloques o menos y la salud nunca baja de cero.

**Regla 2:** una `Tnt` se consume al detonar, incluso si el objetivo está fuera del radio, y no puede volver a usarse.

En `POST /api/explosiones`, `ReglasExplosionService` comprueba ambas reglas, calcula la distancia en tres dimensiones y determina el daño. La transacción persiste la salud, el estado de la TNT y el registro de la explosión; si se rechaza una operación no se consume la TNT ni se guarda una explosión.

```mermaid
classDiagram
    class Entidad {
        <<abstract>>
        +getComportamiento() String
        +recibirDano(int)
    }
    class Mob
    class Pacifico
    class Bloque
    class Cerdo
    class Jugador
    class Tnt {
    }
    class Vector
    class ReglasExplosionService
    class Explosivo {
        <<interface>>
        +explotar() String
    }
    class Creeper
    class EntidadEntity {
        <<abstract>>
    }
    class CerdoEntity
    class JugadorEntity
    class EntidadRepository {
        <<interface>>
    }
    class EntidadController
    class EntidadService
    class ExplosionService
    class SimulacionController
    class MinecraftException {
        <<abstract>>
    }
    class EntidadException {
        <<abstract>>
    }
    class EntidadSinVidaException
    class TntDetonadaException
    class ApiExceptionHandler
    Entidad <|-- Mob
    Mob <|-- Pacifico
    Pacifico <|-- Cerdo
    Entidad <|-- Jugador
    Mob <|-- Creeper
    Bloque <|-- Tnt
    Explosivo <|.. Tnt
    Explosivo <|.. Creeper
    Tnt --> Vector : posición
    ExplosionService --> ReglasExplosionService : aplica reglas
    EntidadEntity <|-- CerdoEntity
    EntidadEntity <|-- JugadorEntity
    EntidadController --> EntidadService
    SimulacionController --> ExplosionService
    EntidadService --> EntidadRepository
    EntidadRepository --> EntidadEntity
    MinecraftException <|-- EntidadException
    EntidadException <|-- EntidadSinVidaException
    MinecraftException <|-- TntDetonadaException
    ApiExceptionHandler ..> EntidadSinVidaException : maneja
    ApiExceptionHandler ..> TntDetonadaException : maneja
```

### Respuestas a «Qué hay que pensar»

- **Recurso y verbo:** `/api/entidades`, `/api/tnts` y `/api/explosiones` nombran recursos; el verbo HTTP indica si se crean, consultan, actualizan o eliminan.
- **Identidad:** el cliente asigna el `id` de entidades y TNT en el JSON; H2 asigna el `id` numérico de cada explosión.
- **Herencia y tablas:** `EntidadEntity` es la base JPA y las hijas usan `SINGLE_TABLE`, con un discriminador `tipo`; el JSON también lleva `tipo` para elegir y reconocer el subtipo. La tabla única evita joins entre tablas de la misma familia y conserva los tipos ya existentes.
- **Interfaz y repositorio:** `EntidadController` depende de `EntidadService`, que usa la interfaz Spring Data `EntidadRepository`; Spring implementa la persistencia, como el protocolo `Explosivo` permite usar distintas implementaciones sin depender de una clase concreta.
- **Protocolo del dominio:** `GET /explosivos` invoca `Explosivo.explotar()` sobre `Creeper` y `Tnt`, de las familias `Entidad` y `Bloque`.
- **Ocultamiento:** `ReglasExplosionService` valida alcance, daño y posición para la explosión; `EntidadService` valida la salud antes de persistirla. Los constructores JPA sin argumentos son protegidos. Las validaciones y reglas de salud y combate se trasladaron a servicios. Los pedidos 08 y 09 de Insomnia muestran que un POST con salud máxima 0 no crea una entidad.
- **PUT y PATCH:** se usa `PUT`, que reemplaza los datos editables; `id`, `tipo` y `saludMaxima` son obligatorios y el tipo no puede cambiar. Si se omiten, `salud` vuelve a la máxima, `posicion` a `(0,0,0)`, `experiencia` a 0 y la profesión de aldeano a `SIN_PROFESION`; no se implementa PATCH.
- **DELETE:** borra físicamente la entidad de la tabla y responde 204; los registros históricos de explosiones conservan los IDs que registraron.
- **Dos reglas y persistencia:** los servicios aplican daño por distancia y consumo de TNT; el recurso `/api/explosiones` registra la simulación y los GET posteriores de la entidad y la TNT muestran ambos estados guardados.
- **Excepciones:** `MinecraftException` y `EntidadException` son bases; hay tipos para datos inválidos, inexistencia, conflicto, entidad sin vida, TNT inexistente y TNT detonada. Son no comprobadas (`RuntimeException`) porque describen errores de dominio que el servicio deja propagar al handler; cada tipo concreto tiene su handler y el JSON lleva `error`.

### Colección de Insomnia

Importar `insomnia/minecraft-poo08.json` mediante **Import → File** en Insomnia. El entorno define `base_url` como `http://localhost:8080`; ejecutar los pedidos numerados en orden: CRUD, POST ilegal y verificación, protocolo, creación de TNT, simulación, consultas del estado, errores y borrado. Copiar el `id` numérico devuelto por la simulación a la variable de entorno `explosion_id` antes del pedido 16. Para repetir el recorrido, usar una base H2 nueva o cambiar los IDs fijos en las peticiones; la TNT detonada no se reutiliza.

## Commit de la solución

### POO-08

[Ver solución POO-08](https://github.com/LucasFadul/lfadul-tp-lp3-2026/commit/bf8efcafbcb9f9ef5ab980ce636536a461a1161b)

### POO-06

[Ver solución POO-06](https://github.com/LucasFadul/lfadul-tp-lp3-2026/commit/a3629c95a2755ec82ca19cbc3b7f3c329f4d8bb7)

Este commit contiene código, pruebas y especificaciones. El commit posterior
únicamente incorpora este enlace de entrega.

### Consulta de objetos externos

El endpoint `GET /api/objetos-externos` consulta con Spring WebClient el listado de `https://api.restful-api.dev/objects`. El campo `data` puede contener distintos atributos o ser `null`. Para cambiar el servidor de origen, configure `api.objetos.base-url` en `Minecraft/src/main/resources/application.properties`.

```bash
curl http://localhost:8080/api/objetos-externos
```

### Documentación de la API

Con la aplicación iniciada, Swagger UI está disponible en [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). Permite explorar y probar los servicios REST. El contrato OpenAPI también está disponible en [JSON](http://localhost:8080/v3/api-docs) y [YAML](http://localhost:8080/v3/api-docs.yaml).

### Integración con la API del compañero

`GET /api/integracion/esqueletos/{esqueletoId}/jugadores/{jugadorId}/disparo` compara un ataque simulado entre dos entidades locales persistidas con la acción que devuelve la API del compañero. Acepta los parámetros opcionales `danoBase` (4), `velocidad` (1) y `usaArco` (true), usados únicamente en la consulta externa. La distancia entre posiciones locales se redondea hacia arriba para enviarla como número entero a esa API. El daño local utiliza la regla propia del proyecto; la acción externa se informa por separado. La simulación no modifica las entidades.

La dirección del compañero se configura con `api.companero.base-url` en `application.properties`. Su servidor debe estar disponible desde la aplicación para que la consulta funcione. El endpoint también está disponible en Swagger UI, en **Integración con compañero**.

### POO-09: grupo y reglas

| Rol | Alumno y repositorio | URL observada por Hamachi | OpenAPI | Reglas publicadas |
| --- | --- | --- | --- | --- |
| A1 | [Lucas Fadul](https://github.com/LucasFadul/lfadul-tp-lp3-2026) | `http://25.30.124.10:8080` | [A1](http://25.30.124.10:8080/v3/api-docs) | La TNT hace hasta 8 de daño a cuatro bloques o menos; se consume y no puede detonarse otra vez. |
| A2 | [Mateo Ríos](https://github.com/MatRF04/mr-taller-git-2026) | `http://25.28.15.25:8080` | [A2](http://25.28.15.25:8080/v3/api-docs) | Un creeper vivo explota una sola vez; causa hasta 10 de daño a tres bloques o menos. [Reglas de Mateo](https://github.com/MatRF04/mr-taller-git-2026#simulación-de-explosiones). |
| A3 | [Santiago Baez](https://github.com/SaDez7/sb-taller-git-2026) | `http://25.29.140.0:8080` | [A3](http://25.29.140.0:8080/v3/api-docs) | El daño de explosión disminuye con la distancia; un bloque dentro del radio queda destruido. [Reglas de Santiago](https://github.com/SaDez7/sb-taller-git-2026#las-dos-reglas-de-negocio). |

Esas IP eran válidas en la sesión de Hamachi del 23/11; se pueden cambiar con variables de entorno. Versiones públicas consultadas el 25/11: [Mateo `8f32d13`](https://github.com/MatRF04/mr-taller-git-2026/commit/8f32d13a4857be25d8b1ab86fe552449c0cd83ab) y [Santiago `763df1c`](https://github.com/SaDez7/sb-taller-git-2026/commit/763df1c80a8882f443be6489e2aefaa8d6de0b24). Son versiones probadas, no una afirmación de que sus autores no harán más cambios.

### Escenario y límites

Una explosión en el origen involucra un creeper de Mateo, un jugador y un creeper de Santiago, y una TNT y objetivo locales. `POST /api/jugadas` recibe `creeperA2`, `jugadorA3`, `creeperA3`, `radioA3`, `tntId`, `objetivoId` y opcionalmente `bloqueA3`. Los IDs externos representan registros separados; el creeper de Santiago debe estar vivo en `(0,0,0)`, origen supuesto por la simulación de Mateo.

1. A1 comprueba que su TNT y objetivo permitan una explosión. Busca el creeper en `GET /entidades/creepers/listos` de Mateo y el jugador en `GET /entidades/vivos` de Santiago. A1 atribuye las ausencias a A2 o A3 desde listas HTTP 200; estas ausencias no son un rechazo explícito remoto.
2. A1 consulta `GET /jugador` y `GET /entidades/{id}` de Santiago. El jugador debe tener salud completa: la simulación de Mateo construye un jugador temporal con salud máxima y no admite otra salud actual.
3. A1 llama `GET /simulaciones/creeper/jugador` de Mateo con la salud máxima y posición de Santiago. Mateo aplica su regla de hasta 10 de daño dentro de tres bloques; es una **simulación sin modificar** su base.
4. A1 llama `POST /simulacion/explosiones` de Santiago con el creeper, jugador, radio y bloque opcional. Santiago aplica su regla de daño decreciente y, si hay bloque, su regla de destrucción. **Esta llamada modifica la salud del jugador y puede destruir el bloque en la base de Santiago.** Un 4xx remoto se traduce a 422 con origen A3.
5. Solo si Santiago acepta, A1 ejecuta su explosión local y guarda la jugada, el daño simulado por Mateo, el daño aplicado por Santiago y el estado del bloque. `GET /api/jugadas/{id}` permite consultarla. Los errores locales se comprueban antes de la escritura remota y también se verifican durante la transacción local.

Si Mateo o Santiago rechaza, A1 no guarda jugada ni explosión. La operación de Santiago usa su propia transacción. **No hay transacción distribuida:** si Santiago confirma y después falla A1, o si se pierde la respuesta de Santiago, su base podría haber cambiado sin que A1 registre la jugada; no reintentar a ciegas. Antes de probar acordar con Santiago que los recursos son de prueba. Una explosión exitosa deja al jugador con menos salud, por lo que para repetir se necesita un jugador con salud completa y una TNT local nueva.

### Corrección POO-08 (21/24)

- H2 generado: `Minecraft/data/minecraftdb.mv.db` ya no está versionado; `.gitignore` excluye archivos de H2. La aplicación usa `${user.home}/.lp3-minecraft`.
- Capas y validaciones: se retiraron las excepciones de las entidades y se movieron las validaciones y el cálculo de TNT a `ReglasCombateService`, `ReglasExplosionService`, `EntidadService` y `ExplosionService`. El modelo conserva métodos de comportamiento ilustrativos de POO anteriores; corresponde a la cátedra valorar esa interpretación de la observación sobre dominio.
- README: se actualizaron la organización de paquetes, el vínculo a especificaciones y la jerarquía de excepciones.

### Arranque local de las tres versiones

Clonar los repositorios y usar una terminal por copia. Para el proyecto propio seleccionar el commit de entrega. Para las copias de los compañeros:

```bash
# Terminal 1: Santiago (A3)
git clone https://github.com/SaDez7/sb-taller-git-2026.git
cd sb-taller-git-2026
git checkout 763df1c80a8882f443be6489e2aefaa8d6de0b24
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
```

```bash
# Terminal 2: Mateo (A2). A2_URL es la ubicación de Santiago para Mateo.
git clone https://github.com/MatRF04/mr-taller-git-2026.git
cd mr-taller-git-2026
git checkout 8f32d13a4857be25d8b1ab86fe552449c0cd83ab
A2_URL=http://localhost:8082 ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

```bash
# Terminal 3: Lucas (A1), desde la raíz de este repositorio
API_A2_BASE_URL=http://localhost:8081 API_A3_BASE_URL=http://localhost:8082 ./mvnw spring-boot:run
```

OpenAPI local: `http://localhost:8080/v3/api-docs`, `http://localhost:8081/v3/api-docs` y `http://localhost:8082/v3/api-docs`. Swagger propio: `http://localhost:8080/swagger-ui/index.html`. Con Hamachi, reemplazar las URLs por las IP vigentes; Mateo también debe configurar su propia `A2_URL` para llegar a Santiago. Los servicios usan bases independientes.

### Insomnia y comprobación

Importar `insomnia/minecraft-poo09.json`, ajustar `a1_url`, `a2_url`, `a3_url`, `creeper_a2`, `jugador_a3`, `creeper_a3` y `bloque_a3`. Crear un creeper en Mateo y, en Santiago, un creeper en el origen, un jugador con salud completa y un bloque opcional; usar los cuerpos descritos en [la guía](docs/GUIA-COORDINACION-POO09.md). Ejecutar los pedidos 01 y 02 para crear objetivo y TNT locales con IDs nuevos. Ejecutar los rechazos 05 y 06, verificar que la TNT y el objetivo siguen iguales y, finalmente, el feliz 07. Copiar el ID retornado para el GET 08. Después del éxito, la TNT local no se puede usar de nuevo. La evidencia HTTP de copias locales y bases temporales está en [docs/EVIDENCIA-POO09.md](docs/EVIDENCIA-POO09.md).

`POST /api/entidades/{id}/explosiones/validar` permite a compañeros calcular la regla de TNT de Lucas sin cambiar salud ni consumir una TNT. La operación valida una explosión hipotética: no comprueba una TNT persistida.
