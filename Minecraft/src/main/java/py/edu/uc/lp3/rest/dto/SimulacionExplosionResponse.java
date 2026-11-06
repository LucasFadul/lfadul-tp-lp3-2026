package py.edu.uc.lp3.rest.dto;

public record SimulacionExplosionResponse(
		String id, int saludAnterior, int saludActual, int danoAplicado, double radio) {
}
