package com.eatnow.deliveryservice.service;

import com.eatnow.deliveryservice.model.Livraison;
import com.eatnow.deliveryservice.model.StatutLivraison;
import com.eatnow.deliveryservice.repository.LivraisonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;

    public LivraisonService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
    }

    public Livraison creerLivraison(UUID commandeId) {
        Livraison nouvelleLivraison = new Livraison(UUID.randomUUID(), commandeId, null, StatutLivraison.EN_ATTENTE);
        return livraisonRepository.save(nouvelleLivraison);
    }

    public Optional<Livraison> findById(UUID id) {
        return livraisonRepository.findById(id);
    }

    public Livraison assignerLivreur(UUID id, String livreur) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livraison non trouvée: " + id));

        Livraison livraisonMiseAJour = new Livraison(id, livraison.commandeId(), livreur, StatutLivraison.EN_COURS);
        return livraisonRepository.save(livraisonMiseAJour);
    }

    public Livraison mettreAJourStatut(UUID id, StatutLivraison statut) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livraison non trouvée: " + id));

        Livraison livraisonMiseAJour = new Livraison(id, livraison.commandeId(), livraison.livreur(), statut);
        return livraisonRepository.save(livraisonMiseAJour);
    }
}
