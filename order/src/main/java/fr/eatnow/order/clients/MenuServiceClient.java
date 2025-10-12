package fr.eatnow.order.clients;

import fr.eatnow.order.dtos.DishDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuServiceClient {


    private final RestTemplate restTemplate;

    @Value("${menu.service.url}")
    private String baseUrl;

    /**
     * Récupère UN plat avec résilience complète
     */
    @CircuitBreaker(name = "menu-service", fallbackMethod = "getDishByIdFallback")
    @Retry(name = "menu-service")
    @TimeLimiter(name = "menu-service")
    public Optional<DishDto> getDishById(String dishId) {
        log.debug("🔗 Appel menu-service pour plat ID: {}", dishId);

        try {
            // CORRECTION : URL correcte /api/menu/{id}
            String url = UriComponentsBuilder
                    .fromUriString(baseUrl)
                    .path("/dishes/{id}")
                    .buildAndExpand(dishId)
                    .toUriString();

            log.debug("📡 URL construite: {}", url);

            DishDto dish = restTemplate.getForObject(url, DishDto.class);

            if (dish != null) {
                log.info("✅ Plat {} récupéré: {}", dishId, dish.getName());
                return Optional.of(dish);
            } else {
                log.debug("❌ Plat {} retourné null par menu-service", dishId);
                return Optional.empty();
            }

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("❌ Plat {} non trouvé (404)", dishId);
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            log.warn("⚠️ Erreur HTTP {} pour plat {}: {}",
                    e.getStatusCode(), dishId, e.getMessage());
            throw e; // Resilience4j va retry
        } catch (Exception e) {
            log.error("💥 Erreur inattendue pour plat {}: {}", dishId, e.getMessage(), e);
            throw e; // Circuit breaker va s'activer
        }
    }

    /**
     * Récupère plusieurs plats avec résilience
     */
    @CircuitBreaker(name = "menu-service", fallbackMethod = "getDishesByIdsFallback")
    @Retry(name = "menu-service")
    @TimeLimiter(name = "menu-service")
    public List<DishDto> getDishesByIds(List<String> dishIds) {
        log.info("🔍 Récupération de {} plats depuis menu-service", dishIds.size());

        // Chaque plat individuel bénéficie de sa propre résilience
        return dishIds.stream()
                .map(this::getDishById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Fallback pour un plat individuel (quand circuit breaker est OPEN)
     */
    public Optional<DishDto> getDishByIdFallback(String dishId, Throwable t) {
        log.error("🔴 CIRCUIT BREAKER OPEN - Plat {} indisponible: {}",
                dishId, getErrorMessage(t));

        // Graceful degradation : retourne vide au lieu de crasher
        return Optional.empty();
    }

    /**
     * Fallback pour plusieurs plats
     */
    public List<DishDto> getDishesByIdsFallback(List<String> dishIds, Throwable t) {
        log.error("🔴 CIRCUIT BREAKER OPEN - Échec récupération {} plats: {}",
                dishIds.size(), getErrorMessage(t));

        // Alternative : retourner des plats "fantômes" avec prix fixe

        return List.of(); // Ou liste vide pour signaler l'erreur
    }

    // Helper pour formater les erreurs de fallback
    private String getErrorMessage(Throwable t) {
        if (t.getCause() != null) {
            return t.getCause().getMessage();
        }
        return t.getMessage();
    }

}

