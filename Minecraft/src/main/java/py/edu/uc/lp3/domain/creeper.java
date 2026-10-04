package py.edu.uc.lp3.domain;

public class creeper extends Mob implements Hostil {
	public creeper(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public void atacar(Entidad objetivo) {
		System.out.println(getId() + " explota junto a " + objetivo.getId());
		objetivo.recibirDano(20);
	}

	@Override
	public String getComportamiento() {
		return "Se acerca en silencio y explota";
	}
}
