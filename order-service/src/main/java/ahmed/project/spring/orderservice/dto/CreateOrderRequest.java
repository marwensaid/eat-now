package ahmed.project.spring.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Requête de création de commande")
public class CreateOrderRequest {

    @Schema(description = "ID de l'utilisateur", example = "user123", required = true)
    private String userId;

    @Schema(description = "Liste des items (dishId et quantity)", required = true)
    private List<OrderItemRequest> items;

    @Schema(description = "Adresse de livraison", example = "15 rue de la Paix, Paris", required = true)
    private String deliveryAddress;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String userId, List<OrderItemRequest> items, String deliveryAddress) {
        this.userId = userId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
