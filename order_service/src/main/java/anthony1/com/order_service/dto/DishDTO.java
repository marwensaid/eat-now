package anthony1.com.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un plat depuis le menu_service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private boolean available;
}
