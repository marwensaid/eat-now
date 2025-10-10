package com.achillethomas.order_service.service;

import com.achillethomas.order_service.model.Order;
import com.achillethomas.order_service.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }

    public Order getOrderById(String id) {
        return Optional.ofNullable(orders.get(id))
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order createOrder(String userId, List<String> dishIds, Double totalAmount) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(
                orderId,
                userId,
                dishIds,
                OrderStatus.CREATED,
                LocalDateTime.now(),
                totalAmount
        );
        orders.put(orderId, order);
        return order;
    }

    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        orders.put(orderId, order);
        return order;
    }

    public void deleteOrder(String id) {
        if (orders.remove(id) == null) {
            throw new RuntimeException("Order not found with id: " + id);
        }
    }
}