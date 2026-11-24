package py.edu.uc.lp3.domain;

public class Enderman extends Mob implements Hostil {
	public Enderman(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public String getComportamiento() {
		return "Se teletransporta para acechar y atacar";
	}
}
