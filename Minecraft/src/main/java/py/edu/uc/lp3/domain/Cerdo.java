package py.edu.uc.lp3.domain;

public class Cerdo extends Pacifico {
	public Cerdo(String id) {
		this(id, 10);
	}

	public Cerdo(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public String getComportamiento() {
		return "Deambula pacificamente por el terreno";
	}
}
