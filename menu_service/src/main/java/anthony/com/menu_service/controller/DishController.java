package anthony.com.menu_service.controller;

import anthony.com.menu_service.dto.DishRequest;
import anthony.com.menu_service.dto.DishResponse;
import anthony.com.menu_service.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion du catalogue des plats
 */
@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu Service", description = "API de gestion du catalogue des plats")
public class DishController {

    private final DishService dishService;

    /**
     * Endpoint de santé
     */
    @GetMapping("/health")
    @Operation(summary = "Vérifier la santé du service")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Menu Service is UP");
    }

    /**
     * Récupérer tous les plats
     */
    @GetMapping
    @Operation(summary = "Lister tous les plats", description = "Récupère la liste complète de tous les plats du catalogue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des plats récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<DishResponse>> getAllDishes() {
        log.info("GET /api/dishes - Récupération de tous les plats");
        List<DishResponse> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    /**
     * Récupérer un plat par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'un plat", description = "Récupère les détails d'un plat spécifique par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plat trouvé"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DishResponse> getDishById(
            @Parameter(description = "ID du plat à récupérer")
            @PathVariable Long id) {
        log.info("GET /api/dishes/{} - Récupération du plat", id);
        DishResponse dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    /**
     * Récupérer les plats par catégorie
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrer les plats par catégorie", description = "Récupère tous les plats d'une catégorie spécifique (ENTREE, PLAT, DESSERT, BOISSON)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des plats de la catégorie récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<DishResponse>> getDishesByCategory(
            @Parameter(description = "Catégorie des plats (ENTREE, PLAT, DESSERT, BOISSON)")
            @PathVariable String category) {
        log.info("GET /api/dishes/category/{} - Récupération des plats par catégorie", category);
        List<DishResponse> dishes = dishService.getDishesByCategory(category);
        return ResponseEntity.ok(dishes);
    }

    /**
     * Récupérer les plats disponibles
     */
    @GetMapping("/available")
    @Operation(summary = "Filtrer les plats disponibles", description = "Récupère les plats selon leur disponibilité")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des plats disponibles récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<DishResponse>> getAvailableDishes(
            @Parameter(description = "Disponibilité (true pour disponibles, false pour indisponibles)")
            @RequestParam(defaultValue = "true") boolean available) {
        log.info("GET /api/dishes/available?available={} - Récupération des plats disponibles", available);
        List<DishResponse> dishes = dishService.getAvailableDishes(available);
        return ResponseEntity.ok(dishes);
    }

    /**
     * Créer un nouveau plat
     */
    @PostMapping
    @Operation(summary = "Ajouter un plat", description = "Crée un nouveau plat dans le catalogue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plat créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DishResponse> createDish(
            @Parameter(description = "Données du plat à créer")
            @Valid @RequestBody DishRequest dishRequest) {
        log.info("POST /api/dishes - Création d'un nouveau plat : {}", dishRequest.getName());
        DishResponse createdDish = dishService.createDish(dishRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    /**
     * Mettre à jour un plat
     */
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un plat", description = "Met à jour les informations d'un plat existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plat mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DishResponse> updateDish(
            @Parameter(description = "ID du plat à mettre à jour")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du plat")
            @Valid @RequestBody DishRequest dishRequest) {
        log.info("PUT /api/dishes/{} - Mise à jour du plat", id);
        DishResponse updatedDish = dishService.updateDish(id, dishRequest);
        return ResponseEntity.ok(updatedDish);
    }

    /**
     * Supprimer un plat
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un plat", description = "Supprime un plat du catalogue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plat supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> deleteDish(
            @Parameter(description = "ID du plat à supprimer")
            @PathVariable Long id) {
        log.info("DELETE /api/dishes/{} - Suppression du plat", id);
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenir le nombre total de plats
     */
    @GetMapping("/count")
    @Operation(summary = "Compter les plats", description = "Retourne le nombre total de plats dans le catalogue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nombre de plats récupéré"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Long> countDishes() {
        log.info("GET /api/dishes/count - Comptage des plats");
        long count = dishService.countDishes();
        return ResponseEntity.ok(count);
    }
}
