package py.edu.uc.lp3.rest.dto;

import java.util.Map;

public record ObjetoExternoResponse(String id, String name, Map<String, Object> data) {
}
