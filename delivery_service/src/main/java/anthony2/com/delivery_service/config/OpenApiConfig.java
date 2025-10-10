package anthony2.com.delivery_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI deliveryServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("Serveur local");

        Contact contact = new Contact();
        contact.setName("Eat Now Team");
        contact.setEmail("contact@eat-now.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Delivery Service API - Eat Now")
                .version("1.0.0")
                .description("API de gestion des livraisons pour l'application Eat Now. "
                        + "Ce service permet de créer des livraisons, assigner des livreurs, "
                        + "suivre le statut des livraisons et consulter l'historique.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
