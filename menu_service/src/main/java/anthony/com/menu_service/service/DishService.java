package anthony.com.menu_service.service;

import anthony.com.menu_service.dto.DishRequest;
import anthony.com.menu_service.dto.DishResponse;
import anthony.com.menu_service.exception.DishNotFoundException;
import anthony.com.menu_service.model.Dish;
import anthony.com.menu_service.repository.DishRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des plats
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DishService {

    private final DishRepository dishRepository;

    /**
     * Récupérer tous les plats
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "getAllDishesFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public List<DishResponse> getAllDishes() {
        log.info("Récupération de tous les plats");
        return dishRepository.findAll().stream()
                .map(DishResponse::fromDish)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un plat par son ID
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "getDishByIdFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public DishResponse getDishById(Long id) {
        log.info("Récupération du plat avec l'id : {}", id);
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
        return DishResponse.fromDish(dish);
    }

    /**
     * Récupérer les plats par catégorie
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "getDishesByCategoryFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public List<DishResponse> getDishesByCategory(String category) {
        log.info("Récupération des plats de la catégorie : {}", category);
        return dishRepository.findByCategory(category).stream()
                .map(DishResponse::fromDish)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les plats disponibles
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "getAvailableDishesFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public List<DishResponse> getAvailableDishes(boolean available) {
        log.info("Récupération des plats disponibles : {}", available);
        return dishRepository.findByAvailable(available).stream()
                .map(DishResponse::fromDish)
                .collect(Collectors.toList());
    }

    /**
     * Créer un nouveau plat
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "createDishFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public DishResponse createDish(DishRequest dishRequest) {
        log.info("Création d'un nouveau plat : {}", dishRequest.getName());

        Dish dish = Dish.builder()
                .name(dishRequest.getName())
                .description(dishRequest.getDescription())
                .price(dishRequest.getPrice())
                .category(dishRequest.getCategory())
                .available(dishRequest.getAvailable() != null ? dishRequest.getAvailable() : true)
                .imageUrl(dishRequest.getImageUrl())
                .build();

        Dish savedDish = dishRepository.createDish(dish);
        return DishResponse.fromDish(savedDish);
    }

    /**
     * Mettre à jour un plat
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "updateDishFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public DishResponse updateDish(Long id, DishRequest dishRequest) {
        log.info("Mise à jour du plat avec l'id : {}", id);

        // Vérifier si le plat existe
        if (!dishRepository.existsById(id)) {
            throw new DishNotFoundException(id);
        }

        Dish dish = Dish.builder()
                .name(dishRequest.getName())
                .description(dishRequest.getDescription())
                .price(dishRequest.getPrice())
                .category(dishRequest.getCategory())
                .available(dishRequest.getAvailable() != null ? dishRequest.getAvailable() : true)
                .imageUrl(dishRequest.getImageUrl())
                .build();

        Dish updatedDish = dishRepository.updateDish(id, dish);
        return DishResponse.fromDish(updatedDish);
    }

    /**
     * Supprimer un plat
     */
    @CircuitBreaker(name = "menuService", fallbackMethod = "deleteDishFallback")
    @Retry(name = "menuService")
    @TimeLimiter(name = "menuService")
    public void deleteDish(Long id) {
        log.info("Suppression du plat avec l'id : {}", id);

        if (!dishRepository.existsById(id)) {
            throw new DishNotFoundException(id);
        }

        dishRepository.deleteDish(id);
    }

    /**
     * Obtenir le nombre total de plats
     */
    public long countDishes() {
        return dishRepository.count();
    }

    // ============= Méthodes de fallback =============
    private List<DishResponse> getAllDishesFallback(Exception e) {
        log.error("Fallback : Impossible de récupérer les plats - {}", e.getMessage());
        return List.of();
    }

    private DishResponse getDishByIdFallback(Long id, Exception e) {
        log.error("Fallback : Impossible de récupérer le plat {} - {}", id, e.getMessage());
        throw new DishNotFoundException("Service temporairement indisponible");
    }

    private List<DishResponse> getDishesByCategoryFallback(String category, Exception e) {
        log.error("Fallback : Impossible de récupérer les plats de la catégorie {} - {}", category, e.getMessage());
        return List.of();
    }

    private List<DishResponse> getAvailableDishesFallback(boolean available, Exception e) {
        log.error("Fallback : Impossible de récupérer les plats disponibles - {}", e.getMessage());
        return List.of();
    }

    private DishResponse createDishFallback(DishRequest dishRequest, Exception e) {
        log.error("Fallback : Impossible de créer le plat - {}", e.getMessage());
        throw new RuntimeException("Service temporairement indisponible pour la création de plats");
    }

    private DishResponse updateDishFallback(Long id, DishRequest dishRequest, Exception e) {
        log.error("Fallback : Impossible de mettre à jour le plat {} - {}", id, e.getMessage());
        throw new RuntimeException("Service temporairement indisponible pour la mise à jour de plats");
    }

    private void deleteDishFallback(Long id, Exception e) {
        log.error("Fallback : Impossible de supprimer le plat {} - {}", id, e.getMessage());
        throw new RuntimeException("Service temporairement indisponible pour la suppression de plats");
    }
}
