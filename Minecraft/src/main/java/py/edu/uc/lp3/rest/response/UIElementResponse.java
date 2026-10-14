package py.edu.uc.lp3.rest.response;

import py.edu.uc.lp3.domain.Vector;

public record UIElementResponse(
		String tipo,
		String imagen,
		Vector posicion,
		String sonido,
		double velocidad,
		String mensaje) {
}
