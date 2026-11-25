package py.edu.uc.lp3.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import py.edu.uc.lp3.service.RechazoJugadaException;
import static org.assertj.core.api.Assertions.*;

class Matrf04ClientTests {
    @Test
    void consultaLaSimulacionDeMateoConLaPosicionDelJugador() {
        WebClient web = WebClient.builder().baseUrl("http://example.test").exchangeFunction(request -> {
            assertThat(request.method().name()).isEqualTo("GET");
            assertThat(request.url().getPath()).isEqualTo("/simulaciones/creeper/jugador");
            assertThat(request.url().getQuery()).contains("saludMaxima=20", "x=2.0", "y=0.0", "z=0.0");
            return Mono.just(ClientResponse.create(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"jugador\":\"Steve\",\"distancia\":2,\"vidaAnterior\":20,\"danoRecibido\":10,\"vidaFinal\":10}").build());
        }).build();
        var impacto = new Matrf04Client(web).simularImpacto(20, 2, 0, 0).block();
        assertThat(impacto.danoRecibido()).isEqualTo(10);
        assertThat(impacto.vidaFinal()).isEqualTo(10);
    }

    @Test
    void atribuyeA2ElRechazoDeSuSimulacion() {
        WebClient web = WebClient.builder().baseUrl("http://example.test")
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST).build())).build();
        assertThatThrownBy(() -> new Matrf04Client(web).simularImpacto(20, 0, 0, 0).block())
                .isInstanceOfSatisfying(RechazoJugadaException.class, ex -> assertThat(ex.getOrigen()).isEqualTo("A2"));
    }
    @Test
    void consultaCreepersListosSinModificarLaApiRemota() {
        WebClient web = WebClient.builder().baseUrl("http://example.test").exchangeFunction(request -> {
            assertThat(request.method().name()).isEqualTo("GET");
            assertThat(request.url().getPath()).isEqualTo("/entidades/creepers/listos");
            return Mono.just(ClientResponse.create(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("[{\"id\":1,\"tipo\":\"Creeper\",\"vida\":20,\"danoBase\":4,\"velocidad\":2,\"yaExploto\":false}]").build());
        }).build();
        var resultado = new Matrf04Client(web).listarCreepersListos().block();
        assertThat(resultado).singleElement().satisfies(creeper -> {
            assertThat(creeper.id()).isEqualTo(1L);
            assertThat(creeper.vida()).isEqualTo(20);
            assertThat(creeper.yaExploto()).isFalse();
        });
    }

    @Test
    void identificaLaFallaDelServicioA2() {
        WebClient web = WebClient.builder().baseUrl("http://example.test")
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build())).build();
        assertThatThrownBy(() -> new Matrf04Client(web).listarCreepersListos().block())
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(ex.getReason()).contains("A2");
                });
    }
}
