package py.edu.uc.lp3.persistence;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ZOMBIE")
public class ZombieEntity extends EntidadEntity {
    protected ZombieEntity() { }

    public ZombieEntity(String id, int salud, int saludMaxima, double x, double y, double z, int experiencia, String profesion) {
        super(id, "ZOMBIE", salud, saludMaxima, x, y, z, experiencia, profesion);
    }
}
