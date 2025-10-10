package ahmed.project.spring.deliveryservice.controller;

import ahmed.project.spring.deliveryservice.dto.CreateDeliveryRequest;
import ahmed.project.spring.deliveryservice.model.Delivery;
import ahmed.project.spring.deliveryservice.model.DeliveryStatus;
import ahmed.project.spring.deliveryservice.service.DeliveryService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Delivery", description = "API de gestion des livraisons")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final Counter deliveryRequestCounter;

    public DeliveryController(DeliveryService deliveryService, MeterRegistry meterRegistry) {
        this.deliveryService = deliveryService;
        this.deliveryRequestCounter = Counter.builder("delivery_service_requests_total")
                .description("Total number of requests to delivery service")
                .register(meterRegistry);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle livraison", description = "Crée une livraison pour une commande")
    @ApiResponse(responseCode = "201", description = "Livraison créée avec succès")
    public ResponseEntity<CompletableFuture<Delivery>> createDelivery(@RequestBody CreateDeliveryRequest request) {
        deliveryRequestCounter.increment();
        CompletableFuture<Delivery> delivery = deliveryService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une livraison", description = "Retourne les détails d'une livraison")
    @ApiResponse(responseCode = "200", description = "Livraison trouvée")
    @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        deliveryRequestCounter.increment();
        return deliveryService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Récupérer la livraison d'une commande")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable Long orderId) {
        deliveryRequestCounter.increment();
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les livraisons")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        deliveryRequestCounter.increment();
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assigner un livreur", description = "Assigne un livreur à une livraison")
    public ResponseEntity<Delivery> assignDriver(
            @PathVariable Long id,
            @RequestParam String driverId,
            @RequestParam String driverName) {
        deliveryRequestCounter.increment();
        return deliveryService.assignDriver(id, driverId, driverName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut de la livraison")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam DeliveryStatus status) {
        deliveryRequestCounter.increment();
        return deliveryService.updateDeliveryStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Mettre à jour la position du livreur")
    public ResponseEntity<Delivery> updateLocation(
            @PathVariable Long id,
            @RequestParam String location) {
        deliveryRequestCounter.increment();
        return deliveryService.updateLocation(id, location)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
