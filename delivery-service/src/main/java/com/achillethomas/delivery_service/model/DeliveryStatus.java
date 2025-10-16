package com.achillethomas.delivery_service.model;

public enum DeliveryStatus {
    PENDING,        // En attente d'assignation
    ASSIGNED,       // Assignée à un livreur
    IN_PROGRESS,    // En cours de livraison
    DELIVERED,      // Livrée
    CANCELLED       // Annulée
}
