package fr.eatnow.delivery.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Requête pour créer une livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeliveryRequest {

    @NotBlank(message = "ID de la commande obligatoire")
    @Schema(description = "ID de la commande à livrer", example = "uuid-command-123")
    private String orderId;

    @NotBlank(message = "Adresse de livraison obligatoire")
    @Schema(description = "Adresse de livraison", example = "123 Rue de la Paix, Paris")
    private String deliveryAddress;
}