# POO-09: escenario aplicado a la entrega de Lucas Fadul

- **A1:** Lucas Fadul, [repositorio](https://github.com/LucasFadul/lfadul-tp-lp3-2026).
- **A2:** Mateo Ríos, [repositorio](https://github.com/MatRF04/mr-taller-git-2026), versión probada [`8f32d13`](https://github.com/MatRF04/mr-taller-git-2026/commit/8f32d13a4857be25d8b1ab86fe552449c0cd83ab).
- **A3:** Santiago Baez, [repositorio](https://github.com/SaDez7/sb-taller-git-2026), versión probada [`763df1c`](https://github.com/SaDez7/sb-taller-git-2026/commit/763df1c80a8882f443be6489e2aefaa8d6de0b24).

**Escenario:** una explosión en el origen usa el creeper de Mateo, daña al jugador y un bloque de Santiago según las reglas de ambos, y registra la TNT local y la jugada en Lucas.

**Cómo probar:** iniciar las tres copias como indica el README, importar `insomnia/minecraft-poo09.json`, crear los participantes y ejecutar los rechazos 05 y 06 y el éxito 07. Consultar el resultado con el pedido 08. Las instrucciones detalladas están en [GUIA-COORDINACION-POO09.md](GUIA-COORDINACION-POO09.md) y las respuestas en [EVIDENCIA-POO09.md](EVIDENCIA-POO09.md).

Mateo simula el daño sin escribir en su base; Santiago aplica el daño y puede destruir un bloque en su base. Si cualquiera rechaza, la base de Lucas queda igual. Una falla posterior a la aceptación de Santiago puede dejar cambios remotos sin registro local, pues las bases no comparten una transacción.

Para Classroom, utilizar el archivo `ENTREGA-CLASSROOM-POO09.md`, que contiene el enlace al commit de la solución.
