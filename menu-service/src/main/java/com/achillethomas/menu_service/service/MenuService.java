package com.achillethomas.menu_service.service;

import com.achillethomas.menu_service.model.Dish;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MenuService {
    private final Map<Long, Dish> dishes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MenuService() {
        // Initialize with some sample dishes test
        addDish(new Dish(null, "Burger Classic", "Burger avec steak, salade, tomate", new BigDecimal("12.50"), "Burgers", true));
        addDish(new Dish(null, "Pizza Margherita", "Pizza tomate, mozzarella, basilic", new BigDecimal("10.00"), "Pizzas", true));
        addDish(new Dish(null, "Salade César", "Salade romaine, poulet, parmesan, croûtons", new BigDecimal("9.50"), "Salades", true));
        addDish(new Dish(null, "Pâtes Carbonara", "Pâtes, lardons, crème, parmesan", new BigDecimal("11.00"), "Pâtes", true));
        addDish(new Dish(null, "Tiramisu", "Dessert italien au café", new BigDecimal("6.50"), "Desserts", true));
    }

    public List<Dish> getAllDishes() {
        return new ArrayList<>(dishes.values());
    }

    public Optional<Dish> getDishById(Long id) {
        return Optional.ofNullable(dishes.get(id));
    }

    public Dish addDish(Dish dish) {
        Long id = idGenerator.getAndIncrement();
        dish.setId(id);
        dishes.put(id, dish);
        return dish;
    }

    public Optional<Dish> updateDish(Long id, Dish updatedDish) {
        if (!dishes.containsKey(id)) {
            return Optional.empty();
        }
        updatedDish.setId(id);
        dishes.put(id, updatedDish);
        return Optional.of(updatedDish);
    }

    public boolean deleteDish(Long id) {
        return dishes.remove(id) != null;
    }

    public List<Dish> getDishesByCategory(String category) {
        return dishes.values().stream()
                .filter(dish -> dish.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public List<Dish> getAvailableDishes() {
        return dishes.values().stream()
                .filter(Dish::isAvailable)
                .toList();
    }
}
