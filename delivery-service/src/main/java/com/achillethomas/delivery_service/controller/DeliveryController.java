package com.achillethomas.delivery_service.controller;

import com.achillethomas.delivery_service.dto.AssignDeliveryPersonRequest;
import com.achillethomas.delivery_service.dto.CreateDeliveryRequest;
import com.achillethomas.delivery_service.dto.UpdateDeliveryStatusRequest;
import com.achillethomas.delivery_service.model.Delivery;
import com.achillethomas.delivery_service.model.DeliveryStatus;
import com.achillethomas.delivery_service.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * Créer une nouvelle livraison
     */
    @PostMapping("/deliveries")
    public ResponseEntity<Delivery> createDelivery(@RequestBody CreateDeliveryRequest request) {
        if (request.getOrderId() == null || request.getDeliveryAddress() == null || request.getCustomerName() == null) {
            return ResponseEntity.badRequest().build();
        }
        Delivery delivery = deliveryService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    /**
     * Lister toutes les livraisons
     */
    @GetMapping("/deliveries")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    /**
     * Consulter une livraison par son ID
     */
    @GetMapping("/deliveries/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Consulter une livraison par l'ID de la commande
     */
    @GetMapping("/deliveries/order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Assigner un livreur à une livraison
     */
    @PutMapping("/deliveries/{id}/assign")
    public ResponseEntity<Delivery> assignDeliveryPerson(
            @PathVariable Long id,
            @RequestBody AssignDeliveryPersonRequest request) {
        if (request.getDeliveryPersonName() == null || request.getDeliveryPersonName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return deliveryService.assignDeliveryPerson(id, request.getDeliveryPersonName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour le statut d'une livraison
     */
    @PutMapping("/deliveries/{id}/status")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestBody UpdateDeliveryStatusRequest request) {
        if (request.getStatus() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Si un livreur est fourni, l'assigner d'abord
        if (request.getDeliveryPersonName() != null && !request.getDeliveryPersonName().isBlank()) {
            deliveryService.assignDeliveryPerson(id, request.getDeliveryPersonName());
        }
        
        return deliveryService.updateDeliveryStatus(id, request.getStatus(), request.getNotes())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lister les livraisons par statut
     */
    @GetMapping("/deliveries/status/{status}")
    public ResponseEntity<List<Delivery>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    /**
     * Lister les livraisons d'un livreur
     */
    @GetMapping("/deliveries/person/{deliveryPersonName}")
    public ResponseEntity<List<Delivery>> getDeliveriesByDeliveryPerson(@PathVariable String deliveryPersonName) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByDeliveryPerson(deliveryPersonName));
    }

    /**
     * Supprimer une livraison (pour les tests)
     */
    @DeleteMapping("/deliveries/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        if (deliveryService.deleteDelivery(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Delivery Service is running");
    }
}
