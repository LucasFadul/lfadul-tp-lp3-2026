# POO-09 — API cruzada de Minecraft

**Estudiante:** Lucas Fadul  
**Repositorio:** [LucasFadul/lfadul-tp-lp3-2026](https://github.com/LucasFadul/lfadul-tp-lp3-2026)  
**Commit exacto de la solución:** [13f384da34a228cd2b5c285cf29e3872043ec73e](https://github.com/LucasFadul/lfadul-tp-lp3-2026/commit/13f384da34a228cd2b5c285cf29e3872043ec73e)

## Grupo

- **A1:** Lucas Fadul. API propia y base de datos que persiste la jugada.
- **A2:** Mateo Ríos ([MatRF04](https://github.com/MatRF04/mr-taller-git-2026)). Versión probada: [8f32d13](https://github.com/MatRF04/mr-taller-git-2026/commit/8f32d13a4857be25d8b1ab86fe552449c0cd83ab).
- **A3:** Santiago Baez ([SaDez7](https://github.com/SaDez7/sb-taller-git-2026)). Versión probada: [763df1c](https://github.com/SaDez7/sb-taller-git-2026/commit/763df1c80a8882f443be6489e2aefaa8d6de0b24).

## Escenario

Un creeper listo de Mateo amenaza a un jugador vivo de Santiago; Lucas consulta la validación de los datos del jugador y la simulación de daño y, si las consultas son aceptadas, guarda una explosión local y la jugada en H2.

El daño de Mateo sobre el jugador es una simulación. La consulta explícita a Santiago valida la construcción del jugador y no ejecuta sus reglas de explosión. Los servicios de los compañeros se consultan sin escribir en sus bases; la explosión y los cambios de TNT y objetivo se registran en la base de Lucas. La ausencia de un participante se interpreta a partir de las listas y se informa como rechazo atribuido a A2 o A3.

## Cómo probar

1. Clonar el commit de la solución y las versiones de los compañeros citadas arriba. Arrancar las tres aplicaciones según el [README de la solución](https://github.com/LucasFadul/lfadul-tp-lp3-2026/blob/13f384da34a228cd2b5c285cf29e3872043ec73e/README.md), que incluye puertos, OpenAPI y variables para configurar URLs.
2. Importar `insomnia/minecraft-poo09.json` y crear un creeper listo en Mateo, un jugador con salud completa en Santiago, y un objetivo y una TNT en Lucas. Ajustar las URLs y los identificadores de la colección.
3. Ejecutar los pedidos de rechazo de A2 y A3. Cada uno debe responder 422 con su `origen`; comprobar que la salud del objetivo y el estado de la TNT local no cambiaron.
4. Ejecutar el escenario feliz. Debe responder 201; usar el ID retornado para consultar la jugada mediante `GET /api/jugadas/{id}`. Verificar la explosión local y el daño simulado de Mateo. Para otro éxito se necesita otra TNT sin detonar.

Se probaron las tres aplicaciones en copias locales con bases H2 temporales: los tres OpenAPI respondieron 200, los dos rechazos dejaron igual la salud y la TNT de A1, y el caso feliz se guardó y se leyó por GET. Los resultados están en `docs/EVIDENCIA-POO09.md` del commit de la solución. Las 44 pruebas automatizadas pasaron.
