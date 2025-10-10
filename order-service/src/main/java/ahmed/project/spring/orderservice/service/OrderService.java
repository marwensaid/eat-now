package ahmed.project.spring.orderservice.service;

import ahmed.project.spring.orderservice.client.MenuClient;
import ahmed.project.spring.orderservice.dto.CreateOrderRequest;
import ahmed.project.spring.orderservice.dto.OrderItemRequest;
import ahmed.project.spring.orderservice.model.Order;
import ahmed.project.spring.orderservice.model.OrderItem;
import ahmed.project.spring.orderservice.model.OrderStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final MenuClient menuClient;

    public OrderService(MenuClient menuClient) {
        this.menuClient = menuClient;
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public Order createOrder(CreateOrderRequest request) {
        logger.info("Creating order for user: {}", request.getUserId());

        Order order = new Order();
        order.setId(idGenerator.getAndIncrement());
        order.setUserId(request.getUserId());
        order.setDeliveryAddress(request.getDeliveryAddress());

        // Récupérer les informations des plats depuis menu-service
        for (OrderItemRequest itemRequest : request.getItems()) {
            try {
                Map<String, Object> dish = menuClient.getDishById(itemRequest.getDishId());

                String dishName = (String) dish.get("name");
                BigDecimal price = new BigDecimal(dish.get("price").toString());

                OrderItem orderItem = new OrderItem(
                        itemRequest.getDishId(),
                        dishName,
                        itemRequest.getQuantity(),
                        price
                );
                order.addItem(orderItem);
            } catch (Exception e) {
                logger.error("Error fetching dish {}: {}", itemRequest.getDishId(), e.getMessage());
                // Continuer avec les autres items
            }
        }

        orders.put(order.getId(), order);
        logger.info("Order created successfully: {}", order.getId());
        return order;
    }

    private Order createOrderFallback(CreateOrderRequest request, Exception ex) {
        logger.error("Fallback: Unable to create order for user {}: {}", request.getUserId(), ex.getMessage());

        // Créer une commande basique sans valider les plats
        Order order = new Order();
        order.setId(idGenerator.getAndIncrement());
        order.setUserId(request.getUserId());
        order.setDeliveryAddress(request.getDeliveryAddress());

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = new OrderItem(
                    itemRequest.getDishId(),
                    "Plat inconnu (service indisponible)",
                    itemRequest.getQuantity(),
                    BigDecimal.ZERO
            );
            order.addItem(orderItem);
        }

        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Optional<Order> updateOrderStatus(Long id, OrderStatus status) {
        Order order = orders.get(id);
        if (order == null) {
            return Optional.empty();
        }

        order.setStatus(status);
        logger.info("Order {} status updated to {}", id, status);
        return Optional.of(order);
    }
}
