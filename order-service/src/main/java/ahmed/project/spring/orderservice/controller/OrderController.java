package ahmed.project.spring.orderservice.controller;

import ahmed.project.spring.orderservice.dto.CreateOrderRequest;
import ahmed.project.spring.orderservice.model.Order;
import ahmed.project.spring.orderservice.model.OrderStatus;
import ahmed.project.spring.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final OrderService orderService;
    private final Counter orderRequestCounter;

    public OrderController(OrderService orderService, MeterRegistry meterRegistry) {
        this.orderService = orderService;
        this.orderRequestCounter = Counter.builder("order_service_requests_total")
                .description("Total number of requests to order service")
                .register(meterRegistry);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande", description = "Crée une commande à partir des plats sélectionnés")
    @ApiResponse(responseCode = "201", description = "Commande créée avec succès")
    @ApiResponse(responseCode = "400", description = "Requête invalide")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        orderRequestCounter.increment();
        Order createdOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande", description = "Retourne les détails d'une commande")
    @ApiResponse(responseCode = "200", description = "Commande trouvée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        orderRequestCounter.increment();
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les commandes d'un utilisateur")
    @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId) {
        orderRequestCounter.increment();
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les commandes")
    public ResponseEntity<List<Order>> getAllOrders() {
        orderRequestCounter.increment();
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une commande")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        orderRequestCounter.increment();
        return orderService.updateOrderStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
