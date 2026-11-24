package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.domain.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Inicio")
@RestController
public class IndexController {

	@Operation(summary = "Obtener saludo de bienvenida")
	@GetMapping("/")
	public String index() {
		return "hola amigo";
	}
}
