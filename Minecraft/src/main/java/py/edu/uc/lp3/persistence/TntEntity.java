package py.edu.uc.lp3.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import py.edu.uc.lp3.domain.Tnt;
import py.edu.uc.lp3.domain.Vector;

@Entity
@Table(name = "tnts")
public class TntEntity {
	@Id private String id;
	private double x;
	private double y;
	private double z;
	private boolean detonada;

	protected TntEntity() { }
	public TntEntity(String id, Vector posicion) {
		if (id == null || id.isBlank() || posicion == null) throw new IllegalArgumentException("Id y posicion de TNT son obligatorios");
		this.id = id;
		this.x = posicion.getX(); this.y = posicion.getY(); this.z = posicion.getZ();
	}
	public String getId() { return id; }
	public Vector getPosicion() { return new Vector(x, y, z); }
	public boolean isDetonada() { return detonada; }
	public Tnt toDomain() { return new Tnt(getPosicion(), detonada); }
	public void registrarDetonacion(Tnt tnt) {
		if (tnt == null || !tnt.isDetonada() || detonada) throw new IllegalStateException("Detonacion invalida");
		detonada = true;
	}
}
