package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.persistence.ExplosionEntity;
import py.edu.uc.lp3.persistence.TntEntity;
import py.edu.uc.lp3.service.ExplosionService;

@RestController
@RequestMapping("/api")
public class SimulacionController {
	private final ExplosionService service;
	public SimulacionController(ExplosionService service) { this.service = service; }
	public record TntRequest(String id, Vector posicion) { }
	public record TntResponse(String id, Vector posicion, boolean detonada) { }
	public record ExplosionRequest(String tntId, String objetivoId) { }

	@PostMapping("/tnts")
	public ResponseEntity<TntResponse> crearTnt(@RequestBody TntRequest request) {
		TntEntity creada = service.crearTnt(request.id(), request.posicion());
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(creada.getId()).toUri();
		return ResponseEntity.created(uri).body(respuesta(creada));
	}
	@GetMapping("/tnts/{id}")
	public TntResponse obtenerTnt(@PathVariable String id) { return respuesta(service.obtenerTnt(id)); }
	@PostMapping("/explosiones")
	public ResponseEntity<ExplosionEntity> simular(@RequestBody ExplosionRequest request) {
		ExplosionEntity explosion = service.simular(request.tntId(), request.objetivoId());
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(explosion.getId()).toUri();
		return ResponseEntity.created(uri).body(explosion);
	}
	@GetMapping("/explosiones/{id}")
	public ExplosionEntity obtenerExplosion(@PathVariable Long id) { return service.obtenerExplosion(id); }
	private TntResponse respuesta(TntEntity tnt) { return new TntResponse(tnt.getId(), tnt.getPosicion(), tnt.isDetonada()); }
}
