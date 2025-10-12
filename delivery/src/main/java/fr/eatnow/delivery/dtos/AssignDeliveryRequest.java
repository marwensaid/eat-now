package fr.eatnow.delivery.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Requête pour assigner un livreur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignDeliveryRequest {

    @NotBlank(message = "Nom du livreur obligatoire")
    @Schema(description = "Nom du livreur", example = "Jean Dupont")
    private String deliveryPerson;

    @NotNull(message = "Estimation d'arrivée obligatoire")
    @Schema(description = "Estimation du temps d'arrivée")
    private String estimatedArrival;  // Format ISO datetime string
}