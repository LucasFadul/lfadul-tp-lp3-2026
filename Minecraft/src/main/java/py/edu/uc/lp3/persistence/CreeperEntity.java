package py.edu.uc.lp3.persistence;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CREEPER")
public class CreeperEntity extends EntidadEntity {
    protected CreeperEntity() { }

    public CreeperEntity(String id, int salud, int saludMaxima, double x, double y, double z, int experiencia, String profesion) {
        super(id, "CREEPER", salud, saludMaxima, x, y, z, experiencia, profesion);
    }
}
