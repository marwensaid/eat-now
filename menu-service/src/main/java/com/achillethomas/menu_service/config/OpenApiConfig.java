package com.achillethomas.menu_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI menuServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Menu Service API")
                        .description("API de gestion du catalogue des plats pour EatNow")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EatNow Team")
                                .email("contact@eatnow.com")));
    }
}
