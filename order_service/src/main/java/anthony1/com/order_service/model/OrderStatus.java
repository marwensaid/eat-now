package anthony1.com.order_service.model;

/**
 * Énumération des statuts possibles pour une commande
 */
public enum OrderStatus {
    CREATED("Créée"),
    CONFIRMED("Confirmée"),
    PREPARING("En préparation"),
    READY("Prête"),
    IN_DELIVERY("En livraison"),
    DELIVERED("Livrée"),
    CANCELLED("Annulée");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
