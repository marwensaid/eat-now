package anthony2.com.delivery_service.exception;

/**
 * Exception levée quand le statut de livraison est invalide
 */
public class InvalidDeliveryStatusException extends RuntimeException {

    public InvalidDeliveryStatusException(String message) {
        super(message);
    }
}
