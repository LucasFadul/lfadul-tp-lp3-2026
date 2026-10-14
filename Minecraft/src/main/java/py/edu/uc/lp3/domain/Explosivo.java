package py.edu.uc.lp3.domain;

public interface Explosivo {

	default String explotar() {
		return "Explota y afecta los bloques cercanos";
	}
}
