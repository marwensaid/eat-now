package com.eatnow.orderservice.controller;

import java.util.Map;
import java.util.UUID;

// DTO pour la requête de création de commande
public record CreationCommandeRequest(Map<UUID, Integer> items) {}
