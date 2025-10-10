package anthony2.com.delivery_service.dto;

import anthony2.com.delivery_service.model.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour mettre à jour le statut d'une livraison
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusUpdateRequest {

    @NotNull(message = "Le nouveau statut est obligatoire")
    private DeliveryStatus status;

    private String currentLocation;
}
