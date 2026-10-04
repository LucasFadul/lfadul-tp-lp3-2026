# Especificaciones POO-06: Minecraft

Autor: Lucas Fadul. GitHub: `LucasFadul`. Trabajo individual.

## Objetivo

Exponer el modelo Minecraft como servicio HTTP Spring Boot, con encapsulamiento,
herencia, polimorfismo, constructores sobrecargados y acciones sobrecargadas.

## Consignas aplicadas

1. Paquetes `py.edu.uc.lp3.domain` y `py.edu.uc.lp3.rest.controller`, con
   `Application` en `py.edu.uc.lp3` para arrancar.
2. Se conserva el modelo de entidades, jugador, mobs hostiles y pacíficos.
3. `Entidad` declara `getComportamiento()` abstracto. `Esqueleto` y `Cerdo`
   lo sobrescriben con sus comportamientos propios.
4. `GET /` confirma disponibilidad; `GET /jugador` construye un jugador nuevo
   desde id, saludMaxima, experiencia y posición x/y/z de la URL.
5. `GET /entidades` entrega JSON de Esqueleto y Cerdo tratados como `Entidad`,
   incluyendo la respuesta del método abstracto.
6. Jugador, Esqueleto y Cerdo tienen constructores simples y sobrecargados,
   delegación `this` y llamada `super`. El constructor completo del jugador
   inicializa todos los parámetros de URL y valida su estado.
7. `Esqueleto.atacar(Entidad)` y `atacar(Entidad, double)` modelan la misma
   acción: 4 de daño dentro de 16 bloques, sin daño fuera del alcance.
8. README con licencia Apache 2.0, Mermaid y explicación de ambos mecanismos.
9. BITACORA registra asistente, modelo informado y resumen de prompts.

## Cómo probar

Requiere Java 21 o superior. Desde la raíz:

```bash
cd Minecraft
./mvnw clean test
./mvnw spring-boot:run
```

En otra terminal:

```bash
curl http://localhost:8080/
curl 'http://localhost:8080/jugador?id=Alex&saludMaxima=30&experiencia=10&x=1&y=2&z=3'
curl http://localhost:8080/entidades
curl -i 'http://localhost:8080/jugador?saludMaxima=0'
curl -i 'http://localhost:8080/jugador?experiencia=-1'
```

Se espera saludo, jugador Alex con salud 30 y experiencia 10, dos entidades
con comportamientos distintos y HTTP 400 en los dos últimos pedidos.
Las pruebas verifican además constructores, ataque con y sin distancia,
curación extrema, daño negativo, posición no finita e inventario protegido.

## Defensa del diseño

Si el controller asigna vida a mano, puede eludir las reglas del dominio.
Aquí los atributos son privados; el constructor valida y los métodos mantienen
la salud dentro del rango. El controller traduce errores a HTTP, sin decidir
las reglas de salud o alcance. Sobrecarga selecciona una firma por argumentos;
sobreescritura selecciona el comportamiento por el tipo real del objeto.

## Entrega

Adjuntar este Markdown en Classroom y pegar el enlace al commit final.
Enviar `LucasFadul` al chat del curso. La carga en Classroom y el envío al chat
son acciones a realizar por el estudiante.
