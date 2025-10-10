package com.achillethomas.delivery_service.service;

import com.achillethomas.delivery_service.dto.CreateDeliveryRequest;
import com.achillethomas.delivery_service.model.Delivery;
import com.achillethomas.delivery_service.model.DeliveryStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DeliveryService {
    private final Map<Long, Delivery> deliveries = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public DeliveryService() {
        // Initialize with some sample deliveries for testing
        createDelivery(new CreateDeliveryRequest(1L, "123 Rue de la Paix, Paris", "Jean Dupont", "Sonnette cassée"));
        createDelivery(new CreateDeliveryRequest(2L, "456 Avenue des Champs, Lyon", "Marie Martin", null));
    }

    /**
     * Créer une nouvelle livraison
     */
    public Delivery createDelivery(CreateDeliveryRequest request) {
        Long id = idGenerator.getAndIncrement();
        Delivery delivery = new Delivery(
            id,
            request.getOrderId(),
            request.getDeliveryAddress(),
            request.getCustomerName(),
            null,
            DeliveryStatus.PENDING,
            request.getNotes()
        );
        deliveries.put(id, delivery);
        return delivery;
    }

    /**
     * Récupérer toutes les livraisons
     */
    public List<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveries.values());
    }

    /**
     * Récupérer une livraison par son ID
     */
    public Optional<Delivery> getDeliveryById(Long id) {
        return Optional.ofNullable(deliveries.get(id));
    }

    /**
     * Récupérer une livraison par l'ID de la commande
     */
    public Optional<Delivery> getDeliveryByOrderId(Long orderId) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getOrderId().equals(orderId))
                .findFirst();
    }

    /**
     * Assigner un livreur à une livraison
     */
    public Optional<Delivery> assignDeliveryPerson(Long id, String deliveryPersonName) {
        Delivery delivery = deliveries.get(id);
        if (delivery == null) {
            return Optional.empty();
        }
        
        delivery.setDeliveryPersonName(deliveryPersonName);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        return Optional.of(delivery);
    }

    /**
     * Mettre à jour le statut d'une livraison
     */
    public Optional<Delivery> updateDeliveryStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveries.get(id);
        if (delivery == null) {
            return Optional.empty();
        }
        
        delivery.setStatus(status);
        return Optional.of(delivery);
    }

    /**
     * Mettre à jour le statut et les notes d'une livraison
     */
    public Optional<Delivery> updateDeliveryStatus(Long id, DeliveryStatus status, String notes) {
        Delivery delivery = deliveries.get(id);
        if (delivery == null) {
            return Optional.empty();
        }
        
        delivery.setStatus(status);
        if (notes != null) {
            delivery.setNotes(notes);
        }
        return Optional.of(delivery);
    }

    /**
     * Récupérer les livraisons par statut
     */
    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getStatus() == status)
                .toList();
    }

    /**
     * Récupérer les livraisons d'un livreur
     */
    public List<Delivery> getDeliveriesByDeliveryPerson(String deliveryPersonName) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getDeliveryPersonName() != null 
                        && delivery.getDeliveryPersonName().equalsIgnoreCase(deliveryPersonName))
                .toList();
    }

    /**
     * Supprimer une livraison (pour les tests)
     */
    public boolean deleteDelivery(Long id) {
        return deliveries.remove(id) != null;
    }
}
