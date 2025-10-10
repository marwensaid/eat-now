package ahmed.project.spring.orderservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Représente une commande client")
public class Order {

    @Schema(description = "Identifiant unique de la commande", example = "1")
    private Long id;

    @Schema(description = "Identifiant du client", example = "user123")
    private String userId;

    @Schema(description = "Liste des items de la commande")
    private List<OrderItem> items;

    @Schema(description = "Montant total de la commande", example = "45.50")
    private BigDecimal totalAmount;

    @Schema(description = "Statut de la commande", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "Adresse de livraison")
    private String deliveryAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalAmount = BigDecimal.ZERO;
    }

    public Order(Long id, String userId, String deliveryAddress) {
        this();
        this.id = id;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotalAmount();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
