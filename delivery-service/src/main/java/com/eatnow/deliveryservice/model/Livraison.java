package com.eatnow.deliveryservice.model;

import java.util.UUID;

public record Livraison(
        UUID livraisonId,
        UUID commandeId,
        String livreur,
        StatutLivraison statut
) {}
