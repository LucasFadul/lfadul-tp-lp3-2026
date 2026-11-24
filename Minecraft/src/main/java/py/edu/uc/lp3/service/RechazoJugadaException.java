package py.edu.uc.lp3.service;

public class RechazoJugadaException extends RuntimeException {
    private final String origen;
    public RechazoJugadaException(String origen, String mensaje) { super(mensaje); this.origen = origen; }
    public String getOrigen() { return origen; }
}
