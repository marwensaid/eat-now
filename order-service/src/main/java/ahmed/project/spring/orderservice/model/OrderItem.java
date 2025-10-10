package ahmed.project.spring.orderservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Item d'une commande")
public class OrderItem {

    @Schema(description = "ID du plat", example = "1")
    private Long dishId;

    @Schema(description = "Nom du plat", example = "Pizza Margherita")
    private String dishName;

    @Schema(description = "Quantité", example = "2")
    private Integer quantity;

    @Schema(description = "Prix unitaire", example = "12.50")
    private BigDecimal unitPrice;

    @Schema(description = "Sous-total", example = "25.00")
    private BigDecimal subtotal;

    public OrderItem() {
    }

    public OrderItem(Long dishId, String dishName, Integer quantity, BigDecimal unitPrice) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters et Setters
    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
