package com.eatnow.orderservice.service;

import com.eatnow.orderservice.model.Livraison;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class DeliveryServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceClient.class);
    private final RestTemplate restTemplate;
    private final String deliveryServiceUrl;

    public DeliveryServiceClient(RestTemplate restTemplate, @Value("${delivery-service.url}") String deliveryServiceUrl) {
        this.restTemplate = restTemplate;
        this.deliveryServiceUrl = deliveryServiceUrl;
    }

    @CircuitBreaker(name = "delivery-service", fallbackMethod = "createLivraisonFallback")
    @Retry(name = "delivery-service")
    public void createLivraison(UUID commandeId) {
        String url = deliveryServiceUrl + "/livraisons";
        log.info("Appel de delivery-service pour créer une livraison pour la commande : {}", commandeId);

        // Le corps de la requête attend un objet avec un champ "commandeId"
        Map<String, UUID> requestBody = Map.of("commandeId", commandeId);

        Livraison livraison = restTemplate.postForObject(url, requestBody, Livraison.class);
        log.info("Livraison créée avec succès : {}", livraison);
    }

    // Méthode de fallback pour le Circuit Breaker
    public void createLivraisonFallback(UUID commandeId, Throwable t) {
        log.error("Fallback pour createLivraison. Impossible de contacter delivery-service. Commande ID: {}, Erreur: {}", commandeId, t.getMessage());
        // Dans un vrai projet, on pourrait mettre la demande en file d''attente pour un traitement ultérieur.
    }
}
