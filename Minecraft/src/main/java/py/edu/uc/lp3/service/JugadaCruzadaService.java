package py.edu.uc.lp3.service;

import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import py.edu.uc.lp3.client.Matrf04Client;
import py.edu.uc.lp3.client.Sadez7Client;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.persistence.JugadaEntity;
import py.edu.uc.lp3.persistence.JugadaRepository;
import py.edu.uc.lp3.rest.dto.JugadaRequest;

@Service
public class JugadaCruzadaService {
    private final Matrf04Client a2;
    private final Sadez7Client a3;
    private final GuardarJugadaService guardar;
    private final JugadaRepository jugadas;
    private final ExplosionService explosiones;
    public JugadaCruzadaService(Matrf04Client a2, Sadez7Client a3, GuardarJugadaService guardar, JugadaRepository jugadas, ExplosionService explosiones) {
        this.a2 = a2; this.a3 = a3; this.guardar = guardar; this.jugadas = jugadas; this.explosiones = explosiones;
    }

    public JugadaEntity ejecutar(JugadaRequest request) {
        if (request == null || request.creeperA2() == null || request.creeperA2() <= 0 ||
                request.jugadorA3() == null || request.jugadorA3().isBlank() ||
                request.tntId() == null || request.tntId().isBlank() ||
                request.objetivoId() == null || request.objetivoId().isBlank() ||
                request.creeperA3() == null || request.creeperA3().isBlank() || request.radioA3() == null ||
                !Double.isFinite(request.radioA3()) || request.radioA3() <= 0 ||
                (request.bloqueA3() != null && request.bloqueA3() <= 0))
            throw new EntidadInvalidaException("Son obligatorios creeperA2, creeperA3, jugadorA3, radioA3 positivo, tntId y objetivoId");
        explosiones.validarParaJugada(request.tntId(), request.objetivoId());
        var creepers = a2.listarCreepersListos().block();
        if (creepers == null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta vacía de A2");
        if (creepers.stream().noneMatch(c -> Objects.equals(c.id(), request.creeperA2())))
            throw new RechazoJugadaException("A2", "El creeper no figura entre los listos para explotar");
        var vivos = a3.listarVivos().block();
        if (vivos == null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta vacía de A3");
        var jugador = vivos.stream().filter(e -> Objects.equals(e.id(), request.jugadorA3()) && "jugador".equalsIgnoreCase(e.tipo()))
                .findFirst().orElseThrow(() -> new RechazoJugadaException("A3", "El jugador no figura entre las entidades vivas"));
        // La simulación de Mateo construye un jugador nuevo con salud máxima, no acepta salud actual.
        if (jugador.salud() != jugador.saludMaxima())
            throw new EntidadInvalidaException("La simulación de A2 requiere un jugador de A3 con salud completa");
        var validado = a3.validarJugador(jugador).block();
        if (validado == null || !Objects.equals(validado.id(), jugador.id()) ||
                !Objects.equals(validado.salud(), jugador.salud()) ||
                !Objects.equals(validado.saludMaxima(), jugador.saludMaxima()))
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta incompleta o inconsistente de A3");
        var creeperA3 = a3.obtenerEntidad(request.creeperA3()).block();
        if (creeperA3 == null || !"creeper".equalsIgnoreCase(creeperA3.tipo()) || creeperA3.salud() <= 0 ||
                creeperA3.x() != 0 || creeperA3.y() != 0 || creeperA3.z() != 0)
            throw new RechazoJugadaException("A3", "El creeper de Santiago debe estar vivo y en el origen (0,0,0)");
        var impacto = a2.simularImpacto(jugador.saludMaxima(), jugador.x(), jugador.y(), jugador.z()).block();
        if (impacto == null || impacto.distancia() == null || impacto.vidaAnterior() == null ||
                impacto.danoRecibido() == null || impacto.vidaFinal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta incompleta de A2");
        var explosionA3 = a3.simularExplosion(request.creeperA3(), request.jugadorA3(), request.radioA3(), request.bloqueA3()).block();
        if (explosionA3 == null || explosionA3.jugadores() == null || explosionA3.jugadores().size() != 1 ||
                !Objects.equals(explosionA3.jugadores().get(0).id(), request.jugadorA3()) ||
                explosionA3.jugadores().get(0).danio() == null || explosionA3.jugadores().get(0).saludDespues() == null ||
                explosionA3.bloques() == null || (request.bloqueA3() != null &&
                (explosionA3.bloques().size() != 1 || !Objects.equals(explosionA3.bloques().get(0).id(), request.bloqueA3()))))
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta inválida de A3; la explosión podría haberse aplicado en su base");
        return guardar.guardar(request, impacto, explosionA3);
    }

    public JugadaEntity obtener(Long id) {
        return jugadas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la jugada"));
    }
}
