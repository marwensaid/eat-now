package com.achillethomas.delivery_service.dto;

import com.achillethomas.delivery_service.model.DeliveryStatus;

public class UpdateDeliveryStatusRequest {
    private DeliveryStatus status;
    private String deliveryPersonName;
    private String notes;

    public UpdateDeliveryStatusRequest() {
    }

    public UpdateDeliveryStatusRequest(DeliveryStatus status, String deliveryPersonName, String notes) {
        this.status = status;
        this.deliveryPersonName = deliveryPersonName;
        this.notes = notes;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
