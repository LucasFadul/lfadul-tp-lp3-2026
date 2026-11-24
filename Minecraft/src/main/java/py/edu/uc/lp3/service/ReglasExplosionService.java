package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.domain.Tnt;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.domain.exception.EntidadSinVidaException;
import py.edu.uc.lp3.domain.exception.TntDetonadaException;

@Service
public class ReglasExplosionService {
    public int calcularDano(Vector origen, Vector objetivo, int salud, String objetivoId, boolean detonada) {
        validarPosicion(origen);
        validarPosicion(objetivo);
        if (detonada) throw new TntDetonadaException("La TNT ya fue detonada");
        if (salud <= 0) throw new EntidadSinVidaException(objetivoId);
        return distancia(origen, objetivo) <= Tnt.RADIO_EXPLOSION ? Math.min(salud, Tnt.DANO_EXPLOSION) : 0;
    }

    public double distancia(Vector origen, Vector objetivo) {
        validarPosicion(origen);
        validarPosicion(objetivo);
        return Math.hypot(Math.hypot(origen.getX() - objetivo.getX(), origen.getY() - objetivo.getY()), origen.getZ() - objetivo.getZ());
    }

    public void validarPosicion(Vector posicion) {
        if (posicion == null || !Double.isFinite(posicion.getX()) || !Double.isFinite(posicion.getY()) || !Double.isFinite(posicion.getZ()))
            throw new EntidadInvalidaException("La posicion debe contener coordenadas finitas");
    }
}
