package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Tnt;
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
	private final EntidadService entidadService;

	public ExplosionService(TntRepository tnts, EntidadRepository entidades, ExplosionRepository explosiones, EntidadService entidadService) {
		this.tnts = tnts; this.entidades = entidades; this.explosiones = explosiones; this.entidadService = entidadService;
	}

	public TntEntity crearTnt(String id, Vector posicion) {
		if (id == null || id.isBlank() || posicion == null) throw new EntidadInvalidaException("Id y posicion de TNT son obligatorios");
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
		Tnt tnt = registroTnt.toDomain();
		Entidad objetivo = entidadService.toDomain(registroObjetivo);
		int saludAntes = objetivo.getSalud();
		int dano = tnt.explotar(objetivo);
		registroTnt.registrarDetonacion(tnt);
		registroObjetivo.setSalud(objetivo.getSalud());
		return explosiones.save(new ExplosionEntity(tntId, objetivoId, saludAntes, objetivo.getSalud(), dano));
	}

	@Transactional(readOnly = true)
	public ExplosionEntity obtenerExplosion(Long id) {
		return explosiones.findById(id).orElseThrow(() -> new EntidadNotFoundException("No existe una explosion con id " + id));
	}
}
