package com.eatnow.orderservice.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

// DTO pour la requête de création de commande
public record CreationCommandeRequest(
        @NotNull(message = "La liste d'items ne peut pas être nulle.")
        @NotEmpty(message = "La liste d'items ne peut pas être vide.")
        Map<UUID, Integer> items
) {}
