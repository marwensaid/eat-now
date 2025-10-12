package fr.eatnow.order.models;

import fr.eatnow.order.dtos.DishDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Schema(description = "Élément d'une commande (plat + quantité)")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @NotNull
    @UUID
    private String dishId;

    @NotNull
    private DishDto dish;

    @Min(value = 1, message = "Quantité minimale de 1")
    @Max(value = 100, message = "Quantité maximale de 100")
    private Integer quantity;

    @NotNull
    @Positive
    private Double unitPrice; // Prix au moment de la commande (fixe)

    @NotNull
    @Positive
    private Double totalPrice; // quantity * unitPrice
}