package py.edu.uc.lp3.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.rest.dto.DisparoIntegradoResponse;
import py.edu.uc.lp3.service.DisparoIntegradoService;
import reactor.core.publisher.Mono;

@Tag(name = "Integración con compañero")
@RestController
@RequestMapping("/api/integracion")
public class DisparoIntegradoController {
    private final DisparoIntegradoService service;

    public DisparoIntegradoController(DisparoIntegradoService service) { this.service = service; }

    @Operation(summary = "Comparar el ataque local de un esqueleto con la acción de la API del compañero",
            description = "Simula sin modificar entidades. La distancia externa se redondea hacia arriba; las reglas de daño local y la acción externa son independientes.")
    @GetMapping("/esqueletos/{esqueletoId}/jugadores/{jugadorId}/disparo")
    public Mono<DisparoIntegradoResponse> simular(@PathVariable String esqueletoId, @PathVariable String jugadorId,
            @RequestParam(defaultValue = "4") int danoBase, @RequestParam(defaultValue = "1") int velocidad,
            @RequestParam(defaultValue = "true") boolean usaArco) {
        return service.simular(esqueletoId, jugadorId, danoBase, velocidad, usaArco);
    }
}
