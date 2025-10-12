package fr.eatnow.delivery.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Modèle d'une livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class Delivery {

    @NotNull(message = "L'ID ne peut pas être null")
    @NotBlank
    private String id;

    @NotNull(message = "ID de la commande est obligatoire")
    @NotBlank
    @Schema(description = "ID de la commande associée", example = "uuid-command-123")
    private String orderId;

    @NotNull
    @Schema(description = "Statut de la livraison", example = "PENDING")
    private DeliveryStatus status;

    @NotBlank(message = "Le livreur doit être assigné")
    @Schema(description = "Nom du livreur", example = "Jean Dupont")
    private String deliveryPerson;

    @NotNull(message = "L'adresse de livraison est obligatoire")
    @Schema(description = "Adresse de livraison", example = "123 Rue de la Paix, Paris")
    private String deliveryAddress;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Schema(description = "Estimation du temps d'arrivée", example = "2024-01-15T11:30:00")
    private LocalDateTime estimatedArrival;

    public enum DeliveryStatus {
        PENDING, ASSIGNED, IN_TRANSIT, DELIVERED, FAILED, CANCELLED
    }
}