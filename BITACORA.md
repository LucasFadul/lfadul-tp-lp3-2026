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
- `zombie` y `Creeper`, otras especializaciones hostiles.
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

## Interfaz entre familias distintas — 14 de octubre de 2026

- Asistente/agente: OpenAI Codex.
- Modelo informado por esta sesión: GPT-5.
- Prompt del estudiante: explicar la diferencia entre interfaz y herencia y
  aplicar el ejercicio de una capacidad compartida por clases de familias
  distintas al dominio Minecraft.
- Decisión de modelado: `Creeper` pertenece a `Entidad -> Mob` y `Tnt` pertenece
  a `Bloque`; ambas implementan `Explosivo` porque pueden explotar.
- Implementación: `Explosivo.explotar()` devuelve texto y tiene una implementación
  `default`; `Tnt` la conserva y `Creeper` la sobrescribe. `GET /explosivos`
  utiliza ambas instancias mediante el tipo `Explosivo` y responde JSON.
- Verificación: `./mvnw clean test` ejecutó 10 pruebas, sin fallos ni errores.
  Las pruebas cubren el comportamiento `default`, la sobrescritura y la
  respuesta JSON de `GET /explosivos`. También se inició la aplicación con
  `./mvnw spring-boot:run`: el endpoint respondió HTTP 200 con los mensajes de
  `Creeper` y `TNT`.

### Ampliación UICapable

- Prompt del estudiante: incorporar también la capacidad `UICapable` con imagen,
  posición, sonido y velocidad.
- Diseño: la interfaz declara métodos para consultar esos datos y un mensaje
  `mostrarEnUI()` que devuelve texto. Los datos pertenecen a cada objeto, ya que
  los campos de una interfaz Java serían constantes compartidas.
- Implementación: `Creeper` y `Tnt`, provenientes de familias distintas,
  implementan `UICapable`. `Creeper` reemplaza `mostrarEnUI()` y `Tnt` conserva
  el método `default`.
- Servicio REST: `GET /ui-capables` pide todos los datos mediante variables del
  tipo `UICapable` y responde JSON.
- Verificación: `./mvnw clean test` ejecutó 12 pruebas, sin fallos ni errores.
  La comprobación HTTP real respondió 200 y devolvió imagen, posición, sonido,
  velocidad y mensaje para `Creeper` y `TNT`.

## POO-08 individual — 6 de noviembre de 2026

- Asistente/agente: OpenAI Codex.
- Modelo informado por la sesión: GPT-6. El entorno no expone aquí un identificador de variante o snapshot más preciso; no se inventa.
- Resumen de solicitudes: construir CRUD REST con Spring Data y H2 para Entidad; explicar y probar la persistencia; añadir simulación de explosiones con tres clases y una jerarquía de excepciones; completar los requisitos individuales de POO-08, colección Insomnia, documentación y especificaciones.
- Se consultaron el enunciado POO-08 y la rúbrica enlazados por el estudiante. No se delegó el trabajo a otros agentes ni se colaboró sobre el repositorio de otro estudiante.
- Se amplió la persistencia JPA polimórfica y la simulación transaccional con TNT consumible y registro de explosiones; se documentaron las reglas, el arranque y las decisiones del dominio.
- Verificación: `./mvnw -q test -Dspring.datasource.url=jdbc:h2:mem:fullverify -Dspring.jpa.hibernate.ddl-auto=create-drop`: 22 pruebas sin fallos. Se inició el servidor desde la raíz con una base temporal, se recorrieron por HTTP las 24 solicitudes de la colección y se comprobó la persistencia de entidad, TNT y explosión tras reiniciar. El archivo JSON se validó sintácticamente; la importación en la interfaz de Insomnia no se automatizó.

## POO-09 — inicio, 22 de noviembre de 2026

- Solicitud: comenzar la entrega, cerrar observaciones de POO-08 e integrar APIs de MatRF04 y SaDez7, con el grupo confirmado por Lucas.
- Asistencia: OpenAI Codex. Se consultaron los README y OpenAPI disponibles y se clonaron copias temporales, sin copiar código de dominio ajeno.
- Se probaron con H2 en memoria: MatRF04 en el commit `727c53064c081ecfad8fdc4cc597d14a485150ab` arrancó pero `/v3/api-docs` respondió 404; SaDez7 en `1853e5226ce5aea60574fa0ebb4d8f8bbfb33aae` arrancó y publicó `/v3/api-docs`.
- Se dejó de versionar la base H2 generada conservando el archivo local, se agregaron patrones al `.gitignore` y se corrigieron tres secciones desactualizadas del README. Queda pendiente mover reglas y validaciones a servicios y completar el escenario POO-09.

### Avance posterior

