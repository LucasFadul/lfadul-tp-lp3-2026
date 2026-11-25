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

/** Consulta provisional de solo lectura: creepers que la API A2 considera listos. */
@Component
public class Matrf04Client {
    private final WebClient client;

    @Autowired
    public Matrf04Client(@Value("${api.a2.base-url:http://localhost:8081}") String baseUrl) {
        this(WebClient.builder().baseUrl(baseUrl).build());
    }

    Matrf04Client(WebClient client) { this.client = client; }

    public Mono<List<CreeperExterno>> listarCreepersListos() {
        return client.get().uri("/entidades/creepers/listos").retrieve()
                .bodyToFlux(CreeperExterno.class).collectList().timeout(Duration.ofSeconds(10))
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A2 (MatRF04)", error));
    }

    public record CreeperExterno(Long id, String tipo, Integer vida, Integer danoBase,
            Integer velocidad, Boolean yaExploto) { }

    /** Mateo calcula el impacto sin modificar su base; consulta a Santiago para construir el jugador. */
    public Mono<ImpactoJugador> simularImpacto(int saludMaxima, double x, double y, double z) {
        return client.get().uri(uri -> uri.path("/simulaciones/creeper/jugador")
                .queryParam("saludMaxima", saludMaxima).queryParam("x", x)
                .queryParam("y", y).queryParam("z", z).build())
                .retrieve().bodyToMono(ImpactoJugador.class).timeout(Duration.ofSeconds(10))
                .onErrorMap(WebClientResponseException.class, error -> error.getStatusCode().is4xxClientError()
                        ? new RechazoJugadaException("A2", "Mateo rechazó la simulación: " + error.getStatusCode().value())
                        : new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A2 (MatRF04)", error))
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A2 (MatRF04)", error));
    }

    public record ImpactoJugador(String jugador, Double distancia, Integer vidaAnterior,
            Integer danoRecibido, Integer vidaFinal) { }
}
