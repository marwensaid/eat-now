package com.achillethomas.order_service.dto;

/**
 * Réponse de création de livraison depuis le delivery-service
 */
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private String deliveryAddress;
    private String customerName;
    private String status;
    private String notes;

    public DeliveryResponse() {
    }

    public DeliveryResponse(Long id, Long orderId, String deliveryAddress, String customerName, String status, String notes) {
        this.id = id;
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.customerName = customerName;
        this.status = status;
        this.notes = notes;
    }

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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
