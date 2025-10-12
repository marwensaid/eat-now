package fr.eatnow.order.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO représentant un plat depuis le menu-service")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishDto {
    private String id;
    private String name;
    private Double price;
}