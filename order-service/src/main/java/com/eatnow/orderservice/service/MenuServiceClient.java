package com.eatnow.orderservice.service;

import com.eatnow.orderservice.model.Plat;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
public class MenuServiceClient {

    private static final Logger log = LoggerFactory.getLogger(MenuServiceClient.class);
    private final RestTemplate restTemplate;
    private final String menuServiceUrl;

    public MenuServiceClient(RestTemplate restTemplate, @Value("${menu-service.url}") String menuServiceUrl) {
        this.restTemplate = restTemplate;
        this.menuServiceUrl = menuServiceUrl;
    }

    @CircuitBreaker(name = "menu-service", fallbackMethod = "getPlatFallback")
    @Retry(name = "menu-service")
    public Optional<Plat> getPlat(UUID platId) {
        String url = menuServiceUrl + "/plats/" + platId;
        log.info("Appel de menu-service : {}", url);
        Plat plat = restTemplate.getForObject(url, Plat.class);
        return Optional.ofNullable(plat);
    }

    // Méthode de fallback pour le Circuit Breaker
    public Optional<Plat> getPlatFallback(UUID platId, Throwable t) {
        log.error("Fallback pour getPlat. Impossible de contacter menu-service. ID: {}, Erreur: {}", platId, t.getMessage());
        return Optional.empty(); // Retourne un plat vide en cas d'échec
    }
}
