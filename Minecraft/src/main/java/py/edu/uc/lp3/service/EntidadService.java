package py.edu.uc.lp3.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Profesion;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.domain.zombie;
import py.edu.uc.lp3.persistence.EntidadEntity;
import py.edu.uc.lp3.persistence.EntidadRepository;
import py.edu.uc.lp3.rest.dto.EntidadRequest;
import py.edu.uc.lp3.rest.dto.EntidadResponse;

@Service
@Transactional
public class EntidadService {
	private final EntidadRepository repository;

	public EntidadService(EntidadRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<EntidadResponse> listar() {
		return repository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public EntidadResponse obtener(String id) {
		return toResponse(buscar(id));
	}

	public EntidadResponse crear(EntidadRequest request) {
		validar(request);
		String id = request.getId().trim();
		if (repository.existsById(id)) {
			throw new EntidadConflictException("Ya existe una entidad con id " + id);
		}
		return toResponse(repository.save(toEntity(request, id)));
	}

	public EntidadResponse actualizar(String id, EntidadRequest request) {
		EntidadEntity existente = buscar(id);
		validar(request);
		if (!id.equals(request.getId().trim())) {
			throw new IllegalArgumentException("El id del cuerpo debe coincidir con el id de la ruta");
		}
		return toResponse(repository.save(toEntity(request, id)));
	}

	public void eliminar(String id) {
		EntidadEntity existente = buscar(id);
		repository.delete(existente);
	}

	private EntidadEntity buscar(String id) {
		return repository.findById(id)
				.orElseThrow(() -> new EntidadNotFoundException("No existe una entidad con id " + id));
	}

	private void validar(EntidadRequest request) {
		if (request == null) throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio");
		if (request.getId() == null || request.getId().isBlank())
			throw new IllegalArgumentException("El id no puede estar vacio");
		if (request.getTipo() == null || request.getTipo().isBlank())
			throw new IllegalArgumentException("El tipo de entidad es obligatorio");
		if (request.getSaludMaxima() == null || request.getSaludMaxima() <= 0)
			throw new IllegalArgumentException("La salud maxima debe ser positiva");
		if (request.getSalud() != null && (request.getSalud() < 0 || request.getSalud() > request.getSaludMaxima()))
			throw new IllegalArgumentException("La salud debe estar entre 0 y la salud maxima");
		if (request.getPosicion() == null)
			throw new IllegalArgumentException("La posicion es obligatoria");
		if (request.getExperiencia() == null || request.getExperiencia() < 0)
			throw new IllegalArgumentException("La experiencia debe ser no negativa");
		try {
			tipoNormalizado(request.getTipo());
		} catch (IllegalArgumentException error) {
			throw new IllegalArgumentException("Tipo no soportado. Use JUGADOR, CERDO, ESQUELETO, CREEPER, ALDEANO o ZOMBIE");
		}
		if (tipoNormalizado(request.getTipo()).equals("ALDEANO")) {
			try {
				profesion(request.getProfesion());
			} catch (IllegalArgumentException error) {
				throw new IllegalArgumentException("Profesion no valida");
			}
		}
	}

	private String tipoNormalizado(String tipo) {
		String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
		if (!List.of("JUGADOR", "CERDO", "ESQUELETO", "CREEPER", "ALDEANO", "ZOMBIE").contains(normalizado))
			throw new IllegalArgumentException("Tipo no soportado");
		return normalizado;
	}

	private Profesion profesion(String valor) {
		if (valor == null || valor.isBlank()) return Profesion.SIN_PROFESION;
		return Profesion.valueOf(valor.trim().toUpperCase(Locale.ROOT));
	}

	private EntidadEntity toEntity(EntidadRequest request, String id) {
		String tipo = tipoNormalizado(request.getTipo());
		int saludMaxima = request.getSaludMaxima();
		int salud = request.getSalud() == null ? saludMaxima : request.getSalud();
		Vector posicion = request.getPosicion();
		return new EntidadEntity(id, tipo, salud, saludMaxima,
				posicion.getX(), posicion.getY(), posicion.getZ(), request.getExperiencia(),
				tipo.equals("ALDEANO") ? profesion(request.getProfesion()).name() : null);
	}

	private EntidadResponse toResponse(EntidadEntity entity) {
		Entidad entidad = switch (entity.getTipo()) {
			case "JUGADOR" -> new Jugador(entity.getId(), entity.getSaludMaxima(), entity.getExperiencia(),
					new Vector(entity.getX(), entity.getY(), entity.getZ()));
			case "CERDO" -> new Cerdo(entity.getId(), entity.getSaludMaxima());
			case "ESQUELETO" -> new Esqueleto(entity.getId(), entity.getSaludMaxima());
			case "CREEPER" -> new Creeper(entity.getId(), entity.getSaludMaxima());
			case "ALDEANO" -> new Aldeano(entity.getId(), entity.getSaludMaxima(), Profesion.valueOf(entity.getProfesion()));
			case "ZOMBIE" -> new zombie(entity.getId(), entity.getSaludMaxima());
			default -> throw new IllegalStateException("Tipo persistido no reconocido: " + entity.getTipo());
		};
		entidad.setSalud(entity.getSalud());
		entidad.setPosicion(new Vector(entity.getX(), entity.getY(), entity.getZ()));
		Integer experiencia = entidad instanceof Jugador jugador ? jugador.getExperiencia() : null;
		Profesion profesion = entidad instanceof Aldeano aldeano ? aldeano.getProfesion() : null;
		return new EntidadResponse(entidad.getId(), entity.getTipo(), entidad.getSalud(), entidad.getSaludMaxima(),
				entidad.getPosicion(), entidad.getComportamiento(), experiencia, profesion);
	}
}
