package fr.eatnow.menu.controllers;

import fr.eatnow.menu.dtos.CreateDishRequest;
import fr.eatnow.menu.models.Dish;
import fr.eatnow.menu.services.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dishes")
@Tag(name = "Menu Service", description = "Gestion des plats")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les plats")
    @ApiResponse(responseCode = "200", description = "Liste des plats")
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(menuService.getAllDishes());
    }

    @PostMapping
    @Operation(summary = "Ajouter un plat")
    @ApiResponse(responseCode = "201", description = "Plat créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<Dish> createDish(@Valid @RequestBody CreateDishRequest request) {
        Dish newDish = menuService.createDish(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDish);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un plat par ID")
    @ApiResponse(responseCode = "200", description = "Détails du plat")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Dish> getDishById(@PathVariable String id) {
        Optional<Dish> dish = menuService.getDishById(id);
        return dish.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un plat")
    @ApiResponse(responseCode = "200", description = "Plat mis à jour")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Dish> updateDish(@PathVariable String id, @Valid @RequestBody Dish dish) {
        Optional<Dish> updated = menuService.updateDish(id, dish);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un plat")
    @ApiResponse(responseCode = "204", description = "Plat supprimé")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Void> deleteDish(@PathVariable String id) {
        if (menuService.deleteDish(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
