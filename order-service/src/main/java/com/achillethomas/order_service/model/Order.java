package com.achillethomas.order_service.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private String userId;
    private List<String> dishIds;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Double totalAmount;

    public Order() {
    }

    public Order(String id, String userId, List<String> dishIds, OrderStatus status, LocalDateTime createdAt, Double totalAmount) {
        this.id = id;
        this.userId = userId;
        this.dishIds = dishIds;
        this.status = status;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getDishIds() {
        return dishIds;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDishIds(List<String> dishIds) {
        this.dishIds = dishIds;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}