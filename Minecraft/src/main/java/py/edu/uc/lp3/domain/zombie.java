package py.edu.uc.lp3.domain;

public class zombie extends Mob implements Hostil {
	public zombie(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	@Override
	public void atacar(Entidad objetivo) {
		System.out.println(getId() + " ataca cuerpo a cuerpo a " + objetivo.getId());
		objetivo.recibirDano(5);
	}

	@Override
	public String getComportamiento() {
		return "Persigue objetivos y ataca cuerpo a cuerpo";
	}
}
