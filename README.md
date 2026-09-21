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

    class Pacifico {
        <<abstract>>
    }

    class Esqueleto {
        +atacar(Entidad objetivo) void
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

    class creeper {
        +atacar(Entidad objetivo) void
        +getComportamiento() String
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
    Mob <|-- creeper
    Pacifico <|-- Aldeano
    Pacifico <|-- Cerdo
    Hostil <|.. Esqueleto
    Hostil <|.. Enderman
    Hostil <|.. zombie
    Hostil <|.. creeper
    Mob --> ComportamientoIA
    Jugador --> Inventario
    Inventario --> Bloque
    Entidad --> Vector
```
