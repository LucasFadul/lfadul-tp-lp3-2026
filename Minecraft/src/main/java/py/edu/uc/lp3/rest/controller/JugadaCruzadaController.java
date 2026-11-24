package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import py.edu.uc.lp3.persistence.JugadaEntity;
import py.edu.uc.lp3.rest.dto.JugadaRequest;
import py.edu.uc.lp3.service.JugadaCruzadaService;
import py.edu.uc.lp3.service.RechazoJugadaException;

@Tag(name = "Jugadas cruzadas")
@RestController
@RequestMapping("/api/jugadas")
public class JugadaCruzadaController {
    private final JugadaCruzadaService service;
    public JugadaCruzadaController(JugadaCruzadaService service) { this.service = service; }

    @Operation(summary = "Validar participantes externos y registrar una explosión local",
            description = "Consulta primero creepers listos de A2 y luego jugadores vivos de A3. La ausencia en una lista provoca un rechazo atribuido por A1; las consultas remotas no modifican sus bases. Si ambos aparecen, se ejecuta la explosión local y se guarda la jugada en una transacción.")
    @ApiResponse(responseCode = "201", description = "Jugada guardada")
    @ApiResponse(responseCode = "400", description = "Solicitud local inválida")
    @ApiResponse(responseCode = "404", description = "Recurso local inexistente")
    @ApiResponse(responseCode = "409", description = "Conflicto local")
    @ApiResponse(responseCode = "422", description = "A1 no encontró el participante en la lista de A2 o A3")
    @ApiResponse(responseCode = "502", description = "La consulta a un compañero falló")
    @PostMapping
    public ResponseEntity<JugadaEntity> ejecutar(@RequestBody JugadaRequest request) {
        JugadaEntity jugada = service.ejecutar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(jugada.getId()).toUri();
        return ResponseEntity.created(uri).body(jugada);
    }

    @Operation(summary = "Consultar una jugada registrada")
    @GetMapping("/{id}")
    public JugadaEntity obtener(@PathVariable Long id) { return service.obtener(id); }

    @ExceptionHandler(RechazoJugadaException.class)
    public ResponseEntity<Map<String, String>> rechazada(RechazoJugadaException error) {
        return ResponseEntity.unprocessableEntity().body(Map.of("origen", error.getOrigen(), "error", error.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> errorRemoto(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(Map.of("error", error.getReason() == null ? "Error" : error.getReason()));
    }
}
