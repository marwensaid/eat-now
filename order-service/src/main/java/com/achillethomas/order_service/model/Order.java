package com.achillethomas.order_service.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private String userId;
    private List<String> dishIds;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double totalAmount;

    public Order() {
    }

    public Order(String id, String userId, List<String> dishIds, OrderStatus status, 
                LocalDateTime createdAt, LocalDateTime updatedAt, double totalAmount) {
        this.id = id;
        this.userId = userId;
        this.dishIds = dishIds;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<String> dishIds) {
        this.dishIds = dishIds;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}