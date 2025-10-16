package com.achillethomas.menu_service.controller;

import com.achillethomas.menu_service.model.Dish;
import com.achillethomas.menu_service.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@Tag(name = "Menu", description = "API de gestion du catalogue des plats")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Lister tous les plats
     */
    @Operation(summary = "Lister tous les plats", description = "Récupère la liste complète des plats du catalogue")
    @GetMapping("/dishes")
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(menuService.getAllDishes());
    }

    /**
     * Consulter le détail d'un plat
     */
    @Operation(summary = "Consulter un plat", description = "Récupère les détails d'un plat par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plat trouvé"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    })
    @GetMapping("/dishes/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        return menuService.getDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Ajouter un plat
     */
    @Operation(summary = "Ajouter un plat", description = "Crée un nouveau plat dans le catalogue")
    @ApiResponse(responseCode = "201", description = "Plat créé avec succès")
    @PostMapping("/dishes")
    public ResponseEntity<Dish> addDish(@RequestBody Dish dish) {
        Dish createdDish = menuService.addDish(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    /**
     * Modifier un plat
     */
    @Operation(summary = "Modifier un plat", description = "Met à jour les informations d'un plat existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plat modifié avec succès"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    })
    @PutMapping("/dishes/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        return menuService.updateDish(id, dish)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprimer un plat
     */
    @Operation(summary = "Supprimer un plat", description = "Supprime un plat du catalogue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plat supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    })
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
