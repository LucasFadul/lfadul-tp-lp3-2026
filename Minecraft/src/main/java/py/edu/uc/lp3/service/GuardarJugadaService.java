package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.persistence.ExplosionEntity;
import py.edu.uc.lp3.client.Matrf04Client.ImpactoJugador;
import py.edu.uc.lp3.persistence.JugadaEntity;
import py.edu.uc.lp3.persistence.JugadaRepository;
import py.edu.uc.lp3.rest.dto.JugadaRequest;

@Service
public class GuardarJugadaService {
    private final ExplosionService explosiones;
    private final JugadaRepository jugadas;
    public GuardarJugadaService(ExplosionService explosiones, JugadaRepository jugadas) {
        this.explosiones = explosiones; this.jugadas = jugadas;
    }

    @Transactional
    public JugadaEntity guardar(JugadaRequest request, ImpactoJugador impacto) {
        ExplosionEntity explosion = explosiones.simular(request.tntId(), request.objetivoId());
        return jugadas.save(new JugadaEntity(request.creeperA2(), request.jugadorA3(), explosion.getId(),
                impacto.distancia(), impacto.danoRecibido(), impacto.vidaFinal()));
    }
}
