package py.edu.uc.lp3.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jugadas_cruzadas")
public class JugadaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long creeperA2;
    private String jugadorA3;
    private Long explosionId;
    protected JugadaEntity() { }
    public JugadaEntity(Long creeperA2, String jugadorA3, Long explosionId) {
        this.creeperA2 = creeperA2; this.jugadorA3 = jugadorA3; this.explosionId = explosionId;
    }
    public Long getId() { return id; }
    public Long getCreeperA2() { return creeperA2; }
    public String getJugadorA3() { return jugadorA3; }
    public Long getExplosionId() { return explosionId; }
}
