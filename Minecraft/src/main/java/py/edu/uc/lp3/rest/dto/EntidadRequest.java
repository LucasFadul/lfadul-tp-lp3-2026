package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Vector;

public class EntidadRequest {
	private String id;
	private String tipo;
	private Integer saludMaxima;
	private Integer salud;
	private Vector posicion = new Vector();
	private Integer experiencia = 0;
	private String profesion;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }
	public Integer getSaludMaxima() { return saludMaxima; }
	public void setSaludMaxima(Integer saludMaxima) { this.saludMaxima = saludMaxima; }
	public Integer getSalud() { return salud; }
	public void setSalud(Integer salud) { this.salud = salud; }
	public Vector getPosicion() { return posicion; }
	public void setPosicion(Vector posicion) { this.posicion = posicion; }
	public Integer getExperiencia() { return experiencia; }
	public void setExperiencia(Integer experiencia) { this.experiencia = experiencia; }
	public String getProfesion() { return profesion; }
	public void setProfesion(String profesion) { this.profesion = profesion; }
}
