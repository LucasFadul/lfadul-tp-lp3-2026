package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.rest.dto.ErrorResponse;
import py.edu.uc.lp3.domain.*;

import java.util.List;
import py.edu.uc.lp3.service.ReglasCombateService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Jugadores y ejemplos")
@RestController
public class JugadorController {

	private final ReglasCombateService reglas;
	public JugadorController(ReglasCombateService reglas) { this.reglas = reglas; }

	@Operation(summary = "Obtener un jugador con los parámetros indicados")
	@ApiResponse(responseCode = "200", description = "Operación realizada correctamente")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@GetMapping("/jugador")
	public Jugador obtenerJugador(
			@RequestParam(defaultValue = "Steve") String id,
			@RequestParam(defaultValue = "20") int saludMaxima,
			@RequestParam(defaultValue = "0") int experiencia,
			@RequestParam(defaultValue = "0") double x,
			@RequestParam(defaultValue = "0") double y,
			@RequestParam(defaultValue = "0") double z) {

		return reglas.crearJugador(id, saludMaxima, experiencia, new Vector(x, y, z));
	}

	@org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
	@org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
	public java.util.Map<String, String> errorDeDominio(IllegalArgumentException error) {
		return java.util.Map.of("error", error.getMessage());
	}

	@Operation(summary = "Listar ejemplos de entidades polimórficas")
	@GetMapping("/entidades")
	public List<Entidad> obtenerEntidadesPolimorficas() {
		Entidad hostil = new Esqueleto("Skeleton", 20);
		hostil.setPosicion(new Vector(4, 64, -2));

		Entidad pacifico = new Cerdo("Pig", 10);
		pacifico.setPosicion(new Vector(-1, 63, 7));

		return List.of(hostil, pacifico);
	}
}
