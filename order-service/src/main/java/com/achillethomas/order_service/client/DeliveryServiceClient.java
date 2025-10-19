package com.achillethomas.order_service.client;

import com.achillethomas.order_service.dto.DeliveryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Client pour communiquer avec le delivery-service
 * Utilise Resilience4j pour la résilience (Circuit Breaker, Retry, Time Limiter)
 */
@Component
public class DeliveryServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceClient.class);

    private final RestTemplate restTemplate;
    private final String deliveryServiceUrl;

    public DeliveryServiceClient(RestTemplate restTemplate,
                                @Value("${delivery.service.url}") String deliveryServiceUrl) {
        this.restTemplate = restTemplate;
        this.deliveryServiceUrl = deliveryServiceUrl;
    }

    /**
     * Crée une livraison pour une commande
     * Applique Circuit Breaker, Retry et Time Limiter pour la résilience
     * 
     * @param orderId ID de la commande
     * @param deliveryAddress Adresse de livraison
     * @param customerName Nom du client
     * @return DeliveryResponse avec les détails de la livraison créée
     */
    @CircuitBreaker(name = "deliveryService", fallbackMethod = "createDeliveryFallback")
    @Retry(name = "deliveryService")
    @TimeLimiter(name = "deliveryService")
    public CompletableFuture<DeliveryResponse> createDelivery(String orderId, String deliveryAddress, String customerName) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Creating delivery for order {} with delivery-service", orderId);
            
            try {
                String url = deliveryServiceUrl + "/api/delivery/deliveries";
                
                // Préparer la requête
                Map<String, Object> request = new HashMap<>();
                request.put("orderId", Long.parseLong(orderId));
                request.put("deliveryAddress", deliveryAddress);
                request.put("customerName", customerName);
                request.put("notes", "Auto-created from order-service");
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
                
                // Appeler le delivery-service
                ResponseEntity<DeliveryResponse> response = restTemplate.postForEntity(
                    url, 
                    entity, 
                    DeliveryResponse.class
                );
                
                DeliveryResponse deliveryResponse = response.getBody();
                logger.info("Delivery created successfully: {}", deliveryResponse != null ? deliveryResponse.getId() : "null");
                
                return deliveryResponse;
                
            } catch (Exception e) {
                logger.error("Error calling delivery-service: {}", e.getMessage());
                throw new RuntimeException("Delivery service unavailable", e);
            }
        });
    }

    /**
     * Méthode fallback appelée quand le circuit breaker est ouvert ou en cas d'erreur
     * Log l'erreur et retourne null (la livraison pourra être créée manuellement plus tard)
     */
    private CompletableFuture<DeliveryResponse> createDeliveryFallback(String orderId, String deliveryAddress, 
                                                                       String customerName, Exception e) {
        logger.error("Delivery service fallback triggered for order {}. Reason: {}. " +
                    "Delivery will need to be created manually.", orderId, e.getMessage());
        
        // En cas d'erreur, on retourne null mais on ne bloque pas la création de la commande
        // La livraison pourra être créée manuellement plus tard
        return CompletableFuture.completedFuture(null);
    }
}
