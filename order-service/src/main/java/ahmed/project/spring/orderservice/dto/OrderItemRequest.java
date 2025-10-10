package ahmed.project.spring.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item de commande dans la requête")
public class OrderItemRequest {
    @Schema(description = "ID du plat", example = "1", required = true)
    private Long dishId;

    @Schema(description = "Quantité", example = "2", required = true)
    private Integer quantity;

    public OrderItemRequest() {
    }

    public OrderItemRequest(Long dishId, Integer quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
