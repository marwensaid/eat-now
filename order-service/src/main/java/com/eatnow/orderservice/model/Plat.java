package com.eatnow.orderservice.model;

import java.math.BigDecimal;
import java.util.UUID;

// DTO (Data Transfer Object) pour représenter un plat récupéré de menu-service.
public record Plat(
        UUID id,
        String nom,
        BigDecimal prix
) {}
