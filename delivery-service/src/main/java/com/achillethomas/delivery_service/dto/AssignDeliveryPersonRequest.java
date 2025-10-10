package com.achillethomas.delivery_service.dto;

public class AssignDeliveryPersonRequest {
    private String deliveryPersonName;

    public AssignDeliveryPersonRequest() {
    }

    public AssignDeliveryPersonRequest(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }
}
