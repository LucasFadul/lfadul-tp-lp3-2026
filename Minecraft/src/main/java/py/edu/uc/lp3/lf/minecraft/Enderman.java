package py.edu.uc.lp3.lf.minecraft;

public class Enderman extends Mob implements Hostil {
	public Enderman(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public void atacar(Entidad objetivo) {
		System.out.println(getId() + " teletransporta y ataca a " + objetivo.getId());
		objetivo.recibirDano(7);
	}

	@Override
	public String getComportamiento() {
		return "Se teletransporta para acechar y atacar";
	}
}
