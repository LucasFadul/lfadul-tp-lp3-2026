package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.domain.exception.EntidadSinVidaException;
import py.edu.uc.lp3.domain.exception.TntDetonadaException;

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
		if (posicion == null) throw new IllegalArgumentException("La posicion es obligatoria");
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

	public int explotar(Entidad objetivo) {
		if (objetivo == null) throw new IllegalArgumentException("El objetivo es obligatorio");
		if (detonada) throw new TntDetonadaException("La TNT ya fue detonada");
		if (!objetivo.estaVivo()) throw new EntidadSinVidaException(objetivo.getId());
		int saludAnterior = objetivo.getSalud();
		if (posicion.distanciaHasta(objetivo.getPosicion()) <= RADIO_EXPLOSION) {
			objetivo.recibirDano(DANO_EXPLOSION);
		}
		detonada = true;
		return saludAnterior - objetivo.getSalud();
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
