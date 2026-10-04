package py.edu.uc.lp3.domain;

public class Jugador extends Entidad {
	private Inventario inventario;
	private int experiencia;

	public Jugador(String id) {
		this(id, 20);
	}

	public Jugador(String id, int saludMaxima, int experiencia, Vector posicion) {
		this(id, saludMaxima);
		setExperiencia(experiencia);
		setPosicion(posicion);
	}

	public Jugador(String id, int saludMaxima) {
		super(id, saludMaxima);
		this.inventario = new Inventario();
	}

	public void interactuar(Entidad entidad) {
		System.out.println(getId() + " interactua con " + entidad.getId());
	}

	public void construir(Bloque bloque) {
		System.out.println(getId() + " coloca un bloque de " + bloque.getTipo());
	}

	@Override
	public String getComportamiento() {
		return "Explora, construye e interactua con el mundo";
	}

	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	public int getExperiencia() {
		return experiencia;
	}

	public void setExperiencia(int experiencia) {
		if (experiencia < 0) throw new IllegalArgumentException("La experiencia debe ser no negativa");
		this.experiencia = experiencia;
	}
}
