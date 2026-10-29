package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Profesion;
import py.edu.uc.lp3.domain.Vector;

public record EntidadResponse(
		String id,
		String tipo,
		int salud,
		int saludMaxima,
		Vector posicion,
		String comportamiento,
		Integer experiencia,
		Profesion profesion) {
}
