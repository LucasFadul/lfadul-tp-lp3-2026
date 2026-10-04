package py.edu.uc.lp3;

import py.edu.uc.lp3.domain.*;

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

@SpringBootTest
@AutoConfigureMockMvc
class MinecraftApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
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
	void entidadMantieneSaludDentroDeRangosValidos() {
		Jugador jugador = new Jugador("Steve", 20);

		jugador.setSalud(50);
		assertThat(jugador.getSalud()).isEqualTo(20);

		jugador.recibirDano(25);
		assertThat(jugador.getSalud()).isZero();
		assertThat(jugador.estaVivo()).isFalse();
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
	void constructoresYAccionSobrecargadosConservanReglas() {
		Jugador jugador = new Jugador("Alex");
		Esqueleto esqueleto = new Esqueleto("Skeleton");
		assertThat(new Cerdo("Pig").getSalud()).isEqualTo(10);
		esqueleto.atacar(jugador);
		assertThat(jugador.getSalud()).isEqualTo(16);
		esqueleto.atacar(jugador, 17);
		assertThat(jugador.getSalud()).isEqualTo(16);
		esqueleto.atacar(jugador, 16);
		assertThat(jugador.getSalud()).isEqualTo(12);
		assertThatThrownBy(() -> esqueleto.atacar(jugador, -1)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> jugador.recibirDano(-1)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> jugador.curar(-1)).isInstanceOf(IllegalArgumentException.class);
		jugador.curar(Integer.MAX_VALUE);
		assertThat(jugador.getSalud()).isEqualTo(20);
		assertThatThrownBy(() -> new Jugador("Alex", 0)).isInstanceOf(IllegalArgumentException.class);
	}
}
