package anthony.com.menu_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Modèle représentant un plat dans le catalogue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish {

    private Long id;

    @NotBlank(message = "Le nom du plat est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private Double price;

    @NotBlank(message = "La catégorie est obligatoire")
    private String category; // ex: ENTREE, PLAT, DESSERT, BOISSON

    private boolean available;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
