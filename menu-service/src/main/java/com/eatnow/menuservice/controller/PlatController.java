package com.eatnow.menuservice.controller;

import com.eatnow.menuservice.model.Plat;
import com.eatnow.menuservice.repository.PlatRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plats")
@Tag(name = "API des Plats", description = "Gestion du catalogue des plats")
public class PlatController {

    private final PlatRepository platRepository;

    public PlatController(PlatRepository platRepository) {
        this.platRepository = platRepository;
    }

    @GetMapping
    @Operation(summary = "Lister tous les plats", description = "Récupère la liste de tous les plats disponibles.")
    public List<Plat> getAllPlats() {
        return platRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un plat par son ID", description = "Récupère les détails d'un plat spécifique.")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Plat> getPlatById(@PathVariable UUID id) {
        return platRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Ajouter un nouveau plat", description = "Crée un nouveau plat dans le catalogue.")
    @ResponseStatus(HttpStatus.CREATED)
    public Plat createPlat(@RequestBody Plat plat) {
        return platRepository.save(plat);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un plat existant", description = "Met à jour les informations d'un plat.")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    public ResponseEntity<Plat> updatePlat(@PathVariable UUID id, @RequestBody Plat platDetails) {
        return platRepository.findById(id)
                .map(plat -> {
                    Plat updatedPlat = new Plat(id, platDetails.nom(), platDetails.description(), platDetails.prix());
                    return ResponseEntity.ok(platRepository.save(updatedPlat));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un plat", description = "Supprime un plat du catalogue par son ID.")
    @ApiResponse(responseCode = "204", description = "Plat supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletePlat(@PathVariable UUID id) {
        if (!platRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        platRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/error")
    @Operation(summary = "Endpoint de test pour générer une erreur 500")
    public void createError() {
        throw new RuntimeException("Erreur de test pour Prometheus");
    }
}
