package anthony1.com.order_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Serveur local");

        Server k8sServer = new Server();
        k8sServer.setUrl("http://order-service:8082");
        k8sServer.setDescription("Serveur Kubernetes");

        Contact contact = new Contact();
        contact.setName("Eat Now Team");
        contact.setEmail("contact@eatnow.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Order Service API")
                .version("1.0.0")
                .description("API de gestion des commandes pour l'application Eat Now. "
                        + "Ce service permet de créer, consulter et gérer les commandes clients.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, k8sServer));
    }
}
