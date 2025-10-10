package com.eatnow.deliveryservice.repository;

import com.eatnow.deliveryservice.model.Livraison;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
public class LivraisonRepository {
    private final Map<UUID, Livraison> livraisons = new ConcurrentHashMap<>();

    public Livraison save(Livraison livraison) {
        livraisons.put(livraison.livraisonId(), livraison);
        return livraison;
    }

    public Optional<Livraison> findById(UUID id) {
        return Optional.ofNullable(livraisons.get(id));
    }
}
