package py.edu.uc.lp3;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.client.Matrf04Client;
import py.edu.uc.lp3.client.Sadez7Client;
import py.edu.uc.lp3.persistence.ExplosionRepository;
import py.edu.uc.lp3.persistence.JugadaRepository;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:jugadas", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
@Transactional
class JugadaCruzadaTests {
    @Autowired MockMvc mvc;
    @Autowired JugadaRepository jugadas;
    @Autowired ExplosionRepository explosiones;
    @MockitoBean Matrf04Client a2;
    @MockitoBean Sadez7Client a3;
    private static final String BODY = "{\"creeperA2\":1,\"jugadorA3\":\"steve\",\"tntId\":\"t1\",\"objetivoId\":\"pig\"}";

    @BeforeEach
    void preparar() throws Exception {
        mvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"pig\",\"tipo\":\"CERDO\",\"saludMaxima\":20}")).andExpect(status().isCreated());
        mvc.perform(post("/api/tnts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"t1\",\"posicion\":{\"x\":3,\"y\":0,\"z\":0}}")).andExpect(status().isCreated());
    }

    private void a2Acepta() {
        when(a2.listarCreepersListos()).thenReturn(Mono.just(List.of(new Matrf04Client.CreeperExterno(1L, "Creeper", 20, 5, 2, false))));
        when(a2.simularImpacto(20, 0, 0, 0)).thenReturn(Mono.just(new Matrf04Client.ImpactoJugador("Steve", 0.0, 20, 10, 10)));
    }
    private void a3Acepta() {
        var jugador = new Sadez7Client.EntidadExterna("steve", "jugador", 20, 20, 0, 0, 0, 0);
        when(a3.listarVivos()).thenReturn(Mono.just(List.of(jugador)));
        when(a3.validarJugador(jugador)).thenReturn(Mono.just(new Sadez7Client.JugadorValidado("steve", 20, 20)));
    }
    private void estadoSinCambios(long jugadasAntes, long explosionesAntes) throws Exception {
        assertThat(jugadas.count()).isEqualTo(jugadasAntes);
        assertThat(explosiones.count()).isEqualTo(explosionesAntes);
        mvc.perform(get("/api/entidades/pig")).andExpect(jsonPath("$.salud").value(20));
        mvc.perform(get("/api/tnts/t1")).andExpect(jsonPath("$.detonada").value(false));
    }

    @Test
    void cuandoAmbosConfirmanGuardaJugadaYExplosion() throws Exception {
        a2Acepta(); a3Acepta();
        long antes=jugadas.count();
        String location = mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.creeperA2").value(1))
                .andExpect(jsonPath("$.danoSimuladoA2").value(10))
                .andExpect(jsonPath("$.saludSimuladaA3").value(10))
                .andExpect(jsonPath("$.jugadorA3").value("steve")).andReturn().getResponse().getHeader("Location");
        assertThat(jugadas.count()).isEqualTo(antes+1);
        mvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.explosionId").isNumber());
        mvc.perform(get("/api/entidades/pig")).andExpect(jsonPath("$.salud").value(12));
        mvc.perform(get("/api/tnts/t1")).andExpect(jsonPath("$.detonada").value(true));
    }

    @Test
    void ausenciaEnA2IdentificaOrigenYNoCambiaBase() throws Exception {
        when(a2.listarCreepersListos()).thenReturn(Mono.just(List.of()));
        long j=jugadas.count(), e=explosiones.count();
        mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.origen").value("A2"));
        verifyNoInteractions(a3);
        estadoSinCambios(j,e);
    }


    @Test
    void servicioA2NoDisponibleSeDistingueDelRechazoYNoCambiaBase() throws Exception {
        when(a2.listarCreepersListos()).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar A2 (MatRF04)")));
        long j=jugadas.count(), e=explosiones.count();
        mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadGateway()).andExpect(jsonPath("$.error").value("No se pudo consultar A2 (MatRF04)"));
        verifyNoInteractions(a3);
        estadoSinCambios(j,e);
    }

    @Test
    void ausenciaEnA3IdentificaOrigenYNoCambiaBase() throws Exception {
        a2Acepta(); when(a3.listarVivos()).thenReturn(Mono.just(List.of()));
        long j=jugadas.count(), e=explosiones.count();
        mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.origen").value("A3"));
        estadoSinCambios(j,e);
    }

    @Test
    void rechazoDeSimulacionDeA2NoCambiaLaBase() throws Exception {
        a2Acepta(); a3Acepta();
        when(a2.simularImpacto(20, 0, 0, 0)).thenReturn(Mono.error(new py.edu.uc.lp3.service.RechazoJugadaException("A2", "Simulación rechazada")));
        long j=jugadas.count(), e=explosiones.count();
        mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.origen").value("A2"));
        estadoSinCambios(j,e);
    }

    @Test
    void rechazoExplicitoDeA3NoCambiaLaBase() throws Exception {
        a2Acepta(); a3Acepta();
        when(a3.validarJugador(any())).thenReturn(Mono.error(new py.edu.uc.lp3.service.RechazoJugadaException("A3", "Jugador rechazado")));
        long j=jugadas.count(), e=explosiones.count();
        mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.origen").value("A3"));
        verify(a2, never()).simularImpacto(anyInt(), anyDouble(), anyDouble(), anyDouble());
        estadoSinCambios(j,e);
    }

    @Test
    void jugadorConSaludParcialNoSeSimulaComoSiEstuvieraSano() throws Exception {
        a2Acepta();
        when(a3.listarVivos()).thenReturn(Mono.just(List.of(new Sadez7Client.EntidadExterna("steve", "jugador", 12, 20, 0, 0, 0, 0))));
        long j=jugadas.count(), e=explosiones.count();
        mvc.perform(post("/api/jugadas").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isBadRequest());
        verify(a2, never()).simularImpacto(anyInt(), anyDouble(), anyDouble(), anyDouble());
        estadoSinCambios(j,e);
    }
}
