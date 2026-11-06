package py.edu.uc.lp3.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "explosiones")
public class ExplosionEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	private String tntId;
	private String objetivoId;
	private int saludAnterior;
	private int saludActual;
	private int danoAplicado;

	protected ExplosionEntity() { }
	public ExplosionEntity(String tntId, String objetivoId, int saludAnterior, int saludActual, int danoAplicado) {
		this.tntId = tntId; this.objetivoId = objetivoId;
		this.saludAnterior = saludAnterior; this.saludActual = saludActual; this.danoAplicado = danoAplicado;
	}
	public Long getId() { return id; }
	public String getTntId() { return tntId; }
	public String getObjetivoId() { return objetivoId; }
	public int getSaludAnterior() { return saludAnterior; }
	public int getSaludActual() { return saludActual; }
	public int getDanoAplicado() { return danoAplicado; }
}
