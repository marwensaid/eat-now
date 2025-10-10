package com.eatnow.orderservice.controller;

import com.eatnow.orderservice.model.Commande;
import com.eatnow.orderservice.model.StatutCommande;
import com.eatnow.orderservice.service.CommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/commandes")
@Tag(name = "API des Commandes", description = "Gestion des commandes clients")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle commande")
    public Commande createCommande(@RequestBody CreationCommandeRequest request) {
        return commandeService.creerCommande(request.items());
    }

    @GetMapping
    @Operation(summary = "Lister toutes les commandes")
    public List<Commande> getAllCommandes() {
        return commandeService.findAll();
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'une commande")
    public Commande updateStatut(@PathVariable UUID id, @RequestBody StatutCommande statut) {
        return commandeService.changeStatut(id, statut);
    }
}
