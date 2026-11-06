package py.edu.uc.lp3.domain.exception;

public class EntidadSinVidaException extends EntidadException {
	public EntidadSinVidaException(String id) {
		super("No se puede simular una explosion sobre la entidad sin vida " + id);
	}
}
