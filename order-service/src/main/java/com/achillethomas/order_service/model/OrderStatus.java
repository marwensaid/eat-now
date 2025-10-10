package com.achillethomas.order_service.model;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    IN_PREPARATION,
    READY_FOR_DELIVERY,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}