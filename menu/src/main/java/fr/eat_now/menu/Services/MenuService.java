package fr.eat_now.menu.Services;

import fr.eat_now.menu.Models.Dish;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MenuService {
    private final Map<String, Dish> dishes = new ConcurrentHashMap<>();

    public MenuService() {
        Dish dish1 = new Dish(UUID.randomUUID().toString(), "Salade Cesar",  12.99);
        Dish dish2 = new Dish(UUID.randomUUID().toString(), "Classic Burger", 14.99);
        Dish dish3 = new Dish(UUID.randomUUID().toString(), "Tiramisu Speculos", 8.99);
        dishes.put(dish1.getId(), dish1);
        dishes.put(dish2.getId(), dish2);
        dishes.put(dish3.getId(), dish3);
    }

    public List<Dish> getAll() {
        return new ArrayList<>(dishes.values());
    }

    public Dish getById(String id) {
        return dishes.get(id);
    }

    public Dish add(Dish dish) {
        dish.setId(UUID.randomUUID().toString());
        dishes.put(dish.getId(), dish);
        return dish;
    }

    public Dish update(Dish dish) {
        dishes.put(dish.getId(), dish);
        return dish;
    }

    public void delete(String id) {
        dishes.remove(id);
    }
}
