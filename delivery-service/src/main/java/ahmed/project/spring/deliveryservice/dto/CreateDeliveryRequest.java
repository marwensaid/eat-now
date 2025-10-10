package ahmed.project.spring.deliveryservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête de création de livraison")
public class CreateDeliveryRequest {

    @Schema(description = "ID de la commande", example = "1", required = true)
    private Long orderId;

    @Schema(description = "Adresse de livraison", example = "15 rue de la Paix, Paris", required = true)
    private String deliveryAddress;

    @Schema(description = "Temps estimé en minutes", example = "30")
    private Integer estimatedTimeMinutes;

    public CreateDeliveryRequest() {
    }

    public CreateDeliveryRequest(Long orderId, String deliveryAddress) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.estimatedTimeMinutes = 30; // Par défaut 30 minutes
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

}
