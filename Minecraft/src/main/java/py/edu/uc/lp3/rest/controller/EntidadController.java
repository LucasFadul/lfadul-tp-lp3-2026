package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.rest.dto.ErrorResponse;
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
import py.edu.uc.lp3.rest.dto.ExplosionRequest;
import py.edu.uc.lp3.rest.dto.SimulacionExplosionResponse;
import py.edu.uc.lp3.service.EntidadService;

@Tag(name = "Entidades")
@RestController
@RequestMapping("/api/entidades")
public class EntidadController {
	private final EntidadService service;

	public EntidadController(EntidadService service) { this.service = service; }

	@Operation(summary = "Listar entidades persistidas")
	@GetMapping
	public List<EntidadResponse> listar() { return service.listar(); }

	@Operation(summary = "Simular una explosión sobre una entidad", description = "Una TNT causa hasta 8 puntos de daño dentro de 4 bloques; fuera del radio no causa daño. Un objetivo sin vida produce 409.")
	@ApiResponse(responseCode = "200", description = "Operación realizada correctamente")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Conflicto con el estado actual", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@PostMapping("/{id}/explosiones")
	public SimulacionExplosionResponse simularExplosion(@PathVariable String id,
			@RequestBody ExplosionRequest request) {
		return service.simularExplosion(id, request);
	}


	@Operation(summary = "Validar una explosión sin guardar cambios",
			description = "Calcula el daño de TNT sobre una entidad existente sin modificar su salud ni consumir una TNT. Radio 4 y daño máximo 8. Rechaza un objetivo sin vida con 409 y uno inexistente con 404.")
	@ApiResponse(responseCode = "200", description = "Validación exitosa; daño y salud final simulados")
	@ApiResponse(responseCode = "400", description = "Posición inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Entidad no encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Entidad sin vida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@PostMapping("/{id}/explosiones/validar")
	public SimulacionExplosionResponse validarExplosion(@PathVariable String id, @RequestBody ExplosionRequest request) {
		return service.validarExplosion(id, request);
	}

	@Operation(summary = "Obtener una entidad por su ID")
	@ApiResponse(responseCode = "200", description = "Operación realizada correctamente")
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@GetMapping("/{id}")
	public EntidadResponse obtener(@PathVariable String id) { return service.obtener(id); }

	@Operation(summary = "Crear una entidad")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Conflicto con el estado actual", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	@PostMapping
	public ResponseEntity<EntidadResponse> crear(@RequestBody EntidadRequest request) {
		EntidadResponse creada = service.crear(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(creada.id()).toUri();
		return ResponseEntity.created(location).body(creada);
	}

	@Operation(summary = "Actualizar una entidad")
	@ApiResponse(responseCode = "200", description = "Operación realizada correctamente")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@PutMapping("/{id}")
	public EntidadResponse actualizar(@PathVariable String id, @RequestBody EntidadRequest request) {
		return service.actualizar(id, request);
	}

	@Operation(summary = "Eliminar una entidad")
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "204", description = "Entidad eliminada")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable String id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
