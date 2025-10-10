package anthony2.com.delivery_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité Livraison
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    private Long id;
    private Long orderId;                  // Référence vers la commande (order_service)
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

    // Temps estimé et réel
    private Integer estimatedDeliveryTime;  // en minutes
    private LocalDateTime pickupTime;        // Heure de récupération
    private LocalDateTime deliveredAt;       // Heure de livraison

    // Notes
    private String notes;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
