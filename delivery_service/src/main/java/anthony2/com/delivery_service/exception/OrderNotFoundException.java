package anthony2.com.delivery_service.exception;

/**
 * Exception levée quand une commande n'est pas trouvée
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Commande non trouvée avec l'ID: " + orderId);
    }
}
