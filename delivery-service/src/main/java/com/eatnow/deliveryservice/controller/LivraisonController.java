package com.eatnow.deliveryservice.controller;

import com.eatnow.deliveryservice.model.Livraison;
import com.eatnow.deliveryservice.model.StatutLivraison;
import com.eatnow.deliveryservice.service.LivraisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/livraisons")
@Tag(name = "API des Livraisons", description = "Gestion des livraisons de commandes")
public class LivraisonController {

    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle livraison pour une commande")
    public Livraison createLivraison(@RequestBody CreationLivraisonRequest request) {
        return livraisonService.creerLivraison(request.commandeId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le statut d'une livraison")
    public ResponseEntity<Livraison> getLivraisonById(@PathVariable UUID id) {
        return livraisonService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/assigner")
    @Operation(summary = "Assigner un livreur à une livraison")
    public Livraison assignerLivreur(@PathVariable UUID id, @RequestBody AssignationLivreurRequest request) {
        return livraisonService.assignerLivreur(id, request.livreur());
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'une livraison")
    public Livraison updateStatut(@PathVariable UUID id, @RequestBody StatutLivraison statut) {
        return livraisonService.mettreAJourStatut(id, statut);
    }
}
