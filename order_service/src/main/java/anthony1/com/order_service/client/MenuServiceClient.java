package anthony1.com.order_service.client;

import anthony1.com.order_service.dto.DishDTO;
import anthony1.com.order_service.exception.DishNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client pour communiquer avec le menu_service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${menu.service.url}")
    private String menuServiceUrl;

    /**
     * Récupérer un plat depuis le menu_service
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "getDishFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public Mono<DishDTO> getDish(Long dishId) {
        log.info("Récupération du plat {} depuis menu_service", dishId);

        return webClientBuilder.build()
                .get()
                .uri(menuServiceUrl + "/api/dishes/{id}", dishId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new DishNotFoundException(dishId))
                )
                .bodyToMono(DishDTO.class)
                .doOnSuccess(dish -> log.info("Plat {} récupéré avec succès", dishId))
                .doOnError(error -> log.error("Erreur lors de la récupération du plat {}: {}",
                dishId, error.getMessage()));
    }

    /**
     * Vérifier la disponibilité d'un plat
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "isDishAvailableFallback")
    @Retry(name = "menuService")
    public Mono<Boolean> isDishAvailable(Long dishId) {
        log.info("Vérification de la disponibilité du plat {}", dishId);

        return getDish(dishId)
                .map(DishDTO::isAvailable)
                .onErrorReturn(false);
    }

    // ============= Méthodes de fallback =============
    private Mono<DishDTO> getDishFallback(Long dishId, Exception e) {
        log.error("Fallback : Impossible de récupérer le plat {} - {}", dishId, e.getMessage());
        return Mono.error(new DishNotFoundException(
                "Service menu temporairement indisponible pour le plat " + dishId));
    }

    private Mono<Boolean> isDishAvailableFallback(Long dishId, Exception e) {
        log.error("Fallback : Impossible de vérifier la disponibilité du plat {} - {}",
                dishId, e.getMessage());
        return Mono.just(false);
    }
}
