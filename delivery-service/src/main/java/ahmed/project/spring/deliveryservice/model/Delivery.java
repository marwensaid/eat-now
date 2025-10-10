package ahmed.project.spring.deliveryservice.model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Représente une livraison")
public class Delivery {
    @Schema(description = "Identifiant unique de la livraison", example = "1")
    private Long id;

    @Schema(description = "ID de la commande associée", example = "1")
    private Long orderId;

    @Schema(description = "ID du livreur", example = "driver42")
    private String driverId;

    @Schema(description = "Nom du livreur", example = "Jean Dupont")
    private String driverName;

    @Schema(description = "Adresse de livraison")
    private String deliveryAddress;

    @Schema(description = "Statut de la livraison", example = "ASSIGNED")
    private DeliveryStatus status;

    @Schema(description = "Position actuelle du livreur")
    private String currentLocation;

    @Schema(description = "Temps estimé d'arrivée (en minutes)", example = "25")
    private Integer estimatedTimeMinutes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;

    public Delivery() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = DeliveryStatus.PENDING;
    }

    public Delivery(Long id, Long orderId, String deliveryAddress) {
        this();
        this.id = id;
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == DeliveryStatus.DELIVERED) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
}
