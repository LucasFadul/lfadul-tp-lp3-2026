package py.edu.uc.lp3.lf.minecraft;

public class Esqueleto extends Mob implements Hostil {
	public Esqueleto(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public void atacar(Entidad objetivo) {
		System.out.println(getId() + " dispara una flecha a " + objetivo.getId());
		objetivo.recibirDano(4);
	}

	@Override
	public String getComportamiento() {
		return "Mantiene distancia y ataca con flechas";
	}
}
