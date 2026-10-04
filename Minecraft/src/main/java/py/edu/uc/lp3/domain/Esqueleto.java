package py.edu.uc.lp3.domain;

public class Esqueleto extends Mob implements Hostil {
	public Esqueleto(String id) {
		this(id, 20);
	}

	public Esqueleto(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public void atacar(Entidad objetivo) {
		atacar(objetivo, 0);
	}

	public void atacar(Entidad objetivo, double distancia) {
		if (!Double.isFinite(distancia) || distancia < 0)
			throw new IllegalArgumentException("La distancia debe ser finita y no negativa");
		if (objetivo == null) throw new IllegalArgumentException("El objetivo es obligatorio");
		if (distancia <= 16) objetivo.recibirDano(4);
	}

	@Override
	public String getComportamiento() {
		return "Mantiene distancia y ataca con flechas";
	}
}
