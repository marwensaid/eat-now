package anthony1.com.order_service.exception;

/**
 * Exception levée lorsqu'un plat n'est pas trouvé dans le menu_service
 */
public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(Long dishId) {
        super("Plat non trouvé avec l'id : " + dishId);
    }

    public DishNotFoundException(String message) {
        super(message);
    }
}