- Se trasladó el cálculo y las excepciones de la simulación de TNT a `ReglasExplosionService`, manteniendo el resultado persistido y los errores HTTP; las pruebas pasan. Aún quedan validaciones y lógica en otras clases de dominio y persistencia señaladas por la corrección.
- Se agregó un cliente de solo lectura para `/entidades/vivos` de SaDez7, con URL configurable y pruebas simuladas; todavía no valida el escenario definitivo ni persiste jugadas POO-09.
- Se documentaron roles y commits consultados, se preparó una colección Insomnia preliminar y se describieron las reglas de TNT en OpenAPI. Pendientes los tres casos del escenario y la validación de A2.
- Se trasladó la selección del subtipo JPA a `EntidadService` y se retiraron validaciones de las entidades JPA; se verificaron las pruebas de la aplicación.

### Correcciones y consultas de preparación

- Se trasladaron validaciones de jugador, posición y combate al servicio; se retiraron excepciones y reglas mutables de los modelos implicados. Los ejemplos de textos e interfaces de ejercicios anteriores siguen en el dominio. Se adaptaron las pruebas para validar las reglas en la capa de servicios y las rutas HTTP.
- Se agregó `Matrf04Client` para la consulta de solo lectura de creepers listos, con URL configurable y pruebas simuladas; no constituye el escenario ni resuelve la ausencia del OpenAPI del último commit consultado.
- Se ejecutaron copias locales de los dos servicios con H2 en memoria, se creó una entidad de prueba en cada copia y se verificaron las respuestas HTTP 200 de las rutas de lectura. Ambas copias se detuvieron después; no se accedió a las bases de los compañeros.
- Se actualizó el README, se ampliaron las consultas preliminares de Insomnia y se creó un borrador de especificaciones POO-09. Siguen pendientes el contrato definitivo de A2 y el escenario validado por ambos compañeros.
- Verificación posterior: 32 pruebas automatizadas aprobadas con base H2 de prueba; ejecución real de A1 en el puerto 18080 con H2 en memoria: OpenAPI 200, creación de entidad y TNT 201, explosión 201 (salud 20 a 12), segunda detonación 409 y salud sin cambios. El servidor temporal se detuvo.
- Se actualizó la guía de estudio en `output/pdf/` para reflejar la ubicación actual de las reglas y el estado incompleto del challenge.

## POO-09 — pruebas por Hamachi, 23 de noviembre de 2026

- A2 publicó OpenAPI accesible en `http://25.28.15.25:8080/v3/api-docs`; A3 lo publicó en `http://25.29.140.0:8080/v3/api-docs`. Las direcciones se pasaron por el usuario y se consultaron sin cambiar datos remotos.
- Se implementó `POST /api/jugadas`: A2 lista creepers listos y A3 entidades vivas; si el participante no figura, A1 genera un 422 que identifica la lista. Ambos GET son de solo lectura. Si aparecen, una transacción local simula y guarda la explosión y la jugada; un GET permite consultarla. Las APIs externas no generan un rechazo explícito: lo infiere A1.
- Se agregaron los casos a Insomnia y pruebas con servicios simulados: éxito, ausencia en A2, ausencia en A3 y fallo de conexión de A2. Las 36 pruebas del proyecto pasaron.
- Se iniciaron los servicios de A1 con H2 en memoria y se comprobó por Hamachi que la ausencia en A2 y A3 devuelve el origen y deja sin cambios la salud y TNT local. No se crearon ni modificaron datos de los compañeros. A3 no tenía jugadores vivos durante la consulta, por lo cual el éxito real de extremo a extremo quedó pendiente.
- Pendiente confirmar que la cátedra acepta la inferencia desde listas como validación de reglas del challenge; las simulaciones de explosión de los compañeros no se invocan. También faltan el commit de A2 que publica OpenAPI, confirmación de commits definitivos y el enlace al commit final propio.

## POO-09 — validación previa sin efectos

- Se revisaron los OpenAPI actuales por Hamachi. Mateo tiene una simulación GET que consulta al jugador en SaDez7 y devuelve el daño; SaDez7 publica validación de jugador por GET y simulación de explosión por POST que actúa sobre sus entidades. Esas operaciones, tal como están documentadas, todavía no establecen dos validaciones independientes explícitas de la misma explosión.
- Se agregó `POST /api/entidades/{id}/explosiones/validar`: calcula la regla local sin modificar datos, con 200, 400, 404 y 409 documentados. Se agregaron pruebas de éxito y rechazos que comprueban el estado local. Esta operación no comprueba una TNT persistida.
- La jugada que consulta listas sigue siendo provisional; falta acordar con los compañeros los contratos para validar una misma explosión y completar esa integración.

## POO-09 - documentación de coordinación

- Solicitud: documentar cómo preparar y probar objetivos en las bases de los tres integrantes.
- Se agregó `docs/GUIA-COORDINACION-POO09.md` con contratos, efectos en cada base, pasos de prueba y limitaciones del escenario provisional. Se actualizó el README y el borrador de especificaciones con la evidencia del caso feliz observado por el usuario (HTTP 201, jugada 1, explosión 5).
- No se crearon ni modificaron entidades remotas en este trabajo de documentación. La implementación definitiva, la validación de reglas de los compañeros y los commits finales continúan pendientes.
- Se actualizaron los dos PDF de estudio y presentación para indicar la prueba real exitosa y documentar la validación de explosión sin cambios; se renderizaron y revisaron visualmente.
