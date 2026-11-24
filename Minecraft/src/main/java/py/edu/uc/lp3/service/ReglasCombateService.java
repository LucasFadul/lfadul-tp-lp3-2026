package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;

/** Validaciones y reglas de combate de los ejemplos del modelo. */
@Service
public class ReglasCombateService {
    private final ReglasExplosionService posiciones;

    public ReglasCombateService(ReglasExplosionService posiciones) { this.posiciones = posiciones; }

    public Jugador crearJugador(String id, int saludMaxima, int experiencia, Vector posicion) {
        if (id == null || id.isBlank()) throw new EntidadInvalidaException("El id no puede estar vacio");
        if (saludMaxima <= 0) throw new EntidadInvalidaException("La salud maxima debe ser positiva");
        if (experiencia < 0) throw new EntidadInvalidaException("La experiencia debe ser no negativa");
        posiciones.validarPosicion(posicion);
        return new Jugador(id, saludMaxima, experiencia, posicion);
    }

    public void recibirDano(Entidad objetivo, int cantidad) {
        if (objetivo == null) throw new EntidadInvalidaException("El objetivo es obligatorio");
        if (cantidad < 0) throw new EntidadInvalidaException("El dano no puede ser negativo");
        objetivo.setSalud(Math.max(0, objetivo.getSalud() - cantidad));
    }

    public void curar(Entidad objetivo, int cantidad) {
        if (objetivo == null) throw new EntidadInvalidaException("El objetivo es obligatorio");
        if (cantidad < 0) throw new EntidadInvalidaException("La curacion no puede ser negativa");
        objetivo.setSalud((int) Math.min(objetivo.getSaludMaxima(), (long) objetivo.getSalud() + cantidad));
    }

    public void atacarConEsqueleto(Entidad objetivo) { atacarConEsqueleto(objetivo, 0); }

    public void atacarConEsqueleto(Entidad objetivo, double distancia) {
        if (objetivo == null) throw new EntidadInvalidaException("El objetivo es obligatorio");
        if (!Double.isFinite(distancia) || distancia < 0)
            throw new EntidadInvalidaException("La distancia debe ser finita y no negativa");
        if (distancia <= 16) recibirDano(objetivo, 4);
    }
}
