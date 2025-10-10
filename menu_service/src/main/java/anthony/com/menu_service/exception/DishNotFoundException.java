package anthony.com.menu_service.exception;

/**
 * Exception levée lorsqu'un plat n'est pas trouvé
 */
public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(Long id) {
        super("Plat non trouvé avec l'id : " + id);
    }

    public DishNotFoundException(String message) {
        super(message);
    }
}
