package anthony1.com.order_service.exception;

/**
 * Exception levée lors d'une transition de statut invalide
 */
public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
