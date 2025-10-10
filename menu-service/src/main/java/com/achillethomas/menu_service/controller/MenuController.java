package com.achillethomas.menu_service.controller;

import com.achillethomas.menu_service.model.Dish;
import com.achillethomas.menu_service.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Lister tous les plats
     */
    @GetMapping("/dishes")
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(menuService.getAllDishes());
    }

    /**
     * Consulter le détail d'un plat
     */
    @GetMapping("/dishes/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        return menuService.getDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Ajouter un plat
     */
    @PostMapping("/dishes")
    public ResponseEntity<Dish> addDish(@RequestBody Dish dish) {
        Dish createdDish = menuService.addDish(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    /**
     * Modifier un plat
     */
    @PutMapping("/dishes/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        return menuService.updateDish(id, dish)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprimer un plat
     */
    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        if (menuService.deleteDish(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Lister les plats par catégorie
     */
    @GetMapping("/dishes/category/{category}")
    public ResponseEntity<List<Dish>> getDishesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(menuService.getDishesByCategory(category));
    }

    /**
     * Lister les plats disponibles
     */
    @GetMapping("/dishes/available")
    public ResponseEntity<List<Dish>> getAvailableDishes() {
        return ResponseEntity.ok(menuService.getAvailableDishes());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Menu Service is running");
    }
}
