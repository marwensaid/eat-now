package fr.eatnow.menu.Services;

import fr.eatnow.menu.Dtos.CreateDishRequest;
import fr.eatnow.menu.Models.Dish;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MenuService {
    private final Map<String, Dish> dishes = new ConcurrentHashMap<>();

    public MenuService() {
        createDish(new CreateDishRequest("Pizza Margherita", 12.0));
        createDish(new CreateDishRequest("Burger Classique", 15.0));
    }

    public List<Dish> getAllDishes() {
        return new ArrayList<>(dishes.values());
    }

    public Optional<Dish> getDishById(String id) {
        return Optional.ofNullable(dishes.get(id));
    }

    public Dish createDish(CreateDishRequest request) {
        Dish dish = Dish.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .price(request.getPrice())
                .build();

        dishes.put(dish.getId(), dish);
        return dish;
    }

    public Optional<Dish> updateDish(String id, Dish updatedDish) {
        if (dishes.containsKey(id)) {
            updatedDish.setId(id);
            dishes.put(id, updatedDish);
            return Optional.of(updatedDish);
        }
        return Optional.empty();
    }

    public boolean deleteDish(String id) {
        return dishes.remove(id) != null;
    }
}
