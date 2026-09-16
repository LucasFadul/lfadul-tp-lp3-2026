package py.edu.uc.lp3.lf.minecraft;

public class creeper extends Mob implements Hostil {
	public creeper(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public void atacar(Entidad objetivo) {
		System.out.println(getId() + " explota junto a " + objetivo.getId());
		objetivo.recibirDano(20);
	}
}
