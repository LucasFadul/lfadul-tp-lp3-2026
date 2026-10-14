package py.edu.uc.lp3.domain;

public class Tnt extends Bloque implements Explosivo, UICapable {
	private final String imagen;
	private final Vector posicion;
	private final String sonido;
	private final double velocidad;

	public Tnt() {
		this(new Vector());
	}

	public Tnt(Vector posicion) {
		super("TNT");
		if (posicion == null) throw new IllegalArgumentException("La posicion es obligatoria");
		this.imagen = "tnt.png";
		this.posicion = posicion;
		this.sonido = "tnt.ogg";
		this.velocidad = 0.0;
	}

	@Override
	public String getImagen() {
		return imagen;
	}

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
