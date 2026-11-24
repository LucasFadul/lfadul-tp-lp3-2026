package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.domain.Tnt;
import py.edu.uc.lp3.rest.response.ExplosionResponse;

@Tag(name = "Explosivos")
@RestController
public class ExplosivoController {

	@Operation(summary = "Listar ejemplos de explosivos")
	@GetMapping("/explosivos")
	public List<ExplosionResponse> obtenerExplosivos() {
		Explosivo creeper = new Creeper("Creeper", 20);
		Explosivo tnt = new Tnt();

		return List.of(
				crearRespuesta("Creeper", creeper),
				crearRespuesta("TNT", tnt));
	}

	private ExplosionResponse crearRespuesta(String tipo, Explosivo explosivo) {
		return new ExplosionResponse(tipo, explosivo.explotar());
	}
}
