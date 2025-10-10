package anthony2.com.delivery_service.model;

/**
 * Énumération des statuts de livraison
 */
public enum DeliveryStatus {
    PENDING, // En attente d'assignation
    ASSIGNED, // Livreur assigné
    PICKED_UP, // Commande récupérée par le livreur
    IN_TRANSIT, // En cours de livraison
    DELIVERED, // Livrée
    CANCELLED, // Annulée
    FAILED          // Échec de livraison
}
