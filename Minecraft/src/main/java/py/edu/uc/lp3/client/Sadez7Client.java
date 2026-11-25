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

/** Consulta de solo lectura al contrato público de SaDez7. */
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
}
