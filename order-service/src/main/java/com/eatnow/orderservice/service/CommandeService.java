package com.eatnow.orderservice.service;

import com.eatnow.orderservice.model.Commande;
import com.eatnow.orderservice.model.Plat;
import com.eatnow.orderservice.model.StatutCommande;
import com.eatnow.orderservice.repository.CommandeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final MenuServiceClient menuServiceClient;

    public CommandeService(CommandeRepository commandeRepository, MenuServiceClient menuServiceClient) {
        this.commandeRepository = commandeRepository;
        this.menuServiceClient = menuServiceClient;
    }

    public Commande creerCommande(Map<UUID, Integer> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La liste d'items ne peut pas être vide.");
        }

        List<Plat> platsCommandes = new ArrayList<>();
        BigDecimal prixTotal = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : items.entrySet()) {
            UUID platId = entry.getKey();
            int quantite = entry.getValue();

            Plat plat = menuServiceClient.getPlat(platId)
                    .orElseThrow(() -> new IllegalArgumentException("Plat non trouvé: " + platId));

            for (int i = 0; i < quantite; i++) {
                platsCommandes.add(plat);
                prixTotal = prixTotal.add(plat.prix());
            }
        }

        Commande nouvelleCommande = new Commande(UUID.randomUUID(), platsCommandes, prixTotal, StatutCommande.CREEE);
        return commandeRepository.save(nouvelleCommande);
    }

    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    public Commande changeStatut(UUID id, StatutCommande statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + id));

        Commande commandeMiseAJour = new Commande(commande.id(), commande.plats(), commande.prixTotal(), statut);
        return commandeRepository.save(commandeMiseAJour);
    }
}
