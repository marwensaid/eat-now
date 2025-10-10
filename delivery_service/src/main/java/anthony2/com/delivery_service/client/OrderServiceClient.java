package anthony2.com.delivery_service.client;

import anthony2.com.delivery_service.dto.OrderDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client pour communiquer avec le Order Service
 */
@Slf4j
@Component
public class OrderServiceClient {

    private final WebClient webClient;

    public OrderServiceClient(WebClient.Builder webClientBuilder,
            @Value("${order.service.url}") String orderServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(orderServiceUrl)
                .build();
    }

    /**
     * Récupérer une commande par son ID
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public Mono<OrderDTO> getOrder(Long orderId) {
        log.info("Appel vers order_service pour récupérer la commande {}", orderId);

        return webClient.get()
                .uri("/api/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(order -> log.info("Commande {} récupérée avec succès", orderId))
                .doOnError(error -> log.error("Erreur lors de la récupération de la commande {}: {}",
                orderId, error.getMessage()));
    }

    /**
     * Vérifier si une commande existe et est valide
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "isOrderValidFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public Mono<Boolean> isOrderValid(Long orderId) {
        return getOrder(orderId)
                .map(order -> order != null && order.getId() != null)
                .onErrorReturn(false);
    }

    /**
     * Fallback pour getOrder
     */
    private Mono<OrderDTO> getOrderFallback(Long orderId, Throwable throwable) {
        log.error("Fallback activé pour getOrder avec orderId={}: {}", orderId, throwable.getMessage());

        return Mono.just(OrderDTO.builder()
                .id(orderId)
                .status("UNKNOWN")
                .deliveryAddress("Adresse indisponible")
                .customerName("Client indisponible")
                .customerPhone("Non disponible")
                .build());
    }

    /**
     * Fallback pour isOrderValid
     */
    private Mono<Boolean> isOrderValidFallback(Long orderId, Throwable throwable) {
        log.error("Fallback activé pour isOrderValid avec orderId={}: {}", orderId, throwable.getMessage());
        return Mono.just(false);
    }
}
