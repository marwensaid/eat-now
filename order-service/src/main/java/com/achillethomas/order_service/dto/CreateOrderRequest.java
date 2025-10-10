package com.achillethomas.order_service.dto;

import java.util.List;

public class CreateOrderRequest {
    private String userId;
    private List<String> dishIds;
    private Double totalAmount;

    // Constructors
    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String userId, List<String> dishIds, Double totalAmount) {
        this.userId = userId;
        this.dishIds = dishIds;
        this.totalAmount = totalAmount;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public List<String> getDishIds() {
        return dishIds;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDishIds(List<String> dishIds) {
        this.dishIds = dishIds;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}