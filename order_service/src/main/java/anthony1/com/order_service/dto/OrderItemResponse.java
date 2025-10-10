package anthony1.com.order_service.dto;

import anthony1.com.order_service.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'un article de commande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long dishId;
    private String dishName;
    private Double dishPrice;
    private Integer quantity;
    private Double totalPrice;

    public static OrderItemResponse fromOrderItem(OrderItem item) {
        return OrderItemResponse.builder()
                .dishId(item.getDishId())
                .dishName(item.getDishName())
                .dishPrice(item.getDishPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
