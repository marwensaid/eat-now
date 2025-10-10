package fr.eat_now.menu.Controllers;

import fr.eat_now.menu.Models.Dish;
import fr.eat_now.menu.Services.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public Map<String, List<Dish>> all() {
        return Map.of("data", menuService.getAll());
    }

    @PostMapping
    public Dish add(@RequestBody Dish dish) {
        return menuService.add(dish);
    }

    @GetMapping("/{id}")
    public Dish getById(@PathVariable String id) {
        return menuService.getById(id);
    }

    @PutMapping("/{id}")
    public Dish update(@PathVariable String id, @RequestBody Dish dish) {
        dish.setId(id);
        return menuService.update(dish);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        menuService.delete(id);
    }
}
