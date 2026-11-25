package py.edu.uc.lp3.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JugadaRequest(
        @Schema(description = "Id del creeper en A2", example = "1") Long creeperA2,
        @Schema(description = "Id del jugador en A3", example = "steve") String jugadorA3,
        @Schema(description = "Id de una TNT local", example = "t1") String tntId,
        @Schema(description = "Id de una entidad local", example = "pig") String objetivoId,
        @Schema(description = "Id del creeper de A3 situado en (0,0,0)", example = "creeper-santiago") String creeperA3,
        @Schema(description = "Radio de la explosión en A3", example = "5") Double radioA3,
        @Schema(description = "Id opcional de un bloque en A3 que puede destruirse", example = "1") Long bloqueA3) { }
