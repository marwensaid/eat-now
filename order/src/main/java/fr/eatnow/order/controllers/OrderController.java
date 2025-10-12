package fr.eatnow.order.controllers;

import fr.eatnow.order.dtos.CreateOrderRequest;
import fr.eatnow.order.models.Order;
import fr.eatnow.order.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Service", description = "Gestion des commandes clients")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Lister toutes les commandes")
    @ApiResponse(responseCode = "200", description = "Liste de toutes les commandes")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande")
    @ApiResponse(responseCode = "201", description = "Commande créée")
    @ApiResponse(responseCode = "400", description = "Données invalides ou plats introuvables")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order newOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Consulter une commande par ID")
    @ApiResponse(responseCode = "200", description = "Commande trouvée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable String orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{orderId}/status/{status}")
    @Operation(summary = "Mettre à jour le statut d'une commande")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<Order> updateOrderStatus(
            @Parameter(description = "ID de la commande") @PathVariable String orderId,
            @Parameter(description = "Nouveau statut (CREATED, CONFIRMED, PREPARING, READY, DELIVERED, CANCELLED)")
            @PathVariable Order.OrderStatus status) {

        Optional<Order> updatedOrder = orderService.updateOrderStatus(orderId, status);
        return updatedOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerEmail}")
    @Operation(summary = "Lister les commandes d'un client")
    @ApiResponse(responseCode = "200", description = "Liste des commandes")
    public ResponseEntity<List<Order>> getOrdersByCustomer(
            @Parameter(description = "Email du client") @PathVariable String customerEmail) {
        List<Order> customerOrders = orderService.getOrdersByCustomer(customerEmail);
        return ResponseEntity.ok(customerOrders);
    }

}
