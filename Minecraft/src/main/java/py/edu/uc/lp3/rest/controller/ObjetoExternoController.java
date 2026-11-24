package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.rest.dto.ObjetoExternoResponse;
import py.edu.uc.lp3.service.ObjetoExternoService;
import reactor.core.publisher.Mono;

@Tag(name = "Objetos externos")
@RestController
@RequestMapping("/api/objetos-externos")
public class ObjetoExternoController {
    private final ObjetoExternoService service;

    public ObjetoExternoController(ObjetoExternoService service) {
        this.service = service;
    }

    @Operation(summary = "Consultar objetos de la API externa")
	@GetMapping
    public Mono<List<ObjetoExternoResponse>> listar() {
        return service.listar();
    }
}
