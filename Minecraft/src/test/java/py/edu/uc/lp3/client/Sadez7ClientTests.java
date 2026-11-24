package py.edu.uc.lp3.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.*;

class Sadez7ClientTests {
    @Test
    void consultaElListadoDeEntidadesVivas() {
        WebClient web = WebClient.builder().baseUrl("http://example.test").exchangeFunction(request -> {
            assertThat(request.url().getPath()).isEqualTo("/entidades/vivos");
            assertThat(request.method().name()).isEqualTo("GET");
            return Mono.just(ClientResponse.create(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("[{\"id\":\"steve\",\"tipo\":\"jugador\",\"salud\":15,\"saludMaxima\":20,\"x\":3,\"y\":0,\"z\":0}]").build());
        }).build();
        var resultado = new Sadez7Client(web).listarVivos().block();
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo("steve");
            assertThat(e.salud()).isEqualTo(15);
        });
    }

    @Test
    void distingueFalloDeA3() {
        WebClient web = WebClient.builder().baseUrl("http://example.test")
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build())).build();
        assertThatThrownBy(() -> new Sadez7Client(web).listarVivos().block())
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(ex.getReason()).contains("A3");
                });
    }
}
