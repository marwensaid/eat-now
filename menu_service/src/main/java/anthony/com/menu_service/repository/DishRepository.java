package anthony.com.menu_service.repository;

import anthony.com.menu_service.model.Dish;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository pour le stockage en mémoire des plats
 */
@Repository
public class DishRepository {

    private final Map<Long, Dish> dishes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public DishRepository() {
        // Initialisation avec quelques plats par défaut
        initializeData();
    }

    private void initializeData() {
        createDish(Dish.builder()
                .name("Salade César")
                .description("Salade verte, poulet grillé, croûtons, parmesan")
                .price(9.50)
                .category("ENTREE")
                .available(true)
                .imageUrl("https://example.com/images/salade-cesar.jpg")
                .build());

        createDish(Dish.builder()
                .name("Burger Classique")
                .description("Steak haché, fromage, salade, tomate, oignon, sauce burger")
                .price(12.90)
                .category("PLAT")
                .available(true)
                .imageUrl("https://example.com/images/burger.jpg")
                .build());

        createDish(Dish.builder()
                .name("Pizza Margherita")
                .description("Tomate, mozzarella, basilic")
                .price(11.50)
                .category("PLAT")
                .available(true)
                .imageUrl("https://example.com/images/pizza.jpg")
                .build());

        createDish(Dish.builder()
                .name("Tiramisu")
                .description("Dessert italien au café et mascarpone")
                .price(6.50)
                .category("DESSERT")
                .available(true)
                .imageUrl("https://example.com/images/tiramisu.jpg")
                .build());

        createDish(Dish.builder()
                .name("Coca-Cola")
                .description("Boisson gazeuse - 33cl")
                .price(2.50)
                .category("BOISSON")
                .available(true)
                .imageUrl("https://example.com/images/coca.jpg")
                .build());
    }

    public Dish createDish(Dish dish) {
        Long id = idGenerator.getAndIncrement();
        dish.setId(id);
        dish.setCreatedAt(LocalDateTime.now());
        dish.setUpdatedAt(LocalDateTime.now());
        dishes.put(id, dish);
        return dish;
    }

    public Optional<Dish> findById(Long id) {
        return Optional.ofNullable(dishes.get(id));
    }

    public List<Dish> findAll() {
        return new ArrayList<>(dishes.values());
    }

    public List<Dish> findByCategory(String category) {
        return dishes.values().stream()
                .filter(dish -> dish.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Dish> findByAvailable(boolean available) {
        return dishes.values().stream()
                .filter(dish -> dish.isAvailable() == available)
                .collect(Collectors.toList());
    }

    public Dish updateDish(Long id, Dish updatedDish) {
        Dish existingDish = dishes.get(id);
        if (existingDish != null) {
            updatedDish.setId(id);
            updatedDish.setCreatedAt(existingDish.getCreatedAt());
            updatedDish.setUpdatedAt(LocalDateTime.now());
            dishes.put(id, updatedDish);
            return updatedDish;
        }
        return null;
    }

    public boolean deleteDish(Long id) {
        return dishes.remove(id) != null;
    }

    public long count() {
        return dishes.size();
    }

    public boolean existsById(Long id) {
        return dishes.containsKey(id);
    }
}
