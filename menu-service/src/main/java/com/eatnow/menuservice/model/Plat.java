package com.eatnow.menuservice.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Représente un plat dans le menu.
 * Utilisation d'un record pour la concision et l'immutabilité.
 */
public record Plat(
        UUID id,
        String nom,
        String description,
        BigDecimal prix
) {}
