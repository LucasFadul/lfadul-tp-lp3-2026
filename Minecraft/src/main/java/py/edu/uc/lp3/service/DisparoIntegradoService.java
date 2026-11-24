package py.edu.uc.lp3.service;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.rest.dto.EntidadResponse;
import py.edu.uc.lp3.rest.dto.DisparoIntegradoResponse;
import reactor.core.publisher.Mono;

@Service
public class DisparoIntegradoService {
    private final EntidadService entidades;
    private final WebClient cliente;
    private final ReglasCombateService combate;
    private final ReglasExplosionService posiciones;

    @Autowired
    public DisparoIntegradoService(EntidadService entidades, ReglasCombateService combate, ReglasExplosionService posiciones,
            @Value("${api.companero.base-url:http://10.40.22.56:8080}") String baseUrl) {
        this(entidades, combate, posiciones, WebClient.builder().baseUrl(baseUrl).build());
    }

    DisparoIntegradoService(EntidadService entidades, ReglasCombateService combate, ReglasExplosionService posiciones, WebClient cliente) {
        this.entidades = entidades;
        this.combate = combate;
        this.posiciones = posiciones;
        this.cliente = cliente;
    }

    public Mono<DisparoIntegradoResponse> simular(String esqueletoId, String jugadorId,
            int danoBase, int velocidad, boolean usaArco) {
        if (danoBase < 0 || velocidad < 0) throw new EntidadInvalidaException("El daño base y la velocidad deben ser no negativos");
        EntidadResponse esqueleto = entidades.obtener(esqueletoId);
        EntidadResponse jugador = entidades.obtener(jugadorId);
        if (!"ESQUELETO".equals(esqueleto.tipo()) || !"JUGADOR".equals(jugador.tipo()))
            throw new EntidadInvalidaException("Se requieren una entidad ESQUELETO y una entidad JUGADOR");
        if (esqueleto.salud() <= 0 || jugador.salud() <= 0)
            throw new EntidadInvalidaException("El esqueleto y el jugador deben estar vivos");
        double distancia = posiciones.distancia(esqueleto.posicion(), jugador.posicion());
        if (!Double.isFinite(distancia) || distancia > Integer.MAX_VALUE)
            throw new EntidadInvalidaException("La distancia excede el rango admitido por la API externa");
        // La API del compañero acepta bloques enteros: se redondea hacia arriba.
        int distanciaExterna = (int) Math.ceil(distancia);
        Jugador objetivo = new Jugador(jugador.id(), jugador.saludMaxima());
        objetivo.setSalud(jugador.salud());
        combate.atacarConEsqueleto(objetivo, distancia);
        int saludFinal = objetivo.getSalud();

        return cliente.get().uri(uri -> uri.path("/entidades/esqueleto/disparar")
                .queryParam("vida", esqueleto.salud()).queryParam("danoBase", danoBase)
                .queryParam("velocidad", velocidad).queryParam("usaArco", usaArco)
                .queryParam("distancia", distanciaExterna).build())
                .retrieve().bodyToMono(AccionExterna.class)
                .timeout(Duration.ofSeconds(10))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "La API del compañero devolvió una respuesta vacía")))
                .map(respuesta -> {
                    if (respuesta.accion() == null || respuesta.accion().isBlank())
                        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "La API del compañero no devolvió una acción");
                    return new DisparoIntegradoResponse(esqueleto.id(), jugador.id(), distancia, distanciaExterna,
                            jugador.salud(), saludFinal, jugador.salud() - saludFinal, respuesta.accion());
                })
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar la API del compañero", error));
    }

    record AccionExterna(String accion) { }
}
