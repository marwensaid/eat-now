package com.eatnow.orderservice.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record Commande(
        UUID id,
        List<Plat> plats,
        BigDecimal prixTotal,
        StatutCommande statut
) {}
