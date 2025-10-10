package anthony.com.menu_service.dto;

import anthony.com.menu_service.model.Dish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un plat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private boolean available;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DishResponse fromDish(Dish dish) {
        return DishResponse.builder()
                .id(dish.getId())
                .name(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .category(dish.getCategory())
                .available(dish.isAvailable())
                .imageUrl(dish.getImageUrl())
                .createdAt(dish.getCreatedAt())
                .updatedAt(dish.getUpdatedAt())
                .build();
    }
}
