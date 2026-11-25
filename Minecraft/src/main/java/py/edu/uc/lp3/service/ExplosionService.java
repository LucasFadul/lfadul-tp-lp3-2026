package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.domain.exception.EntidadConflictException;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.domain.exception.EntidadNotFoundException;
import py.edu.uc.lp3.domain.exception.TntNotFoundException;
import py.edu.uc.lp3.persistence.*;

@Service
@Transactional
public class ExplosionService {
	private final TntRepository tnts;
	private final EntidadRepository entidades;
	private final ExplosionRepository explosiones;
	private final ReglasExplosionService reglas;

	public ExplosionService(TntRepository tnts, EntidadRepository entidades, ExplosionRepository explosiones, ReglasExplosionService reglas) {
		this.tnts = tnts; this.entidades = entidades; this.explosiones = explosiones; this.reglas = reglas;
	}

	public TntEntity crearTnt(String id, Vector posicion) {
		if (id == null || id.isBlank() || posicion == null) throw new EntidadInvalidaException("Id y posicion de TNT son obligatorios");
		reglas.validarPosicion(posicion);
		id = id.trim();
		if (tnts.existsById(id)) throw new EntidadConflictException("Ya existe una TNT con id " + id);
		return tnts.save(new TntEntity(id, posicion));
	}

	@Transactional(readOnly = true)
	public TntEntity obtenerTnt(String id) {
		return tnts.findById(id).orElseThrow(() -> new TntNotFoundException(id));
	}

	public ExplosionEntity simular(String tntId, String objetivoId) {
		if (tntId == null || tntId.isBlank() || objetivoId == null || objetivoId.isBlank())
			throw new EntidadInvalidaException("Los ids de TNT y objetivo son obligatorios");
		TntEntity registroTnt = tnts.buscarParaDetonar(tntId).orElseThrow(() -> new TntNotFoundException(tntId));
		EntidadEntity registroObjetivo = entidades.buscarParaExplosion(objetivoId)
				.orElseThrow(() -> new EntidadNotFoundException("No existe una entidad con id " + objetivoId));
		int saludAntes = registroObjetivo.getSalud();
		int dano = reglas.calcularDano(registroTnt.getPosicion(),
				new Vector(registroObjetivo.getX(), registroObjetivo.getY(), registroObjetivo.getZ()),
				saludAntes, objetivoId, registroTnt.isDetonada());
		registroTnt.setDetonada(true);
		registroObjetivo.setSalud(saludAntes - dano);
		return explosiones.save(new ExplosionEntity(tntId, objetivoId, saludAntes, registroObjetivo.getSalud(), dano));
	}

	/** Comprueba los recursos locales antes de ejecutar la operación con efectos de A3. */
	@Transactional(readOnly = true)
	public void validarParaJugada(String tntId, String objetivoId) {
		TntEntity tnt = tnts.findById(tntId).orElseThrow(() -> new TntNotFoundException(tntId));
		EntidadEntity objetivo = entidades.findById(objetivoId)
				.orElseThrow(() -> new EntidadNotFoundException("No existe una entidad con id " + objetivoId));
		reglas.calcularDano(tnt.getPosicion(), new Vector(objetivo.getX(), objetivo.getY(), objetivo.getZ()),
				objetivo.getSalud(), objetivoId, tnt.isDetonada());
	}

	@Transactional(readOnly = true)
	public ExplosionEntity obtenerExplosion(Long id) {
		return explosiones.findById(id).orElseThrow(() -> new EntidadNotFoundException("No existe una explosion con id " + id));
	}
}
