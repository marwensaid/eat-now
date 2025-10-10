package com.eatnow.menuservice.repository;

import com.eatnow.menuservice.model.Plat;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PlatRepository {

    private final Map<UUID, Plat> plats = new ConcurrentHashMap<>();

    /**
     * Initialise le repository avec des données de test.
     */
    @PostConstruct
    private void init() {
        Plat plat1 = new Plat(UUID.randomUUID(), "Pizza Margherita", "Pizza classique avec tomate, mozzarella et basilic", new BigDecimal("12.50"));
        Plat plat2 = new Plat(UUID.randomUUID(), "Burger Classique", "Burger avec boeuf, salade, tomate, oignons", new BigDecimal("15.00"));
        Plat plat3 = new Plat(UUID.randomUUID(), "Salade César", "Salade romaine, poulet grillé, croûtons et parmesan", new BigDecimal("11.00"));

        plats.put(plat1.id(), plat1);
        plats.put(plat2.id(), plat2);
        plats.put(plat3.id(), plat3);
    }

    public List<Plat> findAll() {
        return new ArrayList<>(plats.values());
    }

    public Optional<Plat> findById(UUID id) {
        return Optional.ofNullable(plats.get(id));
    }

    public Plat save(Plat plat) {
        if (plat.id() == null) {
            plat = new Plat(UUID.randomUUID(), plat.nom(), plat.description(), plat.prix());
        }
        plats.put(plat.id(), plat);
        return plat;
    }

    public void deleteById(UUID id) {
        plats.remove(id);
    }
}
