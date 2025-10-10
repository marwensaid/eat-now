package anthony2.com.delivery_service.dto;

import anthony2.com.delivery_service.model.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer une livraison
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    @NotNull(message = "L'ID de la commande est obligatoire")
    @Positive(message = "L'ID de la commande doit être positif")
    private Long orderId;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String deliveryAddress;

    @NotBlank(message = "Le nom du client est obligatoire")
    private String customerName;

    @NotBlank(message = "Le téléphone du client est obligatoire")
    private String customerPhone;

    @Positive(message = "Le temps de livraison estimé doit être positif")
    private Integer estimatedDeliveryTime; // en minutes

    private String notes;
}
