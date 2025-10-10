package anthony2.com.delivery_service.controller;

import anthony2.com.delivery_service.dto.*;
import anthony2.com.delivery_service.model.DeliveryStatus;
import anthony2.com.delivery_service.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des livraisons
 */
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery Management", description = "APIs pour gérer les livraisons")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Vérifier que le service est opérationnel")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Delivery Service is UP");
    }

    @PostMapping
    @Operation(summary = "Créer une livraison", description = "Créer une nouvelle livraison pour une commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Livraison créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public Mono<ResponseEntity<DeliveryResponse>> createDelivery(
            @Valid @RequestBody DeliveryRequest request) {
        return deliveryService.createDelivery(request)
                .map(delivery -> ResponseEntity.status(HttpStatus.CREATED).body(delivery));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une livraison", description = "Récupérer les détails d'une livraison par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livraison trouvée"),
        @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    public ResponseEntity<DeliveryResponse> getDeliveryById(
            @Parameter(description = "ID de la livraison") @PathVariable Long id) {
        DeliveryResponse delivery = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Consulter une livraison par commande",
            description = "Récupérer les détails d'une livraison par l'ID de la commande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livraison trouvée"),
        @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    public ResponseEntity<DeliveryResponse> getDeliveryByOrderId(
            @Parameter(description = "ID de la commande") @PathVariable Long orderId) {
        DeliveryResponse delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les livraisons",
            description = "Récupérer la liste de toutes les livraisons")
    @ApiResponse(responseCode = "200", description = "Liste des livraisons récupérée")
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        List<DeliveryResponse> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lister les livraisons par statut",
            description = "Récupérer toutes les livraisons ayant un statut donné")
    @ApiResponse(responseCode = "200", description = "Liste des livraisons récupérée")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(
            @Parameter(description = "Statut de livraison") @PathVariable DeliveryStatus status) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/person/{deliveryPersonId}")
    @Operation(summary = "Lister les livraisons d'un livreur",
            description = "Récupérer toutes les livraisons assignées à un livreur")
    @ApiResponse(responseCode = "200", description = "Liste des livraisons récupérée")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByDeliveryPerson(
            @Parameter(description = "ID du livreur") @PathVariable Long deliveryPersonId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByDeliveryPerson(deliveryPersonId);
        return ResponseEntity.ok(deliveries);
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assigner un livreur",
            description = "Assigner un livreur à une livraison en attente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livreur assigné avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    public ResponseEntity<DeliveryResponse> assignDeliveryPerson(
            @Parameter(description = "ID de la livraison") @PathVariable Long id,
            @Valid @RequestBody AssignDeliveryPersonRequest request) {
        DeliveryResponse delivery = deliveryService.assignDeliveryPerson(id, request);
        return ResponseEntity.ok(delivery);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Modifier le statut",
            description = "Mettre à jour le statut d'une livraison")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Transition de statut invalide"),
        @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @Parameter(description = "ID de la livraison") @PathVariable Long id,
            @Valid @RequestBody DeliveryStatusUpdateRequest request) {
        DeliveryResponse delivery = deliveryService.updateDeliveryStatus(id, request);
        return ResponseEntity.ok(delivery);
    }
}
