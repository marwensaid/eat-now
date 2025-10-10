package com.eatnow.orderservice.model;

import java.util.UUID;

// DTO pour représenter une livraison créée par le delivery-service
public record Livraison(
        UUID livraisonId,
        UUID commandeId,
        String statut
) {}
