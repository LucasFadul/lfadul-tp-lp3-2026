package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.domain.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JugadorController {

	@GetMapping("/jugador")
	public Jugador obtenerJugador(
			@RequestParam(defaultValue = "Steve") String id,
			@RequestParam(defaultValue = "20") int saludMaxima,
			@RequestParam(defaultValue = "0") int experiencia,
			@RequestParam(defaultValue = "0") double x,
			@RequestParam(defaultValue = "0") double y,
			@RequestParam(defaultValue = "0") double z) {

		return new Jugador(id, saludMaxima, experiencia, new Vector(x, y, z));
	}

	@org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
	@org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
	public java.util.Map<String, String> errorDeDominio(IllegalArgumentException error) {
		return java.util.Map.of("error", error.getMessage());
	}

	@GetMapping("/entidades")
	public List<Entidad> obtenerEntidadesPolimorficas() {
		Entidad hostil = new Esqueleto("Skeleton", 20);
		hostil.setPosicion(new Vector(4, 64, -2));

		Entidad pacifico = new Cerdo("Pig", 10);
		pacifico.setPosicion(new Vector(-1, 63, 7));

		return List.of(hostil, pacifico);
	}
}
