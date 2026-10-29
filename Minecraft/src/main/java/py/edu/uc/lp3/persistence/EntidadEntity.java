package py.edu.uc.lp3.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entidades")
public class EntidadEntity {

	@Id
	private String id;
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

	public EntidadEntity(String id, String tipo, int salud, int saludMaxima,
			double x, double y, double z, int experiencia, String profesion) {
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

	public String getId() { return id; }
	public String getTipo() { return tipo; }
	public int getSalud() { return salud; }
	public int getSaludMaxima() { return saludMaxima; }
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	public int getExperiencia() { return experiencia; }
	public String getProfesion() { return profesion; }
}
