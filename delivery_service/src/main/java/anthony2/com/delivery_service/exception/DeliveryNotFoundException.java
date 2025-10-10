package anthony2.com.delivery_service.exception;

/**
 * Exception levée quand une livraison n'est pas trouvée
 */
public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(Long id) {
        super("Livraison non trouvée avec l'ID: " + id);
    }
}
