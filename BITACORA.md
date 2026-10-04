# Bitacora del Taller de Git y Spring Boot

## Datos de la entrega

- Estudiante: Lucas Fadul
- Dominio elegido: Minecraft
- Repositorio principal: <https://github.com/LucasFadul/lfadul-tp-lp3-2026>
- Repositorio del companero: <https://github.com/SaDez7/sb-taller-git-2026>
- Pull Request realizado: <https://github.com/SaDez7/sb-taller-git-2026/pull/1>
- Rama de colaboracion: `lf-contribucion-lp3`

## Objetivo

Publicar una API REST con Spring Boot que exponga un modelo orientado a objetos
del dominio Minecraft. El modelo debe aplicar herencia, sobreescritura,
polimorfismo y ocultamiento de la informacion. El trabajo tambien practica el
flujo colaborativo de Git mediante ramas, commits, push, Pull Request y revision
de codigo.

## Creacion y configuracion del repositorio

Se creo el repositorio publico `lfadul-tp-lp3-2026` en GitHub. El repositorio
incluye un archivo `README.md` y la licencia Apache License 2.0. Posteriormente
se incorporo el proyecto Spring Boot dentro de la carpeta `Minecraft`.

La aplicacion utiliza Maven Wrapper, Java 21 y Spring Web. Su clase de inicio es
`MinecraftApplication` y el codigo fuente se encuentra en:

```text
Minecraft/src/main/java/py/edu/uc/lp3/lf/minecraft
```

El remoto del repositorio se encuentra configurado mediante SSH:

```text
git@github.com:LucasFadul/lfadul-tp-lp3-2026.git
```

## Historial de trabajo

El historial principal observado contiene los siguientes commits:

```text
fb74a87 Initial commit
18ecbd1 Clase 16/09
7cc9b1e Clases y controllers creados
2a32aa3 Create RUN.md
```

Estos commits registran la creacion inicial, la incorporacion del modelo, la
creacion de los controllers y las instrucciones para ejecutar la API.

## Modelo orientado a objetos

La clase abstracta `Entidad` concentra el estado y comportamiento comun de las
entidades: identificador, posicion, salud actual y salud maxima. Tambien define
operaciones para moverse, recibir dano, curarse y consultar si la entidad sigue
viva. El metodo abstracto `getComportamiento()` obliga a cada entidad concreta a
describir su comportamiento propio y se serializa en las respuestas JSON.

`Mob` hereda de `Entidad` y agrega el comportamiento comun de las criaturas.
Desde esa clase se especializan entidades hostiles y pacificas. Entre las
clases concretas del modelo se encuentran:

- `Jugador`, entidad controlada por el usuario.
- `Esqueleto`, mob hostil que ataca con una flecha.
- `Enderman`, mob hostil que se teletransporta y ataca.
- `zombie` y `creeper`, otras especializaciones hostiles.
- `Aldeano` y `Cerdo`, especializaciones pacificas.

Los comportamientos `Hostil` y `ComportamientoIA` se representan mediante
interfaces. Las clases concretas implementan el comportamiento correspondiente
sin que el consumidor necesite consultar manualmente su tipo concreto.

El encapsulamiento se refuerza manteniendo privados los atributos del dominio,
limitando la salud al rango valido entre cero y la salud maxima, normalizando la
salud maxima a un valor positivo y evitando que el consumidor modifique
directamente la lista interna de `Inventario`.

## API HTTP

### Saludo del servicio

El `IndexController` publica:

```http
GET /
```

Respuesta actual:

```text
hola amigo
```

### Construccion de un jugador

El `JugadorController` publica un endpoint que recibe parametros de la URL y
construye una instancia del dominio:

```http
GET /jugador?id=Alex&saludMaxima=30&experiencia=10&x=1&y=2&z=3
```

Los parametros se utilizan para establecer el identificador, la salud maxima,
la experiencia y la posicion del jugador. Spring serializa el resultado como
JSON.

### Entidades polimorficas

El `JugadorController` tambien publica:

```http
GET /entidades
```

Este endpoint devuelve una lista declarada como `List<Entidad>` con dos objetos
concretos: un `Esqueleto` y un `Cerdo`. Spring serializa cada instancia usando
su tipo real e incluye el campo `comportamiento`, que proviene del metodo
abstracto declarado en `Entidad`.

## Compilacion y pruebas

Desde la raiz del repositorio se ejecuta:

```bash
cd Minecraft
./mvnw test
```

La verificacion final realizada el 21 de septiembre de 2026 termino
correctamente:

