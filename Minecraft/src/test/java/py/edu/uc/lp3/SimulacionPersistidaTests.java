package py.edu.uc.lp3;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:simulacion", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
@Transactional
class SimulacionPersistidaTests {
	@Autowired MockMvc mvc;
	@Autowired EntityManager entityManager;

	@Test
	void crudConDosHijasYExplosionGuardaAmbosEstados() throws Exception {
		crearEntidad("pig", "CERDO", 20);
		crearEntidad("steve", "JUGADOR", 20);
		mvc.perform(get("/api/entidades/steve")).andExpect(status().isOk()).andExpect(jsonPath("$.tipo").value("JUGADOR"));
		mvc.perform(post("/api/tnts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"t1\",\"posicion\":{\"x\":3,\"y\":0,\"z\":0}}"))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/explosiones").contentType(MediaType.APPLICATION_JSON)
				.content("{\"tntId\":\"t1\",\"objetivoId\":\"pig\"}"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.danoAplicado").value(8))
				.andExpect(jsonPath("$.saludActual").value(12));
		entityManager.flush(); entityManager.clear();
		mvc.perform(get("/api/entidades/pig")).andExpect(jsonPath("$.salud").value(12));
		mvc.perform(get("/api/tnts/t1")).andExpect(jsonPath("$.detonada").value(true));
		mvc.perform(get("/api/explosiones/1")).andExpect(status().isOk()).andExpect(jsonPath("$.objetivoId").value("pig"));
		mvc.perform(post("/api/explosiones").contentType(MediaType.APPLICATION_JSON)
				.content("{\"tntId\":\"t1\",\"objetivoId\":\"pig\"}"))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.error").value("La TNT ya fue detonada"));
		mvc.perform(get("/api/entidades/pig")).andExpect(jsonPath("$.salud").value(12));
		mvc.perform(put("/api/entidades/steve").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"steve\",\"tipo\":\"JUGADOR\",\"saludMaxima\":30,\"experiencia\":5}"))
				.andExpect(status().isOk());
		entityManager.flush(); entityManager.clear();
		mvc.perform(get("/api/entidades/steve")).andExpect(jsonPath("$.saludMaxima").value(30));
		mvc.perform(delete("/api/entidades/steve")).andExpect(status().isNoContent());
		mvc.perform(get("/api/entidades/steve")).andExpect(status().isNotFound());
	}

	@Test
	void objetivoMuertoNoConsumeLaTnt() throws Exception {
		mvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"muerto\",\"tipo\":\"CERDO\",\"saludMaxima\":10,\"salud\":0}"))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/tnts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"t2\",\"posicion\":{\"x\":0,\"y\":0,\"z\":0}}"))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/explosiones").contentType(MediaType.APPLICATION_JSON)
				.content("{\"tntId\":\"t2\",\"objetivoId\":\"muerto\"}"))
				.andExpect(status().isConflict());
		entityManager.flush(); entityManager.clear();
		mvc.perform(get("/api/tnts/t2")).andExpect(jsonPath("$.detonada").value(false));
	}

	private void crearEntidad(String id, String tipo, int salud) throws Exception {
		mvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"" + id + "\",\"tipo\":\"" + tipo + "\",\"saludMaxima\":" + salud + "}"))
				.andExpect(status().isCreated());
	}
}
