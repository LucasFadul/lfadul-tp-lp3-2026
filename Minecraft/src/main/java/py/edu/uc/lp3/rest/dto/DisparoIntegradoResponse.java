package py.edu.uc.lp3.rest.dto;

public record DisparoIntegradoResponse(String esqueletoId, String jugadorId, double distancia,
        int distanciaEnviada, int saludInicial, int saludFinalSimulada, int danoLocal, String accionExterna) {
}
