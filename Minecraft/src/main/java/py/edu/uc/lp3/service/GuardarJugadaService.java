package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.persistence.ExplosionEntity;
import py.edu.uc.lp3.client.Matrf04Client.ImpactoJugador;
import py.edu.uc.lp3.client.Sadez7Client.ExplosionA3;
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
    public JugadaEntity guardar(JugadaRequest request, ImpactoJugador impacto, ExplosionA3 explosionA3) {
        ExplosionEntity explosion = explosiones.simular(request.tntId(), request.objetivoId());
        return jugadas.save(new JugadaEntity(request.creeperA2(), request.jugadorA3(), explosion.getId(),
                impacto.distancia(), impacto.danoRecibido(), impacto.vidaFinal(), request.creeperA3(), request.radioA3(),
                explosionA3.jugadores().get(0).danio(), explosionA3.jugadores().get(0).saludDespues(),
                request.bloqueA3(), request.bloqueA3() == null ? null : explosionA3.bloques().get(0).destruido()));
    }
}
