package com.eatnow.orderservice.repository;

import com.eatnow.orderservice.model.Commande;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CommandeRepository {
    private final Map<UUID, Commande> commandes = new ConcurrentHashMap<>();

    public List<Commande> findAll() {
        return new ArrayList<>(commandes.values());
    }

    public Optional<Commande> findById(UUID id) {
        return Optional.ofNullable(commandes.get(id));
    }

    public Commande save(Commande commande) {
        commandes.put(commande.id(), commande);
        return commande;
    }
}
