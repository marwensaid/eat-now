package com.achillethomas.order_service.controller;

import com.achillethomas.order_service.dto.CreateOrderRequest;
import com.achillethomas.order_service.dto.UpdateOrderStatusRequest;
import com.achillethomas.order_service.model.Order;
import com.achillethomas.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Controller", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    @CircuitBreaker(name = "orderService")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public CompletableFuture<ResponseEntity<List<Order>>> getAllOrders() {
        return CompletableFuture.supplyAsync(() -> 
            ResponseEntity.ok(orderService.getAllOrders())
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID")
    @CircuitBreaker(name = "orderService")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public CompletableFuture<ResponseEntity<List<Order>>> getOrdersByUserId(@PathVariable String userId) {
        return CompletableFuture.supplyAsync(() -> 
            ResponseEntity.ok(orderService.getOrdersByUserId(userId))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    @CircuitBreaker(name = "orderService")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public CompletableFuture<ResponseEntity<Order>> getOrderById(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> 
            ResponseEntity.ok(orderService.getOrderById(id))
        );
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    @CircuitBreaker(name = "orderService")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public CompletableFuture<ResponseEntity<Order>> createOrder(@RequestBody CreateOrderRequest request) {
        return CompletableFuture.supplyAsync(() -> 
            ResponseEntity.ok(orderService.createOrder(
                request.getUserId(),
                request.getDishIds(),
                request.getTotalAmount()
            ))
        );
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status")
    @CircuitBreaker(name = "orderService")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public CompletableFuture<ResponseEntity<Order>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusRequest request) {
        return CompletableFuture.supplyAsync(() -> 
            ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()))
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order")
    @CircuitBreaker(name = "orderService")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public CompletableFuture<ResponseEntity<Void>> deleteOrder(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        });
    }
}