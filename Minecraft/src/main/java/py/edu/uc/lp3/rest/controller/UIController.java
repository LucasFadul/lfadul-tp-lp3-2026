package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Tnt;
import py.edu.uc.lp3.domain.UICapable;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.rest.response.UIElementResponse;

@Tag(name = "Interfaz")
@RestController
public class UIController {

	@Operation(summary = "Listar elementos compatibles con la interfaz")
	@GetMapping("/ui-capables")
	public List<UIElementResponse> obtenerElementosUI() {
		Creeper creeperConPosicion = new Creeper("Creeper", 20);
		creeperConPosicion.setPosicion(new Vector(4, 64, -2));

		UICapable creeper = creeperConPosicion;
		UICapable tnt = new Tnt(new Vector(8, 64, 3));

		return List.of(
				crearRespuesta("Creeper", creeper),
				crearRespuesta("TNT", tnt));
	}

	private UIElementResponse crearRespuesta(String tipo, UICapable elemento) {
		return new UIElementResponse(
				tipo,
				elemento.getImagen(),
				elemento.getPosicion(),
				elemento.getSonido(),
				elemento.getVelocidad(),
				elemento.mostrarEnUI());
	}
}
