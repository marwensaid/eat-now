package anthony.com.menu_service.config;

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
    public OpenAPI menuServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8081");
        localServer.setDescription("Serveur local");

        Server k8sServer = new Server();
        k8sServer.setUrl("http://menu-service:8081");
        k8sServer.setDescription("Serveur Kubernetes");

        Contact contact = new Contact();
        contact.setName("Eat Now Team");
        contact.setEmail("contact@eatnow.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Menu Service API")
                .version("1.0.0")
                .description("API de gestion du catalogue des plats pour l'application Eat Now. "
                        + "Ce service permet de lister, consulter, ajouter, modifier et supprimer des plats.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, k8sServer));
    }
}
