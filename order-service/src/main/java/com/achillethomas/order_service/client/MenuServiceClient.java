package com.achillethomas.order_service.client;

import com.achillethomas.order_service.dto.DishValidationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Client pour communiquer avec le menu-service
 * Utilise Resilience4j pour la résilience (Circuit Breaker, Retry, Time Limiter)
 */
@Component
public class MenuServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceClient.class);

    private final RestTemplate restTemplate;
    private final String menuServiceUrl;

    public MenuServiceClient(RestTemplate restTemplate,
                            @Value("${menu.service.url}") String menuServiceUrl) {
        this.restTemplate = restTemplate;
        this.menuServiceUrl = menuServiceUrl;
    }

    /**
     * Valide que les plats existent et sont disponibles dans le menu-service
     * Applique Circuit Breaker, Retry et Time Limiter pour la résilience
     * 
     * @param dishIds Liste des IDs de plats à valider
     * @return DishValidationResponse avec le résultat de la validation
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "validateDishesFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public CompletableFuture<DishValidationResponse> validateDishes(List<String> dishIds) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Validating dishes with menu-service: {}", dishIds);
            
            try {
                // Vérifier chaque plat individuellement
                List<String> invalidDishes = new ArrayList<>();
                
                for (String dishId : dishIds) {
                    String url = menuServiceUrl + "/api/menu/dishes/" + dishId;
                    try {
                        @SuppressWarnings("rawtypes")
                        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                        
                        if (!response.getStatusCode().is2xxSuccessful()) {
                            invalidDishes.add(dishId);
                        } else {
                            // Vérifier si le plat est disponible
                            @SuppressWarnings("unchecked")
                            Map<String, Object> dish = response.getBody();
                            if (dish != null && dish.containsKey("available")) {
                                Boolean available = (Boolean) dish.get("available");
                                if (!available) {
                                    invalidDishes.add(dishId);
                                    logger.warn("Dish {} is not available", dishId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error validating dish {}: {}", dishId, e.getMessage());
                        invalidDishes.add(dishId);
                    }
                }
                
                boolean isValid = invalidDishes.isEmpty();
                String message = isValid ? "All dishes are valid and available" 
                                        : "Some dishes are invalid or unavailable: " + invalidDishes;
                
                logger.info("Dish validation result: valid={}, invalidDishes={}", isValid, invalidDishes);
                return new DishValidationResponse(isValid, invalidDishes, message);
                
            } catch (Exception e) {
                logger.error("Error calling menu-service: {}", e.getMessage());
                throw new RuntimeException("Menu service unavailable", e);
            }
        });
    }

    /**
     * Méthode fallback appelée quand le circuit breaker est ouvert ou en cas d'erreur
     * Retourne une réponse par défaut permettant de continuer le traitement
     */
    private CompletableFuture<DishValidationResponse> validateDishesFallback(List<String> dishIds, Exception e) {
        logger.warn("Menu service fallback triggered for dishes: {}. Reason: {}", dishIds, e.getMessage());
        
        // En cas d'erreur, on refuse la validation pour éviter de créer des commandes invalides
        DishValidationResponse response = new DishValidationResponse(
            false,
            dishIds,
            "Menu service is currently unavailable. Please try again later."
        );
        
        return CompletableFuture.completedFuture(response);
    }
}
