package anthony1.com.order_service.exception;

/**
 * Exception levée lorsqu'une commande n'est pas trouvée
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Commande non trouvée avec l'id : " + id);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
