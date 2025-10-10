package anthony1.com.order_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import anthony1.com.order_service.dto.OrderRequest;
import anthony1.com.order_service.dto.OrderResponse;
import anthony1.com.order_service.dto.OrderStatusUpdateRequest;
import anthony1.com.order_service.model.OrderStatus;
import anthony1.com.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Contrôleur REST pour la gestion des commandes
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Service", description = "API de gestion des commandes")
public class OrderController {

    private final OrderService orderService;

    /**
     * Endpoint de santé
     */
    @GetMapping("/health")
    @Operation(summary = "Vérifier la santé du service")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is UP");
    }

    /**
     * Créer une nouvelle commande
     */
    @PostMapping
    @Operation(summary = "Créer une commande", description = "Crée une nouvelle commande avec références aux plats du menu")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Un ou plusieurs plats non trouvés"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public Mono<ResponseEntity<OrderResponse>> createOrder(
            @Parameter(description = "Données de la commande à créer")
            @Valid @RequestBody OrderRequest orderRequest) {
        log.info("POST /api/orders - Création d'une commande pour l'utilisateur {}",
                orderRequest.getUserId());

        return orderService.createOrder(orderRequest)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order));
    }

    /**
     * Récupérer une commande par son ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consulter une commande", description = "Récupère les détails d'une commande par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande trouvée"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID de la commande")
            @PathVariable Long id) {
        log.info("GET /api/orders/{} - Récupération de la commande", id);
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Récupérer toutes les commandes
     */
    @GetMapping
    @Operation(summary = "Lister toutes les commandes", description = "Récupère la liste de toutes les commandes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET /api/orders - Récupération de toutes les commandes");
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Récupérer les commandes d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les commandes d'un utilisateur", description = "Récupère toutes les commandes d'un utilisateur spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes de l'utilisateur récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable String userId) {
        log.info("GET /api/orders/user/{} - Récupération des commandes de l'utilisateur", userId);
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Récupérer les commandes par statut
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrer les commandes par statut", description = "Récupère toutes les commandes ayant un statut spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes avec le statut récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Statut des commandes (CREATED, CONFIRMED, PREPARING, READY, IN_DELIVERY, DELIVERED, CANCELLED)")
            @PathVariable OrderStatus status) {
        log.info("GET /api/orders/status/{} - Récupération des commandes par statut", status);
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Mettre à jour le statut d'une commande
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut", description = "Met à jour le statut d'une commande (CREATED → DELIVERED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Transition de statut invalide"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "ID de la commande")
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut")
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        log.info("PATCH /api/orders/{}/status - Mise à jour du statut vers {}", id, request.getStatus());
        OrderResponse order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Compter le nombre total de commandes
     */
    @GetMapping("/count")
    @Operation(summary = "Compter les commandes", description = "Retourne le nombre total de commandes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nombre de commandes récupéré"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Long> countOrders() {
        log.info("GET /api/orders/count - Comptage des commandes");
        long count = orderService.countOrders();
        return ResponseEntity.ok(count);
    }

    /**
     * Compter le nombre de commandes d'un utilisateur
     */
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Compter les commandes d'un utilisateur", description = "Retourne le nombre de commandes d'un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nombre de commandes récupéré"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Long> countUserOrders(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable String userId) {
        log.info("GET /api/orders/user/{}/count - Comptage des commandes de l'utilisateur", userId);
        long count = orderService.countUserOrders(userId);
        return ResponseEntity.ok(count);
    }
}
