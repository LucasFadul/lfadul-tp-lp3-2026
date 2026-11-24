# POO-09: APIs cruzadas - borrador de entrega

**Estado: provisional.** Existe un escenario con consultas de listas y persistencia local. El caso feliz se observó con ambas APIs reales el 23/11/2026; falta confirmar que estas verificaciones satisfacen las reglas exigidas y repetir los casos con las versiones definitivas.

## Grupo

- **A1 - Lucas Fadul:** [lfadul-tp-lp3-2026](https://github.com/LucasFadul/lfadul-tp-lp3-2026), API local `http://localhost:8080`, OpenAPI `/v3/api-docs`.
- **A2 - MatRF04:** [mr-taller-git-2026](https://github.com/MatRF04/mr-taller-git-2026), versión consultada [727c530](https://github.com/MatRF04/mr-taller-git-2026/commit/727c53064c081ecfad8fdc4cc597d14a485150ab), API local propuesta `http://localhost:8081`. En ese commit el servicio arrancó pero `/v3/api-docs` devolvió 404. El 23/11/2026 se observó un OpenAPI en ejecución en `http://25.28.15.25:8080/v3/api-docs`; falta confirmar el commit correspondiente.
- **A3 - SaDez7:** [sb-taller-git-2026](https://github.com/SaDez7/sb-taller-git-2026), versión consultada [1853e52](https://github.com/SaDez7/sb-taller-git-2026/commit/1853e5226ce5aea60574fa0ebb4d8f8bbfb33aae), API local propuesta `http://localhost:8082`, OpenAPI `/v3/api-docs`.

Los tres integrantes confirmaron trabajar en grupo. Cada uno hace su entrega individual y consulta a los otros dos.

## Reglas y escenario

La [documentación de A2](https://github.com/MatRF04/mr-taller-git-2026#simulación-de-explosiones) describe un creeper vivo que explota una vez y causa daño a objetivos dentro del radio. La [documentación de A3](https://github.com/SaDez7/sb-taller-git-2026#las-dos-reglas-de-negocio) describe daño que disminuye con la distancia y destrucción de bloques dentro del radio.

**Escenario provisional:** registrar una explosión local cuando el creeper indicado aparece listo en A2 y el jugador indicado aparece vivo en A3. Estas condiciones se infieren en A1 desde listas HTTP 200; las APIs no envían un rechazo explícito. Sus simulaciones de explosión no se invocan. Debe confirmarse si esto satisface la consigna de reglas y rechazos; una lectura de lista no reemplaza necesariamente la validación de la explosión completa.

## Avance comprobado

A1 ofrece CRUD, TNT, simulación local, H2 y OpenAPI. Se prepararon dos clientes HTTP configurables de lectura: A2 `GET /entidades/creepers/listos` y A3 `GET /entidades/vivos`. Se verificó cada ruta en una copia local con H2 en memoria; las pruebas automatizadas de los clientes usan respuestas simuladas. `POST /api/jugadas` combina ambas lecturas y persiste una explosión local solo cuando ambas condiciones se confirman; `GET /api/jugadas/{id}` consulta el registro. Los dos rechazos de ausencia se probaron contra las APIs reales y el éxito se observó por Hamachi (HTTP 201, jugada 1 y explosión 5) en la sesión del 23/11/2026. Las APIs remotas devuelven listas; la ausencia la interpreta A1.

## Cómo iniciar las copias

Clonar los repositorios y usar los commits confirmados por los compañeros. Cada bloque se ejecuta en una terminal diferente, desde la raíz del proyecto correspondiente:

```bash
# A1
./mvnw spring-boot:run

# A2
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# A3
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
```

Abrir `/v3/api-docs` en cada puerto cuando la versión del servicio lo publique. Importar `insomnia/minecraft-poo09.json`: tiene las tres URLs, preparación local y los tres casos. Los ids deben ajustarse a los datos de cada API; después del éxito copiar el id a `jugada_id`.

## Antes de entregar

- Definir el escenario en una frase y citar las dos reglas realmente consumidas.
- Confirmar que la inferencia a partir de listas califica como validación de las reglas de ambos compañeros; de no ser así, acordar endpoints de validación sin efectos con ellos.
- Probar éxito y rechazo de cada compañero, incluyendo la identificación del origen del rechazo y la conservación del estado en A1.
- Revisar OpenAPI, README, bitácora e Insomnia con las versiones definitivas y sus URLs.
- Agregar a la copia de este documento que se subirá a Classroom el enlace exacto al commit final de A1. **El commit final todavía no existe.**

## Guía para el grupo

Ver [GUIA-COORDINACION-POO09.md](GUIA-COORDINACION-POO09.md) para los enlaces de Hamachi, preparación de objetivos, efectos sobre cada base y pruebas.
