package py.edu.uc.lp3.domain;

public class zombie extends Mob implements Hostil {
	public zombie(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public String getComportamiento() {
		return "Persigue objetivos y ataca cuerpo a cuerpo";
	}
}
