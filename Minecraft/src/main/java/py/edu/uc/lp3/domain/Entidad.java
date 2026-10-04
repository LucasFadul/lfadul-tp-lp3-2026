package py.edu.uc.lp3.domain;

public abstract class Entidad {
	private String id;
	private Vector posicion;
	private int salud;
	private int saludMaxima;

	public Entidad(String id, int saludMaxima) {
		setId(id);
		if (saludMaxima <= 0) throw new IllegalArgumentException("La salud maxima debe ser positiva");
		this.saludMaxima = saludMaxima;
		this.salud = this.saludMaxima;
		this.posicion = new Vector();
	}

	public abstract String getComportamiento();

	public void moverse() {
		System.out.println(getId() + " se mueve.");
	}

	public void recibirDano(int cantidad) {
		if (cantidad < 0) throw new IllegalArgumentException("El dano no puede ser negativo");
		this.salud = Math.max(0, this.salud - cantidad);
	}

	public void curar(int cantidad) {
		if (cantidad < 0) throw new IllegalArgumentException("La curacion no puede ser negativa");
		this.salud = (int) Math.min(getSaludMaxima(), (long) this.salud + cantidad);
	}

	public boolean estaVivo() {
		return salud > 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id == null || id.isBlank()) throw new IllegalArgumentException("El id no puede estar vacio");
		this.id = id;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		if (posicion == null) throw new IllegalArgumentException("La posicion es obligatoria");
		this.posicion = posicion;
	}

	public int getSalud() {
		return salud;
	}

	public void setSalud(int salud) {
		this.salud = Math.max(0, Math.min(saludMaxima, salud));
	}

	public int getSaludMaxima() {
		return saludMaxima;
	}

	public void setSaludMaxima(int saludMaxima) {
		if (saludMaxima <= 0) throw new IllegalArgumentException("La salud maxima debe ser positiva");
		this.saludMaxima = saludMaxima;
		this.salud = Math.min(this.salud, this.saludMaxima);
	}
}
