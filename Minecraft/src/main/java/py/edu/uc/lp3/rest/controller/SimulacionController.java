package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.rest.dto.ErrorResponse;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.persistence.ExplosionEntity;
import py.edu.uc.lp3.persistence.TntEntity;
import py.edu.uc.lp3.service.ExplosionService;

@Tag(name = "Simulaciones")
@RestController
@RequestMapping("/api")
public class SimulacionController {
	private final ExplosionService service;
	public SimulacionController(ExplosionService service) { this.service = service; }
	public record TntRequest(String id, Vector posicion) { }
	public record TntResponse(String id, Vector posicion, boolean detonada) { }
	public record ExplosionRequest(String tntId, String objetivoId) { }

	@Operation(summary = "Crear una TNT")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Conflicto con el estado actual", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	@PostMapping("/tnts")
	public ResponseEntity<TntResponse> crearTnt(@RequestBody TntRequest request) {
		TntEntity creada = service.crearTnt(request.id(), request.posicion());
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(creada.getId()).toUri();
		return ResponseEntity.created(uri).body(respuesta(creada));
	}
	@Operation(summary = "Obtener una TNT por su ID")
	@ApiResponse(responseCode = "200", description = "Operación realizada correctamente")
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@GetMapping("/tnts/{id}")
	public TntResponse obtenerTnt(@PathVariable String id) { return respuesta(service.obtenerTnt(id)); }
	@Operation(summary = "Simular y registrar una explosión", description = "La TNT causa hasta 8 puntos de daño dentro de 4 bloques y solo puede detonarse una vez. Un objetivo sin vida o una TNT ya detonada producen 409; al rechazar, no se modifican los registros.")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Conflicto con el estado actual", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	@PostMapping("/explosiones")
	public ResponseEntity<ExplosionEntity> simular(@RequestBody ExplosionRequest request) {
		ExplosionEntity explosion = service.simular(request.tntId(), request.objetivoId());
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(explosion.getId()).toUri();
		return ResponseEntity.created(uri).body(explosion);
	}
	@Operation(summary = "Obtener una explosión por su ID")
	@ApiResponse(responseCode = "200", description = "Operación realizada correctamente")
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@GetMapping("/explosiones/{id}")
	public ExplosionEntity obtenerExplosion(@PathVariable Long id) { return service.obtenerExplosion(id); }
	private TntResponse respuesta(TntEntity tnt) { return new TntResponse(tnt.getId(), tnt.getPosicion(), tnt.isDetonada()); }
}
