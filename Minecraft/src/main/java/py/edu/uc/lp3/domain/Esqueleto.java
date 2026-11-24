package py.edu.uc.lp3.domain;

public class Esqueleto extends Mob implements Hostil {
	public Esqueleto(String id) {
		this(id, 20);
	}

	public Esqueleto(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public String getComportamiento() {
		return "Mantiene distancia y ataca con flechas";
	}
}
