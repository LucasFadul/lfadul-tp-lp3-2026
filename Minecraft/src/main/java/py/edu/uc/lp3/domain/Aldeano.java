package py.edu.uc.lp3.domain;

import java.util.ArrayList;
import java.util.List;

public class Aldeano extends Pacifico {
	private Profesion profesion;

	public Aldeano(String id, int saludMaxima, Profesion profesion) {
		super(id, saludMaxima);
		this.profesion = profesion;
	}

	public List<Object> ofrecerTrueques() {
		return new ArrayList<>();
	}

	@Override
	public String getComportamiento() {
		return "Ofrece trueques y evita el combate";
	}

	public Profesion getProfesion() {
		return profesion;
	}

	public void setProfesion(Profesion profesion) {
		this.profesion = profesion;
	}
}
