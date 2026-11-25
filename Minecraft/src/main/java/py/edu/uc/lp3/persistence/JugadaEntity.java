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
    private Double distanciaJugadorA3;
    private Integer danoSimuladoA2;
    private Integer saludSimuladaA3;
    private String creeperA3;
    private Double radioA3;
    private Integer danoAplicadoA3;
    private Integer saludFinalA3;
    private Long bloqueA3;
    private Boolean bloqueDestruidoA3;
    protected JugadaEntity() { }
    public JugadaEntity(Long creeperA2, String jugadorA3, Long explosionId, Double distanciaJugadorA3,
            Integer danoSimuladoA2, Integer saludSimuladaA3, String creeperA3, Double radioA3,
            Integer danoAplicadoA3, Integer saludFinalA3, Long bloqueA3, Boolean bloqueDestruidoA3) {
        this.creeperA2 = creeperA2; this.jugadorA3 = jugadorA3; this.explosionId = explosionId;
        this.distanciaJugadorA3 = distanciaJugadorA3; this.danoSimuladoA2 = danoSimuladoA2;
        this.saludSimuladaA3 = saludSimuladaA3;
        this.creeperA3 = creeperA3; this.radioA3 = radioA3; this.danoAplicadoA3 = danoAplicadoA3;
        this.saludFinalA3 = saludFinalA3; this.bloqueA3 = bloqueA3; this.bloqueDestruidoA3 = bloqueDestruidoA3;
    }
    public Long getId() { return id; }
    public Long getCreeperA2() { return creeperA2; }
    public String getJugadorA3() { return jugadorA3; }
    public Long getExplosionId() { return explosionId; }
    public Double getDistanciaJugadorA3() { return distanciaJugadorA3; }
    public Integer getDanoSimuladoA2() { return danoSimuladoA2; }
    public Integer getSaludSimuladaA3() { return saludSimuladaA3; }
    public String getCreeperA3() { return creeperA3; }
    public Double getRadioA3() { return radioA3; }
    public Integer getDanoAplicadoA3() { return danoAplicadoA3; }
    public Integer getSaludFinalA3() { return saludFinalA3; }
    public Long getBloqueA3() { return bloqueA3; }
    public Boolean getBloqueDestruidoA3() { return bloqueDestruidoA3; }
}
