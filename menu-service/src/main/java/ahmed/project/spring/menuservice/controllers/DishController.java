package ahmed.project.spring.menuservice.controllers;

import ahmed.project.spring.menuservice.DishService;
import ahmed.project.spring.menuservice.models.Dish;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@Tag(name = "Menu", description = "API de gestion du menu")
public class DishController {
    private final DishService dishService;
    private final Counter dishRequestCounter;

    public DishController(DishService dishService, MeterRegistry meterRegistry) {
        this.dishService = dishService;
        this.dishRequestCounter = Counter.builder("menu_service_requests_total")
                .description("Total number of requests to menu service")
                .register(meterRegistry);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les plats", description = "Retourne la liste complète des plats disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des plats récupérée avec succès")
    public ResponseEntity<List<Dish>> getAllDishes() {
        dishRequestCounter.increment();
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un plat par ID", description = "Retourne les détails d'un plat spécifique")
    @ApiResponse(responseCode = "200", description = "Plat trouvé")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        dishRequestCounter.increment();
        return dishService.getDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau plat", description = "Ajoute un nouveau plat au menu")
    @ApiResponse(responseCode = "201", description = "Plat créé avec succès")
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        dishRequestCounter.increment();
        Dish createdDish = dishService.createDish(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un plat", description = "Modifie les informations d'un plat existant")
    @ApiResponse(responseCode = "200", description = "Plat mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        dishRequestCounter.increment();
        return dishService.updateDish(id, dish)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un plat", description = "Retire un plat du menu")
    @ApiResponse(responseCode = "204", description = "Plat supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishRequestCounter.increment();
        if (dishService.deleteDish(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Récupérer les plats par catégorie", description = "Filtre les plats selon leur catégorie")
    public ResponseEntity<List<Dish>> getDishesByCategory(@PathVariable String category) {
        dishRequestCounter.increment();
        return ResponseEntity.ok(dishService.getDishesByCategory(category));
    }
}
