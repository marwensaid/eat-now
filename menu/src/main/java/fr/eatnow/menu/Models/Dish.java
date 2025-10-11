package fr.eatnow.menu.Models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@Schema(description = "Modele d'un plat")
@Data   // Génère getters, setters, toString, equals et hashCode
@NoArgsConstructor  // Génère un constructeur sans arguments
@AllArgsConstructor // Génère un constructeur avec tous les arguments
@Builder    // Génère un builder pour la classe
@EqualsAndHashCode(of = {"id", "name"}) // Egalite basé sur id et name
public class Dish {
    @NotNull(message = "L'id ne peut pas etre null")
    @UUID(message = "L'id doit etre un UUID valide")
    private String id;

    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du plat doit contenir entre 2 et 100 caracteres")
    private String name;

    @NotNull(message = "Le prix du plat est obligatoire")
    @Positive(message = "Le prix du plat doit etre un nombre positif")
    @DecimalMin(value = "0.01", message = "Le prix du plat doit etre au moins 0.01")
    @Schema(description = "Le prix du plat en euros", example = "12.50")
    private Double price;
}
