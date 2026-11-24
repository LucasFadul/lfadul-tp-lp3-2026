package py.edu.uc.lp3.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjetoExternoServiceTests {
    @Test
    void consultaYDeserializaObjetosConDatosVariables() {
        WebClient client = WebClient.builder().baseUrl("https://example.test")
                .exchangeFunction(request -> {
                    assertThat(request.method()).isEqualTo(HttpMethod.GET);
                    assertThat(request.url().toString()).isEqualTo("https://example.test/objects");
                    return Mono.just(ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("[{\"id\":\"1\",\"name\":\"Telefono\",\"data\":{\"price\":123.5}},{\"id\":\"2\",\"name\":\"Otro\",\"data\":null}]")
                            .build());
                }).build();
        var objetos = new ObjetoExternoService(client).listar().block();
        assertThat(objetos).hasSize(2);
        assertThat(objetos.get(0).data()).containsEntry("price", 123.5);
        assertThat(objetos.get(1).data()).isNull();
    }

    @Test
    void propagaErroresHttpDelServicioExterno() {
        WebClient client = WebClient.builder().baseUrl("https://example.test")
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build()))
                .build();
        assertThatThrownBy(() -> new ObjetoExternoService(client).listar().block())
                .isInstanceOf(WebClientResponseException.ServiceUnavailable.class);
    }
}
