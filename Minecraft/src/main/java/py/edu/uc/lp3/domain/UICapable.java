package py.edu.uc.lp3.domain;

public interface UICapable {

	String getImagen();

	Vector getPosicion();

	String getSonido();

	double getVelocidad();

	default String mostrarEnUI() {
		return "Muestra " + getImagen() + " en " + getPosicion()
				+ " con sonido " + getSonido() + " y velocidad " + getVelocidad();
	}
}
