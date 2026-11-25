package py.edu.uc.lp3.client;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import py.edu.uc.lp3.service.RechazoJugadaException;
import reactor.core.publisher.Mono;

/** Cliente del contrato público de SaDez7; simularExplosion modifica su base. */
@Component
public class Sadez7Client {
    private final WebClient client;

    @Autowired
    public Sadez7Client(@Value("${api.a3.base-url:http://localhost:8082}") String baseUrl) {
        this(WebClient.builder().baseUrl(baseUrl).build());
    }

    Sadez7Client(WebClient client) { this.client = client; }

    public Mono<List<EntidadExterna>> listarVivos() {
        return client.get().uri("/entidades/vivos").retrieve()
                .bodyToFlux(EntidadExterna.class).collectList().timeout(Duration.ofSeconds(10))
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A3 (SaDez7)", error));
    }

    public record EntidadExterna(String id, String tipo, int salud, int saludMaxima,
            Integer experiencia, double x, double y, double z) { }

    /** Valida el mismo jugador en la operación de construcción de Santiago, sin persistirlo. */
    public Mono<JugadorValidado> validarJugador(EntidadExterna jugador) {
        return client.get().uri(uri -> uri.path("/jugador").queryParam("id", jugador.id())
                .queryParam("saludMaxima", jugador.saludMaxima()).queryParam("experiencia", jugador.experiencia() == null ? 0 : jugador.experiencia())
                .queryParam("x", jugador.x()).queryParam("y", jugador.y()).queryParam("z", jugador.z()).build())
                .retrieve().bodyToMono(JugadorValidado.class).timeout(Duration.ofSeconds(10))
                .onErrorMap(WebClientResponseException.class, error -> error.getStatusCode().is4xxClientError()
                        ? new RechazoJugadaException("A3", "Santiago rechazó al jugador: " + error.getStatusCode().value())
                        : new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A3 (SaDez7)", error))
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A3 (SaDez7)", error));
    }

    public record JugadorValidado(String id, Integer salud, Integer saludMaxima) { }

    public Mono<EntidadExterna> obtenerEntidad(String id) {
        return client.get().uri(uri -> uri.path("/entidades/{id}").build(id))
                .retrieve().bodyToMono(EntidadExterna.class).timeout(Duration.ofSeconds(10))
                .onErrorMap(WebClientResponseException.class, error -> error.getStatusCode().is4xxClientError()
                        ? new RechazoJugadaException("A3", "Santiago no aceptó la entidad: " + error.getStatusCode().value())
                        : new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A3 (SaDez7)", error))
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A3 (SaDez7)", error));
    }

    /** Esta operación actualiza la salud y, si se indica, el estado del bloque en A3. */
    public Mono<ExplosionA3> simularExplosion(String creeperId, String jugadorId, double radio, Long bloqueId) {
        var pedido = new PedidoExplosion(creeperId, radio, List.of(new ReferenciaJugador(jugadorId)),
                bloqueId == null ? List.of() : List.of(new ReferenciaBloque(bloqueId)));
        return client.post().uri("/simulacion/explosiones").bodyValue(pedido)
                .retrieve().bodyToMono(ExplosionA3.class).timeout(Duration.ofSeconds(10))
                .onErrorMap(WebClientResponseException.class, error -> error.getStatusCode().is4xxClientError()
                        ? new RechazoJugadaException("A3", "Santiago rechazó la explosión: " + error.getStatusCode().value())
                        : new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falló la explosión de A3; su resultado puede ser desconocido", error))
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falló la explosión de A3; su resultado puede ser desconocido", error));
    }

    public record PedidoExplosion(String creeperId, double radio, List<ReferenciaJugador> jugadores, List<ReferenciaBloque> bloques) { }
    public record ReferenciaJugador(String id) { }
    public record ReferenciaBloque(Long id) { }
    public record ExplosionA3(String creeperId, Double radio, List<JugadorAfectado> jugadores, List<BloqueAfectado> bloques) { }
    public record JugadorAfectado(String id, Double distancia, Integer danio, Integer saludAntes, Integer saludDespues) { }
    public record BloqueAfectado(Long id, Boolean destruido) { }
}
