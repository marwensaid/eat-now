package com.achillethomas.delivery_service.dto;

public class CreateDeliveryRequest {
    private Long orderId;
    private String deliveryAddress;
    private String customerName;
    private String notes;

    public CreateDeliveryRequest() {
    }

    public CreateDeliveryRequest(Long orderId, String deliveryAddress, String customerName, String notes) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.customerName = customerName;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
