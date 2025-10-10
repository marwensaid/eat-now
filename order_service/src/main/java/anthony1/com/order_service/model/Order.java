package anthony1.com.order_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modèle représentant une commande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private String userId;                      // Identifiant de l'utilisateur
    private List<OrderItem> items;              // Liste des articles commandés
    private Double totalAmount;                 // Montant total de la commande
    private OrderStatus status;                 // Statut de la commande
    private String deliveryAddress;             // Adresse de livraison
    private String customerName;                // Nom du client
    private String customerPhone;               // Téléphone du client
    private String notes;                       // Notes supplémentaires
    private LocalDateTime createdAt;            // Date de création
    private LocalDateTime updatedAt;            // Date de dernière mise à jour

    /**
     * Initialise la liste des items si null
     */
    public List<OrderItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    /**
     * Calcule le montant total de la commande
     */
    public void calculateTotalAmount() {
        this.totalAmount = getItems().stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
}
