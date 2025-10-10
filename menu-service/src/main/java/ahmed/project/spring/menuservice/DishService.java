package ahmed.project.spring.menuservice;


import ahmed.project.spring.menuservice.models.Dish;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final Map<Long, Dish> dishes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void initializeData() {
        // Données initiales pour les tests
        createDish(new Dish(null, "Pizza Margherita", "Pizza traditionnelle avec tomate et mozzarella", new BigDecimal("12.50"), "PIZZA"));
        createDish(new Dish(null, "Burger Classic", "Burger avec steak, salade, tomate, oignons", new BigDecimal("10.90"), "BURGER"));
        createDish(new Dish(null, "Salade César", "Salade avec poulet grillé, parmesan, croûtons", new BigDecimal("9.50"), "SALADE"));
        createDish(new Dish(null, "Sushi Mix", "Assortiment de 12 sushis variés", new BigDecimal("18.90"), "SUSHI"));
        createDish(new Dish(null, "Pasta Carbonara", "Pâtes à la crème, lardons, parmesan", new BigDecimal("11.50"), "PASTA"));
        createDish(new Dish(null, "Tacos Poulet", "3 tacos au poulet épicé", new BigDecimal("8.90"), "TACOS"));
    }

    public List<Dish> getAllDishes() {
        return new ArrayList<>(dishes.values());
    }

    public Optional<Dish> getDishById(Long id) {
        return Optional.ofNullable(dishes.get(id));
    }

    public Dish createDish(Dish dish) {
        Long id = idGenerator.getAndIncrement();
        dish.setId(id);
        dishes.put(id, dish);
        return dish;
    }

    public Optional<Dish> updateDish(Long id, Dish updatedDish) {
        Dish existingDish = dishes.get(id);
        if (existingDish == null) {
            return Optional.empty();
        }

        existingDish.setName(updatedDish.getName());
        existingDish.setDescription(updatedDish.getDescription());
        existingDish.setPrice(updatedDish.getPrice());
        existingDish.setCategory(updatedDish.getCategory());
        if (updatedDish.getAvailable() != null) {
            existingDish.setAvailable(updatedDish.getAvailable());
        }

        return Optional.of(existingDish);
    }

    public boolean deleteDish(Long id) {
        return dishes.remove(id) != null;
    }

    public List<Dish> getDishesByCategory(String category) {
        return dishes.values().stream()
                .filter(dish -> dish.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
}
