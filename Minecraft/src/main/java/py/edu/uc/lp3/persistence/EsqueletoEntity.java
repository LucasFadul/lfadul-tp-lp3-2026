package py.edu.uc.lp3.persistence;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESQUELETO")
public class EsqueletoEntity extends EntidadEntity {
    protected EsqueletoEntity() { }

    public EsqueletoEntity(String id, int salud, int saludMaxima, double x, double y, double z, int experiencia, String profesion) {
        super(id, "ESQUELETO", salud, saludMaxima, x, y, z, experiencia, profesion);
    }
}
