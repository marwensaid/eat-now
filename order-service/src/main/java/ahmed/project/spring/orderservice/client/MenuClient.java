package ahmed.project.spring.orderservice.client;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class MenuClient {
    private final WebClient webClient;

    public MenuClient(WebClient.Builder webClientBuilder,
                      @Value("${menu.service.url:http://menu-service:8081}") String menuServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(menuServiceUrl).build();
    }

    public Map<String, Object> getDishById(Long dishId) {
        return webClient.get()
                .uri("/api/dishes/{id}", dishId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
