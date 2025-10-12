package fr.eatnow.delivery.clients;

import fr.eatnow.delivery.models.OrderInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderServiceClient {

    private final RestTemplate restTemplate;

    @Value("${order.service.url}")
    private String baseUrl;

    /**
     * Récupère une commande pour livraison avec résilience
     */
    @CircuitBreaker(name = "order-service", fallbackMethod = "getOrderByIdFallback")
    @Retry(name = "order-service")
    @TimeLimiter(name = "order-service")
    public Optional<OrderInfo> getOrderForDelivery(String orderId) {
        log.debug("🔗 Appel order-service pour commande ID: {}", orderId);

        try {
            String url = UriComponentsBuilder
                    .fromUriString(baseUrl)
                    .path("/{orderId}")
                    .buildAndExpand(orderId)
                    .toUriString();

            log.debug("📡 URL construite: {}", url);

            OrderInfo order = restTemplate.getForObject(url, OrderInfo.class);

            if (order != null) {
                log.info("✅ Commande {} récupérée pour livraison", orderId);
                return Optional.of(order);
            } else {
                log.debug("❌ Commande {} retournée null par order-service", orderId);
                return Optional.empty();
            }

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("❌ Commande {} non trouvée (404)", orderId);
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            log.warn("⚠️ Erreur HTTP {} pour commande {}: {}",
                    e.getStatusCode(), orderId, e.getMessage());
            throw e; // Resilience4j va retry
        } catch (Exception e) {
            log.error("💥 Erreur inattendue pour commande {}: {}", orderId, e.getMessage(), e);
            throw e; // Circuit breaker s'activera
        }
    }

    /**
     * Récupère les commandes prêtes pour livraison (statut READY)
     */
    @CircuitBreaker(name = "order-service", fallbackMethod = "getReadyOrdersFallback")
    @Retry(name = "order-service")
    @TimeLimiter(name = "order-service")
    public List<OrderInfo> getReadyOrdersForDelivery() {
        log.info("🔍 Récupération des commandes prêtes pour livraison depuis order-service");

        try {
            String url = UriComponentsBuilder
                    .fromUriString(baseUrl)
                    .path("/ready-for-delivery")  // Endpoint spécial ou filtre côté client
                    .queryParam("status", "READY")
                    .build()
                    .toUriString();

            OrderInfo[] orders = restTemplate.getForObject(url, OrderInfo[].class);
            List<OrderInfo> readyOrders = orders != null ? List.of(orders) : List.of();

            log.info("✅ {} commandes prêtes récupérées", readyOrders.size());
            return readyOrders;

        } catch (Exception e) {
            log.error("💥 Erreur récupération commandes prêtes: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // FALLBACK METHODS
    // ═══════════════════════════════════════════════════════════════════════════════

    public Optional<OrderInfo> getOrderByIdFallback(String orderId, Throwable t) {
        log.warn("🔴 CIRCUIT BREAKER OPEN - Commande {} indisponible: {}",
                orderId, getErrorMessage(t));
        return Optional.empty();  // Graceful degradation
    }

    public List<OrderInfo> getReadyOrdersFallback(Throwable t) {
        log.warn("🔴 CIRCUIT BREAKER OPEN - Impossible de récupérer les commandes prêtes: {}",
                getErrorMessage(t));
        return List.of();  // Liste vide
    }

    private String getErrorMessage(Throwable t) {
        if (t.getCause() != null) {
            return t.getCause().getMessage();
        }
        return t.getMessage();
    }
}