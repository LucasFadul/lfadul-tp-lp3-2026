package py.edu.uc.lp3.domain;

public class Tnt extends Bloque implements Explosivo, UICapable {
	public static final double RADIO_EXPLOSION = 4;
	public static final int DANO_EXPLOSION = 8;
	private final String imagen;
	private final Vector posicion;
	private final String sonido;
	private final double velocidad;
	private boolean detonada;

	public Tnt() {
		this(new Vector());
	}

	public Tnt(Vector posicion) {
		this(posicion, false);
	}

	public Tnt(Vector posicion, boolean detonada) {
		super("TNT");
		this.imagen = "tnt.png";
		this.posicion = posicion;
		this.sonido = "tnt.ogg";
		this.velocidad = 0.0;
		this.detonada = detonada;
	}

	@Override
	public String getImagen() {
		return imagen;
	}


	public boolean isDetonada() { return detonada; }

	@Override
	public Vector getPosicion() {
		return posicion;
	}

	@Override
	public String getSonido() {
		return sonido;
	}

	@Override
	public double getVelocidad() {
		return velocidad;
	}
}
