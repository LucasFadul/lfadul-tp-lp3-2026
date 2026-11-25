# POO-09 — API cruzada de Minecraft

**Estudiante:** Lucas Fadul  
**Repositorio:** [LucasFadul/lfadul-tp-lp3-2026](https://github.com/LucasFadul/lfadul-tp-lp3-2026)  
**Commit exacto de la solución:** [c0db1484b0333452fa1d868d6ac887e008b8c226](https://github.com/LucasFadul/lfadul-tp-lp3-2026/commit/c0db1484b0333452fa1d868d6ac887e008b8c226)

## Grupo

- **A1:** Lucas Fadul. API propia y base de datos que persiste la jugada.
- **A2:** Mateo Ríos ([MatRF04](https://github.com/MatRF04/mr-taller-git-2026)). Versión probada: [8f32d13](https://github.com/MatRF04/mr-taller-git-2026/commit/8f32d13a4857be25d8b1ab86fe552449c0cd83ab).
- **A3:** Santiago Baez ([SaDez7](https://github.com/SaDez7/sb-taller-git-2026)). Versión probada: [763df1c](https://github.com/SaDez7/sb-taller-git-2026/commit/763df1c80a8882f443be6489e2aefaa8d6de0b24).

## Escenario

Una explosión en el origen usa el creeper de Mateo, aplica las reglas de Mateo y Santiago sobre un jugador de Santiago y registra la explosión de una TNT y la jugada en la base H2 de Lucas.

Mateo calcula el daño de un creeper genérico en el origen, de forma simulada y sin escribir su base; antes se comprueba que el creeper elegido esté listo. Santiago ejecuta su explosión, cambia la salud de su jugador y puede destruir un bloque en su base. Lucas guarda la explosión local y la jugada si ambas consultas son aceptadas. Si Santiago acepta y luego falla el guardado local, sus cambios pueden quedar sin registro en Lucas: no hay transacción distribuida.

## Cómo probar

1. Clonar el commit de la solución y las versiones de los compañeros citadas arriba. Arrancar las tres aplicaciones según el [README de la solución](https://github.com/LucasFadul/lfadul-tp-lp3-2026/blob/c0db1484b0333452fa1d868d6ac887e008b8c226/README.md), que incluye puertos, OpenAPI y variables para configurar URLs.
2. Importar `insomnia/minecraft-poo09.json` y crear un creeper listo en Mateo, un creeper en el origen, un jugador con salud completa y un bloque en Santiago, y un objetivo y una TNT en Lucas. Ajustar las URLs y los identificadores de la colección.
3. Ejecutar los pedidos de rechazo de A2 y A3. Cada uno debe responder 422 con su `origen`; comprobar que la salud del objetivo y el estado de la TNT local no cambiaron.
4. Ejecutar el escenario feliz. Debe responder 201; usar el ID retornado para consultar la jugada mediante `GET /api/jugadas/{id}`. Verificar la explosión local y el daño simulado de Mateo. Para otro éxito se necesita otra TNT sin detonar y un jugador de Santiago con salud completa; usar también otro bloque de prueba si se quiere demostrar la destrucción.

Se probaron las tres aplicaciones en copias locales con bases H2 temporales: los tres OpenAPI respondieron 200, los dos rechazos dejaron igual la salud y la TNT de A1, y el caso feliz se guardó y se leyó por GET. Los resultados están en `docs/EVIDENCIA-POO09.md` del commit de la solución. Las 48 pruebas automatizadas pasaron.
