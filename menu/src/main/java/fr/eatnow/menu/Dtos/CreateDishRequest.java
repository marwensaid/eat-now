package fr.eatnow.menu.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Schema(description = "Requête pour créer un plat (sans ID)")
@Data
@AllArgsConstructor
@Builder
public class CreateDishRequest {
    @NotBlank(message = "Le nom du plat est obligatoire")
    @Size(min = 2, max = 100)
    @Schema(description = "Nom du plat", example = "Pizza Margherita")
    private String name;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    @DecimalMin(value = "0.01")
    @Schema(description = "Prix en euros", example = "12.50")
    private Double price;

}
