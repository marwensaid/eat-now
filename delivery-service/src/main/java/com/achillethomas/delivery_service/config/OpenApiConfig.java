package com.achillethomas.delivery_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Service API")
                        .description("API de gestion des livraisons pour l'application EatNow")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EatNow Team")
                                .email("contact@eatnow.com")));
    }
}
