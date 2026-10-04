package py.edu.uc.lp3.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventario {
	private List<Bloque> items = new ArrayList<>();

	public void agregar(Bloque bloque) {
		items.add(bloque);
	}

	public void remover(Bloque bloque) {
		items.remove(bloque);
	}

	public List<Bloque> getItems() {
		return Collections.unmodifiableList(items);
	}

	public void setItems(List<Bloque> items) {
		this.items = new ArrayList<>(items);
	}
}
