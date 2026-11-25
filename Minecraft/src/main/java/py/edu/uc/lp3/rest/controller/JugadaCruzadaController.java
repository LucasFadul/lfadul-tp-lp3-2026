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
            description = "Comprueba la TNT local y el creeper de A2, valida el jugador y creeper de A3 y consulta la simulación de Mateo. Luego ejecuta la explosión de Santiago: modifica su jugador y puede destruir un bloque en A3. Finalmente guarda la explosión y jugada locales. No hay transacción distribuida: una falla posterior o timeout puede dejar cambios en A3 sin registro local.")
    @ApiResponse(responseCode = "201", description = "Jugada guardada")
    @ApiResponse(responseCode = "400", description = "Solicitud local inválida o jugador de A3 sin salud completa")
    @ApiResponse(responseCode = "404", description = "Recurso local inexistente")
    @ApiResponse(responseCode = "409", description = "Conflicto local")
    @ApiResponse(responseCode = "422", description = "Participante ausente o simulación rechazada; se indica el origen")
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
