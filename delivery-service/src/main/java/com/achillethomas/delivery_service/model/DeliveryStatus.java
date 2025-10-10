package com.achillethomas.delivery_service.model;

public enum DeliveryStatus {
    PENDING,        // En attente d'assignation
    ASSIGNED,       // Assignée à un livreur
    PICKED_UP,      // Récupérée par le livreur
    IN_TRANSIT,     // En cours de livraison
    DELIVERED,      // Livrée
    CANCELLED       // Annulée
}
