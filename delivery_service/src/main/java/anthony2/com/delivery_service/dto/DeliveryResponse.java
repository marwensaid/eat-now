package anthony2.com.delivery_service.dto;

import anthony2.com.delivery_service.model.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour retourner une livraison
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {

    private Long id;
    private Long orderId;
    private DeliveryStatus status;
    private String deliveryAddress;

    // Informations livreur
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;

    // Informations client
    private String customerName;
    private String customerPhone;

    // Localisation
    private String currentLocation;

    // Temps
    private Integer estimatedDeliveryTime;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveredAt;

    // Notes
    private String notes;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
