package anthony2.com.delivery_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour assigner un livreur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignDeliveryPersonRequest {

    @NotNull(message = "L'ID du livreur est obligatoire")
    @Positive(message = "L'ID du livreur doit être positif")
    private Long deliveryPersonId;

    @NotNull(message = "Le nom du livreur est obligatoire")
    private String deliveryPersonName;

    @NotNull(message = "Le téléphone du livreur est obligatoire")
    private String deliveryPersonPhone;
}
