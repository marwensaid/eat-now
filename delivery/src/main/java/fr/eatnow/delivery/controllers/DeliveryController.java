package fr.eatnow.delivery.controllers;

import fr.eatnow.delivery.dtos.AssignDeliveryRequest;
import fr.eatnow.delivery.dtos.CreateDeliveryRequest;
import fr.eatnow.delivery.models.Delivery;
import fr.eatnow.delivery.services.DeliveryService;
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
@RequestMapping("/api/deliveries")
@Tag(name = "Delivery Service", description = "Gestion des livraisons")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle livraison pour une commande")
    @ApiResponse(responseCode = "201", description = "Livraison créée")
    @ApiResponse(responseCode = "400", description = "Commande introuvable ou invalide")
    @ApiResponse(responseCode = "503", description = "Order-service indisponible")
    public ResponseEntity<Delivery> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        Delivery newDelivery = deliveryService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDelivery);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les livraisons")
    @ApiResponse(responseCode = "200", description = "Liste de toutes les livraisons")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/pending")
    @Operation(summary = "Lister les livraisons en attente d'assignation")
    @ApiResponse(responseCode = "200", description = "Liste des livraisons PENDING")
    public ResponseEntity<List<Delivery>> getPendingDeliveries() {
        return ResponseEntity.ok(deliveryService.getPendingDeliveries());
    }

    @GetMapping("/{deliveryId}")
    @Operation(summary = "Consulter une livraison par ID")
    @ApiResponse(responseCode = "200", description = "Livraison trouvée")
    @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    public ResponseEntity<Delivery> getDeliveryById(
            @Parameter(description = "ID de la livraison") @PathVariable String deliveryId) {
        Optional<Delivery> delivery = deliveryService.getDeliveryById(deliveryId);
        return delivery.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{deliveryId}")
    @Operation(summary = "Annuler une livraison")
    @ApiResponse(responseCode = "204", description = "Livraison annulée")
    @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    public ResponseEntity<Void> cancelDelivery(
            @Parameter(description = "ID de la livraison") @PathVariable String deliveryId) {
        boolean cancelled = deliveryService.cancelDelivery(deliveryId);
        if (cancelled) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{deliveryId}/assign")
    @Operation(summary = "Assigner un livreur à une livraison")
    @ApiResponse(responseCode = "200", description = "Livreur assigné")
    @ApiResponse(responseCode = "400", description = "Données invalides ou livraison non assignable")
    @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    public ResponseEntity<Delivery> assignDelivery(
            @Parameter(description = "ID de la livraison") @PathVariable String deliveryId,
            @Valid @RequestBody AssignDeliveryRequest request) {
        Delivery assignedDelivery = deliveryService.assignDelivery(deliveryId, request);
        return ResponseEntity.ok(assignedDelivery);
    }

    @PutMapping("/{deliveryId}/status/{status}")
    @Operation(summary = "Mettre à jour le statut de livraison")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour")
    @ApiResponse(responseCode = "400", description = "Transition de statut invalide")
    @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @Parameter(description = "ID de la livraison") @PathVariable String deliveryId,
            @Parameter(description = "Nouveau statut (IN_TRANSIT, DELIVERED, FAILED)")
            @PathVariable Delivery.DeliveryStatus status) {

        Delivery updatedDelivery = deliveryService.updateDeliveryStatus(deliveryId, status);
        return ResponseEntity.ok(updatedDelivery);
    }

    @GetMapping("/person/{deliveryPerson}")
    @Operation(summary = "Lister les livraisons d'un livreur")
    @ApiResponse(responseCode = "200", description = "Liste des livraisons du livreur")
    public ResponseEntity<List<Delivery>> getDeliveriesByPerson(
            @Parameter(description = "Nom du livreur") @PathVariable String deliveryPerson) {
        List<Delivery> personDeliveries = deliveryService.getDeliveriesByPerson(deliveryPerson);
        return ResponseEntity.ok(personDeliveries);
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtenir les statistiques de livraison")
    @ApiResponse(responseCode = "200", description = "Statistiques de livraison")
    public ResponseEntity<DeliveryService.DeliveryStats> getDeliveryStats() {
        return ResponseEntity.ok(deliveryService.getDeliveryStats());
    }
}