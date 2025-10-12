package fr.eatnow.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "Requête pour créer une commande")
@Data
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    @NotBlank(message = "L'email du client est obligatoire")
    @Email(message = "Email invalide")
    @Schema(description = "Email du client", example = "client@example.com")
    private String customerEmail;

    @NotEmpty(message = "La commande doit contenir au moins un plat")
    @Size(max = 50, message = "Maximum 50 plats par commande")
    @Schema(description = "IDs des plats commandés")
    private List<String> dishIds;
}
