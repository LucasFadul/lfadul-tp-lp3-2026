package py.edu.uc.lp3.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.rest.dto.EntidadResponse;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisparoIntegradoServiceTests {
    private EntidadResponse entidad(String id, String tipo, double x) {
        return new EntidadResponse(id, tipo, 20, 20, new Vector(x, 0, 0), "", 0, null);
    }

    @Test
    void combinaElAtaqueLocalConLaAccionExternaSinModificarEntidades() {
        EntidadService entidades = mock(EntidadService.class);
        when(entidades.obtener("s")).thenReturn(entidad("s", "ESQUELETO", 0));
        when(entidades.obtener("j")).thenReturn(entidad("j", "JUGADOR", 4.2));
        WebClient cliente = WebClient.builder().baseUrl("http://example.test").exchangeFunction(request -> {
            assertThat(request.url().getPath()).isEqualTo("/entidades/esqueleto/disparar");
            assertThat(request.url().getQuery()).contains("vida=20", "danoBase=7", "velocidad=2", "usaArco=true", "distancia=5");
            return Mono.just(ClientResponse.create(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"accion\":\"Dispara una flecha\"}").build());
        }).build();
        var resultado = new DisparoIntegradoService(entidades, new ReglasCombateService(new ReglasExplosionService()), new ReglasExplosionService(), cliente).simular("s", "j", 7, 2, true).block();
        assertThat(resultado.distancia()).isEqualTo(4.2);
        assertThat(resultado.distanciaEnviada()).isEqualTo(5);
        assertThat(resultado.saludFinalSimulada()).isEqualTo(16);
        assertThat(resultado.danoLocal()).isEqualTo(4);
        assertThat(resultado.accionExterna()).isEqualTo("Dispara una flecha");
        verify(entidades, never()).actualizar(anyString(), any());
    }

    @Test
    void rechazaTiposInvalidosYComunicaFallasExternas() {
        EntidadService entidades = mock(EntidadService.class);
        when(entidades.obtener("s")).thenReturn(entidad("s", "CERDO", 0));
        when(entidades.obtener("j")).thenReturn(entidad("j", "JUGADOR", 20));
        WebClient cliente = WebClient.builder().baseUrl("http://example.test")
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build())).build();
        DisparoIntegradoService service = new DisparoIntegradoService(entidades, new ReglasCombateService(new ReglasExplosionService()), new ReglasExplosionService(), cliente);
        assertThatThrownBy(() -> service.simular("s", "j", 4, 1, true)).isInstanceOf(EntidadInvalidaException.class);
        when(entidades.obtener("s")).thenReturn(entidad("s", "ESQUELETO", 0));
        assertThatThrownBy(() -> service.simular("s", "j", 4, 1, true).block())
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY));
    }
}
