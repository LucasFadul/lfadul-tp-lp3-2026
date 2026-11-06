package py.edu.uc.lp3.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Table(name = "entidades")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class EntidadEntity {

	@Id
	private String id;
	@Column(name = "tipo", insertable = false, updatable = false)
	private String tipo;
	private int salud;
	private int saludMaxima;
	private double x;
	private double y;
	private double z;
	private int experiencia;
	private String profesion;

	protected EntidadEntity() {
	}

	protected EntidadEntity(String id, String tipo, int salud, int saludMaxima,
			double x, double y, double z, int experiencia, String profesion) {
		if (id == null || id.isBlank() || saludMaxima <= 0 || salud < 0 || salud > saludMaxima || experiencia < 0
				|| !Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z))
			throw new IllegalArgumentException("Datos de entidad invalidos");
		this.id = id;
		this.tipo = tipo;
		this.salud = salud;
		this.saludMaxima = saludMaxima;
		this.x = x;
		this.y = y;
		this.z = z;
		this.experiencia = experiencia;
		this.profesion = profesion;
	}

	public static EntidadEntity crear(String id, String tipo, int salud, int saludMaxima,
			double x, double y, double z, int experiencia, String profesion) {
		return switch (tipo) {
			case "CERDO" -> new CerdoEntity(id, salud, saludMaxima, x, y, z, experiencia, profesion);
			case "JUGADOR" -> new JugadorEntity(id, salud, saludMaxima, x, y, z, experiencia, profesion);
			case "ESQUELETO" -> new EsqueletoEntity(id, salud, saludMaxima, x, y, z, experiencia, profesion);
			case "CREEPER" -> new CreeperEntity(id, salud, saludMaxima, x, y, z, experiencia, profesion);
			case "ALDEANO" -> new AldeanoEntity(id, salud, saludMaxima, x, y, z, experiencia, profesion);
			case "ZOMBIE" -> new ZombieEntity(id, salud, saludMaxima, x, y, z, experiencia, profesion);
			default -> throw new IllegalArgumentException("Tipo de entidad no soportado");
		};
	}

	public String getId() { return id; }
	public String getTipo() { return tipo; }
	public int getSalud() { return salud; }
	public void setSalud(int salud) {
		if (salud < 0 || salud > saludMaxima) throw new IllegalArgumentException("Salud fuera de rango");
		this.salud = salud;
	}
	public int getSaludMaxima() { return saludMaxima; }
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	public int getExperiencia() { return experiencia; }
	public String getProfesion() { return profesion; }
}
