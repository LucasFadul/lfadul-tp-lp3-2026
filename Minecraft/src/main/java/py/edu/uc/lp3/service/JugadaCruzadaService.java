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
        if (vivos.stream().noneMatch(e -> Objects.equals(e.id(), request.jugadorA3()) && "jugador".equalsIgnoreCase(e.tipo())))
            throw new RechazoJugadaException("A3", "El jugador no figura entre las entidades vivas");
        return guardar.guardar(request);
    }

    public JugadaEntity obtener(Long id) {
        return jugadas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la jugada"));
    }
}
