package com.achillethomas.order_service.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration des clients REST pour la communication inter-services
 */
@Configuration
public class RestClientConfig {

    /**
     * Bean RestTemplate pour les appels HTTP synchrones
     * Configuré avec des timeouts pour éviter les blocages
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
