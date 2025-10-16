package com.achillethomas.order_service.service;

import com.achillethomas.order_service.model.Order;
import com.achillethomas.order_service.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    private final Map<String, Order> orders = new HashMap<>();

    public Order createOrder(String userId, List<String> dishIds, double totalAmount) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(
            orderId,
            userId,
            dishIds,
            OrderStatus.CREATED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            totalAmount
        );
        orders.put(orderId, order);
        return order;
    }

    public Order getOrder(String orderId) {
        return Optional.ofNullable(orders.get(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public List<Order> getUserOrders(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }

    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = getOrder(orderId);
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
}