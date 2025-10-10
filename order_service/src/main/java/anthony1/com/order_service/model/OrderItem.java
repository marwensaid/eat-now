package anthony1.com.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modèle représentant un article dans une commande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long dishId;          // Référence au plat dans menu_service
    private String dishName;      // Nom du plat (copié depuis menu_service)
    private Double dishPrice;     // Prix unitaire du plat
    private Integer quantity;     // Quantité commandée

    /**
     * Calcule le prix total de cet article
     */
    public Double getTotalPrice() {
        return dishPrice * quantity;
    }
}
