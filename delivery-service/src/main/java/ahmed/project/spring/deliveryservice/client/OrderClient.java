package ahmed.project.spring.deliveryservice.client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Component
public class OrderClient {

    private final WebClient webClient;

    public OrderClient(WebClient.Builder webClientBuilder,
                       @Value("${order.service.url:http://order-service:8082}") String orderServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(orderServiceUrl).build();
    }

    public Map<String, Object> getOrderById(Long orderId) {
        return webClient.get()
                .uri("/api/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public void updateOrderStatus(Long orderId, String status) {
        webClient.patch()
                .uri("/api/orders/{id}/status?status={status}", orderId, status)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