```text
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

La compilacion se ejecuto con `javac` en modo `release 21`. Las pruebas cubren
el arranque del contexto, el endpoint de jugador, el endpoint polimorfico con
dos hijas tratadas como `Entidad`, el control de rango de salud y la proteccion
de la lista interna del inventario.

Para levantar la aplicacion:

```bash
cd Minecraft
./mvnw spring-boot:run
```

Luego se pueden probar los endpoints:

```bash
curl http://localhost:8080/
curl "http://localhost:8080/jugador?id=Alex&saludMaxima=30&experiencia=10&x=1&y=2&z=3"
curl http://localhost:8080/entidades
```

La aplicacion se detiene con `Ctrl+C`.

## Colaboracion mediante Pull Request

Para la Parte C se clono el repositorio del companero y se creo una rama de
trabajo independiente:

```bash
git switch -c lf-contribucion-lp3
```

En esa rama se agrego `Arana` como una nueva especializacion de `Hostil`. La
clase configura un ataque de mordida que causa tres puntos de dano y sobrescribe
`moverse()` para representar el desplazamiento de una arana trepando.

Antes de publicar la contribucion se ejecuto:

```bash
./mvnw test
```

El proyecto del companero termino con `BUILD SUCCESS` y una prueba aprobada. La
rama se publico con:

```bash
git push -u origin lf-contribucion-lp3
```

Finalmente se abrio el Pull Request numero 1 desde
`lf-contribucion-lp3` hacia `main`:

<https://github.com/SaDez7/sb-taller-git-2026/pull/1>

El Pull Request modifica un solo archivo, no toca el `README.md` del companero y
GitHub informa que no presenta conflictos con la rama base. La revision y el
merge quedan a cargo del propietario del repositorio.

Durante la actualizacion final del 21 de septiembre de 2026 no se obtuvo una
confirmacion verificable del merge desde el entorno local, por lo que no se
declara como fusionado sin evidencia del repositorio remoto.

## Estado respecto de la consigna

Al momento de redactar esta bitacora se verifico lo siguiente:

- [x] Repositorio publico en GitHub.
- [x] README inicial y licencia Apache License 2.0.
- [x] Proyecto Spring Boot con Maven Wrapper y Spring Web.
- [x] Modelo Minecraft incorporado al proyecto.
- [x] `IndexController` con `GET /`.
- [x] Controller que construye un objeto con parametros de la URL.
- [x] Compilacion con Java 21 y pruebas con `BUILD SUCCESS`.
- [x] Rama de colaboracion y Pull Request en el repositorio del companero.
- [x] Propiedad de compilacion del proyecto actualizada de Java 17 a Java 21.
- [x] Metodo abstracto `getComportamiento()` declarado en la clase base.
- [x] JSON polimorfico expuesto en `GET /entidades` con dos hijas tratadas como `Entidad`.
- [x] Encapsulamiento reforzado en salud e inventario.
- [x] `README.md` actualizado con diagrama Mermaid del dominio.
- [x] Pruebas ampliadas y ejecutadas correctamente.
- [ ] Merge del Pull Request por el companero pendiente de confirmacion externa.

## Conclusion

El trabajo integra el modelo Minecraft con una API HTTP y aplica herencia,
sobreescritura, polimorfismo y encapsulamiento. La configuracion quedo en Java
21, el `README.md` contiene el diagrama Mermaid actualizado y las pruebas
finales pasan correctamente. El unico punto no cerrado por codigo es la
confirmacion externa del merge del Pull Request en el repositorio del
companero.

## Revisión individual POO-06 — 4 de octubre de 2026

- Asistente/agente: OpenAI Codex.
- Modelo informado por esta sesión: GPT-6. No se expone un identificador de variante
  o snapshot más específico; no se inventa uno. La asistencia previa no registró
  su modelo exacto y no se puede reconstruir con certeza.
- Prompt del estudiante: aplicar el ejercicio POO-06 al repositorio existente,
  publicar el HTTP con Spring Boot, seguir paquetes del template, incorporar
  constructores y acción sobrecargados, sobreescritura abstracta con dos hijas,
  completar README, licencia, bitácora, especificaciones y enlace al commit.
- Fuentes leídas: enunciado del 30/09/2026, rúbrica y árbol del template oficial.
- Cambios: separación `domain` / `rest.controller`, `Application` en paquete raíz,
  constructores de Jugador, Esqueleto y Cerdo; ataque con distancia; rechazo de
  parámetros inválidos por el dominio con respuesta HTTP 400; documentación y pruebas.
- Este ejercicio es individual. La colaboración descrita en las secciones anteriores
  corresponde a una actividad previa; no se realizó una contribución a otro alumno
  para esta revisión.

### Validación de esta revisión

`./mvnw clean test`: 8 pruebas, 0 fallos, 0 errores; BUILD SUCCESS.
Ejecutado con OpenJDK 26 compilando para Java 21.
`./mvnw spring-boot:run`: arranque confirmado. HTTP real: saludo en `/`,
Alex construido con salud 30 y experiencia 10, dos comportamientos distintos
en `/entidades`, y HTTP 400 con mensaje de dominio para salud máxima 0.
El sandbox impidió abrir el puerto inicialmente; la verificación se ejecutó
con permiso para iniciar el servidor local. Se conservó el nombre del
repositorio por instrucción explícita del estudiante.

Se integró el historial remoto antes del push. La interfaz AtacaJugador de una contribución previa se conservó en domain y se incorporó al diagrama.
