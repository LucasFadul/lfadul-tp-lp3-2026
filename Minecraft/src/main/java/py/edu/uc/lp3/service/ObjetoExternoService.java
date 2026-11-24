package py.edu.uc.lp3.service;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.rest.dto.ObjetoExternoResponse;
import reactor.core.publisher.Mono;

@Service
public class ObjetoExternoService {
    private final WebClient webClient;

    @Autowired
    public ObjetoExternoService(@Value("${api.objetos.base-url:https://api.restful-api.dev}") String baseUrl) {
        this(WebClient.builder().baseUrl(baseUrl).build());
    }

    ObjetoExternoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<ObjetoExternoResponse>> listar() {
        return webClient.get()
                .uri("/objects")
                .retrieve()
                .bodyToFlux(ObjetoExternoResponse.class)
                .collectList()
                .timeout(Duration.ofSeconds(10));
    }
}
