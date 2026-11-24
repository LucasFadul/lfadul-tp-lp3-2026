package py.edu.uc.lp3;

import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Tnt;
import py.edu.uc.lp3.domain.Vector;
import py.edu.uc.lp3.domain.exception.EntidadConflictException;
import py.edu.uc.lp3.domain.exception.EntidadNotFoundException;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.domain.exception.EntidadSinVidaException;
import py.edu.uc.lp3.persistence.EntidadEntity;
import py.edu.uc.lp3.persistence.EntidadRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:explosion-tests",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ExplosionTests {
	@Autowired private MockMvc mvc;
	@Autowired private EntidadRepository repository;
	@Autowired private EntityManager entityManager;

	@Test
	void explosionEnElLimiteAplicaDanoYPersisteSalud() throws Exception {
		guardar(10);
		mvc.perform(post("/api/entidades/objetivo/explosiones")
				.contentType(MediaType.APPLICATION_JSON).content("{\"posicion\":{\"x\":4,\"y\":0,\"z\":0}}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.saludAnterior").value(10))
				.andExpect(jsonPath("$.saludActual").value(2)).andExpect(jsonPath("$.danoAplicado").value(8));
		repository.flush();
		entityManager.clear();
		mvc.perform(get("/api/entidades/objetivo")).andExpect(jsonPath("$.salud").value(2));
	}

	@Test
	void fueraDelRadioNoCambiaLaSalud() throws Exception {
		guardar(10);
		mvc.perform(post("/api/entidades/objetivo/explosiones")
				.contentType(MediaType.APPLICATION_JSON).content("{\"posicion\":{\"x\":4.01,\"y\":0,\"z\":0}}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.danoAplicado").value(0));
		assertThat(repository.findById("objetivo").orElseThrow().getSalud()).isEqualTo(10);
	}

	@Test
	void danoNoDejaSaludNegativaYDistanciaIncluyeTresDimensiones() {
		Cerdo objetivo = new Cerdo("pig", 5);
		assertThat(new py.edu.uc.lp3.service.ReglasExplosionService().calcularDano(
				new Vector(), objetivo.getPosicion(), objetivo.getSalud(), objetivo.getId(), false)).isEqualTo(5);
		assertThat(new py.edu.uc.lp3.service.ReglasExplosionService().distancia(new Vector(1, 2, 3), new Vector(1, 5, 7))).isEqualTo(5);
	}

	@Test
	void objetivoSinVidaDevuelveConflicto() throws Exception {
		guardar(0);
		mvc.perform(post("/api/entidades/objetivo/explosiones")
				.contentType(MediaType.APPLICATION_JSON).content("{\"posicion\":{\"x\":0,\"y\":0,\"z\":0}}"))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.error").exists())
				.andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(EntidadSinVidaException.class));
		assertThat(repository.findById("objetivo").orElseThrow().getSalud()).isZero();
	}

	@Test
	void sinPosicionDevuelveBadRequest() throws Exception {
		mvc.perform(post("/api/entidades/objetivo/explosiones")
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists())
				.andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(EntidadInvalidaException.class));
	}

	@Test
	void objetivoInexistenteDevuelveNotFound() throws Exception {
		mvc.perform(post("/api/entidades/inexistente/explosiones")
				.contentType(MediaType.APPLICATION_JSON).content("{\"posicion\":{\"x\":0,\"y\":0,\"z\":0}}"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.error").exists())
				.andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(EntidadNotFoundException.class));
	}

	@Test
	void entidadDuplicadaDevuelveConflictoYConservaElRegistro() throws Exception {
		guardar(10);
		mvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"objetivo\",\"tipo\":\"CERDO\",\"saludMaxima\":20}"))
				.andExpect(status().isConflict())
				.andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(EntidadConflictException.class));
		assertThat(repository.findById("objetivo").orElseThrow().getSaludMaxima()).isEqualTo(10);
	}

	@Test
	void saludInvalidaDevuelveBadRequestYNoCreaLaEntidad() throws Exception {
		mvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"invalida\",\"tipo\":\"CERDO\",\"saludMaxima\":0}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("La salud maxima debe ser positiva"))
				.andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(EntidadInvalidaException.class));
		assertThat(repository.existsById("invalida")).isFalse();
	}


	@Test
	void actualizacionInvalidaConservaEstadoPersistido() throws Exception {
		guardar(10);
		mvc.perform(put("/api/entidades/objetivo").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"objetivo\",\"tipo\":\"CERDO\",\"saludMaxima\":10,\"salud\":11}"))
				.andExpect(status().isBadRequest());
		repository.flush();
		entityManager.clear();
		mvc.perform(get("/api/entidades/objetivo"))
				.andExpect(jsonPath("$.salud").value(10))
				.andExpect(jsonPath("$.saludMaxima").value(10));
	}


	@Test
	void validacionPreviaCalculaDanoSinGuardarCambios() throws Exception {
		guardar(10);
		mvc.perform(post("/api/entidades/objetivo/explosiones/validar")
				.contentType(MediaType.APPLICATION_JSON).content("{\"posicion\":{\"x\":4,\"y\":0,\"z\":0}}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.saludAnterior").value(10))
				.andExpect(jsonPath("$.saludActual").value(2)).andExpect(jsonPath("$.danoAplicado").value(8));
		repository.flush(); entityManager.clear();
		mvc.perform(get("/api/entidades/objetivo")).andExpect(jsonPath("$.salud").value(10));
	}

	@Test
	void validacionPreviaRechazaObjetivoMuertoEInexistente() throws Exception {
		guardar(0);
		String body = "{\"posicion\":{\"x\":0,\"y\":0,\"z\":0}}";
		mvc.perform(post("/api/entidades/objetivo/explosiones/validar").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isConflict());
		mvc.perform(post("/api/entidades/no-existe/explosiones/validar").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isNotFound());
		assertThat(repository.findById("objetivo").orElseThrow().getSalud()).isZero();
	}

	private void guardar(int salud) {
		repository.save(new py.edu.uc.lp3.persistence.CerdoEntity("objetivo", salud, 10, 0, 0, 0, 0, null));
	}
}
