package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import py.edu.uc.lp3.rest.dto.EntidadRequest;
import py.edu.uc.lp3.rest.dto.EntidadResponse;
import py.edu.uc.lp3.service.EntidadService;

@RestController
@RequestMapping("/api/entidades")
public class EntidadController {
	private final EntidadService service;

	public EntidadController(EntidadService service) { this.service = service; }

	@GetMapping
	public List<EntidadResponse> listar() { return service.listar(); }

	@GetMapping("/{id}")
	public EntidadResponse obtener(@PathVariable String id) { return service.obtener(id); }

	@PostMapping
	public ResponseEntity<EntidadResponse> crear(@RequestBody EntidadRequest request) {
		EntidadResponse creada = service.crear(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(creada.id()).toUri();
		return ResponseEntity.created(location).body(creada);
	}

	@PutMapping("/{id}")
	public EntidadResponse actualizar(@PathVariable String id, @RequestBody EntidadRequest request) {
		return service.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable String id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
