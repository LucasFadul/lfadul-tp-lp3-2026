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
    public JugadaCruzadaService(Matrf04Client a2, Sadez7Client a3, GuardarJugadaService guardar, JugadaRepository jugadas) {
        this.a2 = a2; this.a3 = a3; this.guardar = guardar; this.jugadas = jugadas;
    }

    public JugadaEntity ejecutar(JugadaRequest request) {
        if (request == null || request.creeperA2() == null || request.creeperA2() <= 0 ||
                request.jugadorA3() == null || request.jugadorA3().isBlank() ||
                request.tntId() == null || request.tntId().isBlank() ||
                request.objetivoId() == null || request.objetivoId().isBlank())
            throw new EntidadInvalidaException("Son obligatorios creeperA2, jugadorA3, tntId y objetivoId");
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
        var impacto = a2.simularImpacto(jugador.saludMaxima(), jugador.x(), jugador.y(), jugador.z()).block();
        if (impacto == null || impacto.distancia() == null || impacto.vidaAnterior() == null ||
                impacto.danoRecibido() == null || impacto.vidaFinal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta incompleta de A2");
        return guardar.guardar(request, impacto);
    }

    public JugadaEntity obtener(Long id) {
        return jugadas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la jugada"));
    }
}
