package anthony.com.menu_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO pour la création et mise à jour d'un plat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishRequest {

    @NotBlank(message = "Le nom du plat est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private Double price;

    @NotBlank(message = "La catégorie est obligatoire")
    private String category;

    private Boolean available;

    private String imageUrl;
}
