package py.edu.uc.lp3.domain;

public abstract class Entidad {
	private String id;
	private Vector posicion;
	private int salud;
	private int saludMaxima;

	public Entidad(String id, int saludMaxima) {
		setId(id);
		this.saludMaxima = saludMaxima;
		this.salud = this.saludMaxima;
		this.posicion = new Vector();
	}

	public abstract String getComportamiento();

	public void moverse() {
		System.out.println(getId() + " se mueve.");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		this.posicion = posicion;
	}

	public int getSalud() {
		return salud;
	}

	public void setSalud(int salud) {
		this.salud = salud;
	}

	public int getSaludMaxima() {
		return saludMaxima;
	}

	public void setSaludMaxima(int saludMaxima) {
		this.saludMaxima = saludMaxima;
	}
}
