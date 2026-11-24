package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JugadaRequest(
        @Schema(description = "Id del creeper en A2", example = "1") Long creeperA2,
        @Schema(description = "Id del jugador en A3", example = "steve") String jugadorA3,
        @Schema(description = "Id de una TNT local", example = "t1") String tntId,
        @Schema(description = "Id de una entidad local", example = "pig") String objetivoId) { }
