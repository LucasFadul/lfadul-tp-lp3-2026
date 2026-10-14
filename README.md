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
        +recibirDano(int cantidad) void
        +curar(int cantidad) void
        +estaVivo() boolean
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
        +atacar(Entidad objetivo) void
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
        +atacar(Entidad objetivo) void
        +atacar(Entidad objetivo, double distancia) void
        +getComportamiento() String
    }

    class Enderman {
        +atacar(Entidad objetivo) void
        +getComportamiento() String
    }

    class zombie {
        +atacar(Entidad objetivo) void
        +getComportamiento() String
    }

    class Creeper {
        -String imagen
        -String sonido
        -double velocidad
        +atacar(Entidad objetivo) void
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
- `domain/`: todas las clases del modelo y sus reglas.
- `rest/controller/`: controllers de inicio, jugadores y explosivos.
- `rest/response/`: respuestas JSON específicas de los servicios REST.

## Sobrecarga y sobreescritura

**Sobrecarga:** `Jugador(String)` usa salud 20 y experiencia 0;
`Jugador(String, int)` permite elegir la salud máxima;
`Jugador(String, int, int, Vector)` recibe también experiencia y posición.
Las firmas delegan con `this` y la hija llama a `super(id, saludMaxima)`.
`Esqueleto` y `Cerdo` tienen constructores con id solo (salud 20 y 10)
y con id más salud. `Vector()` y `Vector(double, double, double)` se conservan.

Además, `Esqueleto.atacar(Entidad)` delega en `atacar(Entidad, double)`:
sin distancia se asume 0; con distancia hasta 16 bloques inflige 4 de daño,
y fuera de alcance no hace daño. Una distancia negativa o no finita se rechaza.
Son firmas distintas de la misma acción en la misma clase.

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

El constructor valida id no vacío, salud máxima positiva, experiencia no negativa
y coordenadas finitas. El controller construye una instancia nueva con los valores
URL; no asigna vida a mano. El dominio rechaza valores inválidos y el controller
responde HTTP 400 con `{"error":"..."}`. Un número con formato incorrecto también
recibe HTTP 400 por Spring. La salud se mantiene entre 0 y su máximo; daño y
curación negativos se rechazan, y una curación grande no desborda.

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
Las especificaciones de entrega están en [docs/ESPECIFICACIONES.md](docs/ESPECIFICACIONES.md).
La asistencia de IA se registra en [BITACORA.md](BITACORA.md).

## Commit de la solución

[Ver solución POO-06](https://github.com/LucasFadul/lfadul-tp-lp3-2026/commit/a3629c95a2755ec82ca19cbc3b7f3c329f4d8bb7)

Este commit contiene código, pruebas y especificaciones. El commit posterior
únicamente incorpora este enlace de entrega.
