package py.edu.uc.lp3.domain.exception;

public class TntNotFoundException extends MinecraftException {
	public TntNotFoundException(String id) { super("No existe una TNT con id " + id); }
}
