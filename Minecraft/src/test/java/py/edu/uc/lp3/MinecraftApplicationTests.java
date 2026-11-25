package py.edu.uc.lp3;

import py.edu.uc.lp3.domain.*;
import py.edu.uc.lp3.service.ReglasCombateService;
import py.edu.uc.lp3.service.ReglasExplosionService;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:applicationtests", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class MinecraftApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ReglasCombateService combate;

	@Test
	void contextLoads() {
	}


	@Test
	void openApiDocumentaTodosLosEndpointsYSwaggerEstaDisponible() throws Exception {
		var docs = mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("API Minecraft LP3"));
		String[][] operaciones = {
				{"/", "get"}, {"/jugador", "get"}, {"/entidades", "get"},
				{"/explosivos", "get"}, {"/ui-capables", "get"},
				{"/api/objetos-externos", "get"}, {"/api/entidades", "get"},
				{"/api/entidades", "post"}, {"/api/entidades/{id}", "get"},
				{"/api/entidades/{id}", "put"}, {"/api/entidades/{id}", "delete"},
				{"/api/entidades/{id}/explosiones", "post"}, {"/api/entidades/{id}/explosiones/validar", "post"}, {"/api/tnts", "post"},
				{"/api/tnts/{id}", "get"}, {"/api/explosiones", "post"},
				{"/api/explosiones/{id}", "get"}
		};
		for (String[] operacion : operaciones) {
			docs.andExpect(jsonPath("$.paths['" + operacion[0] + "']." + operacion[1] + ".summary").isNotEmpty());
		}
		docs.andExpect(jsonPath("$.paths['/api/explosiones'].post.description").value(org.hamcrest.Matchers.containsString("solo puede detonarse una vez")))
				.andExpect(jsonPath("$.paths['/api/explosiones'].post.responses['409']").exists())
				.andExpect(jsonPath("$.paths['/api/entidades/{id}'].get.responses['200']").exists())
				.andExpect(jsonPath("$.paths['/api/entidades/{id}'].get.responses['404']").exists())
				.andExpect(jsonPath("$.paths['/api/objetos-externos'].get.responses['200']").exists())
				.andExpect(jsonPath("$.paths['/api/entidades'].post.responses['201']").exists())
				.andExpect(jsonPath("$.paths['/api/entidades/{id}'].delete.responses['204']").exists());
		mockMvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
		mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
	}

	@Test
	void jugadorEndpointDevuelveJsonConComportamiento() throws Exception {
		mockMvc.perform(get("/jugador")
						.param("id", "Alex")
						.param("saludMaxima", "30")
						.param("experiencia", "10")
						.param("x", "1")
						.param("y", "2")
						.param("z", "3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("Alex"))
				.andExpect(jsonPath("$.salud").value(30))
				.andExpect(jsonPath("$.experiencia").value(10))
				.andExpect(jsonPath("$.posicion.x").value(1.0))
				.andExpect(jsonPath("$.comportamiento").value("Explora, construye e interactua con el mundo"));
	}

	@Test
	void entidadesEndpointExponeDosHijasComoTipoPadre() throws Exception {
		mockMvc.perform(get("/entidades"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("Skeleton"))
				.andExpect(jsonPath("$[0].comportamiento").value("Mantiene distancia y ataca con flechas"))
				.andExpect(jsonPath("$[1].id").value("Pig"))
				.andExpect(jsonPath("$[1].comportamiento").value("Deambula pacificamente por el terreno"));
	}

	@Test
	void servicioMantieneSaludDentroDeRangosValidos() {
		Jugador jugador = combate.crearJugador("Steve", 20, 0, new Vector());
		combate.recibirDano(jugador, 25);
		assertThat(jugador.getSalud()).isZero();
		combate.curar(jugador, Integer.MAX_VALUE);
		assertThat(jugador.getSalud()).isEqualTo(20);
	}

	@Test
	void inventarioNoExponeListaMutableInterna() {
		Inventario inventario = new Inventario();
		inventario.agregar(new Bloque("piedra"));

		assertThatThrownBy(() -> inventario.getItems().add(new Bloque("tierra")))
				.isInstanceOf(UnsupportedOperationException.class);
		assertThat(inventario.getItems()).hasSize(1);
	}

	@Test
	void indexYJugadorPorDefecto() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
		mockMvc.perform(get("/jugador")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("Steve"))
				.andExpect(jsonPath("$.salud").value(20));
	}

	@Test
	void urlInvalidaInformaErrorDelDominio() throws Exception {
		for (String salud : new String[]{"0", "-1"}) {
			mockMvc.perform(get("/jugador").param("saludMaxima", salud))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.error").value("La salud maxima debe ser positiva"));
		}
		mockMvc.perform(get("/jugador").param("experiencia", "-1"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/jugador").param("id", " "))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/jugador").param("x", "NaN"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void servicioDeCombateValidaYAplicaElAlcance() {
		Jugador jugador = combate.crearJugador("Alex", 20, 0, new Vector());
		assertThat(new Cerdo("Pig").getSalud()).isEqualTo(10);
		combate.atacarConEsqueleto(jugador);
		assertThat(jugador.getSalud()).isEqualTo(16);
		combate.atacarConEsqueleto(jugador, 17);
		assertThat(jugador.getSalud()).isEqualTo(16);
		combate.atacarConEsqueleto(jugador, 16);
		assertThat(jugador.getSalud()).isEqualTo(12);
		assertThatThrownBy(() -> combate.atacarConEsqueleto(jugador, -1)).isInstanceOf(EntidadInvalidaException.class);
		assertThatThrownBy(() -> combate.recibirDano(jugador, -1)).isInstanceOf(EntidadInvalidaException.class);
		assertThatThrownBy(() -> combate.curar(jugador, -1)).isInstanceOf(EntidadInvalidaException.class);
		assertThatThrownBy(() -> combate.crearJugador("Alex", 0, 0, new Vector())).isInstanceOf(EntidadInvalidaException.class);
	}

	@Test
	void explosivosEndpointUsaInterfazEntreFamiliasDistintas() throws Exception {
		mockMvc.perform(get("/explosivos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].tipo").value("Creeper"))
				.andExpect(jsonPath("$[0].mensaje").value("El creeper se acerca en silencio y explota"))
				.andExpect(jsonPath("$[1].tipo").value("TNT"))
				.andExpect(jsonPath("$[1].mensaje").value("Explota y afecta los bloques cercanos"));
	}

	@Test
	void creeperReemplazaDefaultYTntLoConserva() {
		Explosivo creeper = new Creeper("Creeper", 20);
		Explosivo tnt = new Tnt();

		assertThat(creeper.explotar()).isEqualTo("El creeper se acerca en silencio y explota");
		assertThat(tnt.explotar()).isEqualTo("Explota y afecta los bloques cercanos");
	}

	@Test
	void uiCapablesEndpointUsaInterfazEntreFamiliasDistintas() throws Exception {
		mockMvc.perform(get("/ui-capables"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].tipo").value("Creeper"))
				.andExpect(jsonPath("$[0].imagen").value("creeper.png"))
				.andExpect(jsonPath("$[0].posicion.y").value(64.0))
				.andExpect(jsonPath("$[0].sonido").value("creeper.ogg"))
				.andExpect(jsonPath("$[0].velocidad").value(0.25))
				.andExpect(jsonPath("$[0].mensaje").value("Creeper animado en (4.0, 64.0, -2.0) con sonido creeper.ogg"))
				.andExpect(jsonPath("$[1].tipo").value("TNT"))
				.andExpect(jsonPath("$[1].imagen").value("tnt.png"))
				.andExpect(jsonPath("$[1].posicion.x").value(8.0))
				.andExpect(jsonPath("$[1].sonido").value("tnt.ogg"))
				.andExpect(jsonPath("$[1].velocidad").value(0.0));
	}

	@Test
	void tntUsaMensajeDefaultDeUICapable() {
		UICapable tnt = new Tnt(new Vector(1, 2, 3));

		assertThat(tnt.mostrarEnUI())
				.isEqualTo("Muestra tnt.png en (1.0, 2.0, 3.0) con sonido tnt.ogg y velocidad 0.0");
		assertThatThrownBy(() -> new ReglasExplosionService().validarPosicion(null)).isInstanceOf(EntidadInvalidaException.class);
	}
}
