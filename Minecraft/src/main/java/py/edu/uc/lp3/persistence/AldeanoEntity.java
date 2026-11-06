package py.edu.uc.lp3.persistence;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ALDEANO")
public class AldeanoEntity extends EntidadEntity {
    protected AldeanoEntity() { }

    public AldeanoEntity(String id, int salud, int saludMaxima, double x, double y, double z, int experiencia, String profesion) {
        super(id, "ALDEANO", salud, saludMaxima, x, y, z, experiencia, profesion);
    }
}
