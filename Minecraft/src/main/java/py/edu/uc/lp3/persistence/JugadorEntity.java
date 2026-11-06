package py.edu.uc.lp3.persistence;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("JUGADOR")
public class JugadorEntity extends EntidadEntity {
    protected JugadorEntity() { }

    public JugadorEntity(String id, int salud, int saludMaxima, double x, double y, double z, int experiencia, String profesion) {
        super(id, "JUGADOR", salud, saludMaxima, x, y, z, experiencia, profesion);
    }
}
