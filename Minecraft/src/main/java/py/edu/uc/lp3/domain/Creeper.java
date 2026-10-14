package py.edu.uc.lp3.domain;

public class Creeper extends Mob implements Hostil, Explosivo, UICapable {
	private final String imagen;
	private final String sonido;
	private final double velocidad;

	public Creeper(String id, int saludMaxima) {
		super(id, saludMaxima);
		this.imagen = "creeper.png";
		this.sonido = "creeper.ogg";
		this.velocidad = 0.25;
	}

	@Override
	public void atacar(Entidad objetivo) {
		System.out.println(getId() + " explota junto a " + objetivo.getId());
		objetivo.recibirDano(20);
	}

	@Override
	public String explotar() {
		return "El creeper se acerca en silencio y explota";
	}

	@Override
	public String getImagen() {
		return imagen;
	}

	@Override
	public String getSonido() {
		return sonido;
	}

	@Override
	public double getVelocidad() {
		return velocidad;
	}

	@Override
	public String mostrarEnUI() {
		return "Creeper animado en " + getPosicion() + " con sonido " + sonido;
	}

	@Override
	public String getComportamiento() {
		return "Se acerca en silencio y explota";
	}
}
