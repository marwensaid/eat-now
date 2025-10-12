package fr.eatnow.order.services;

import fr.eatnow.order.clients.MenuServiceClient;
import fr.eatnow.order.dtos.CreateOrderRequest;
import fr.eatnow.order.dtos.DishDto;
import fr.eatnow.order.models.Order;
import fr.eatnow.order.models.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final MenuServiceClient menuClient;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public List<Order> getAllOrders() {
        return orders.values().stream().toList();
    }

    public Order createOrder(CreateOrderRequest request) {
        log.info("🛒 Création commande pour {} - {} plats",
                request.getCustomerEmail(), request.getDishIds().size());

        // La résilience est GÈRÉE dans le MenuServiceClient
        List<OrderItem> orderItems = validateAndCreateOrderItems(request.getDishIds());

        if (orderItems.isEmpty()) {
            log.warn("⚠️ Aucun plat valide trouvé pour {}", request.getCustomerEmail());
            throw new IllegalArgumentException(
                    "Aucun plat disponible. Le menu-service peut être temporairement indisponible."
            );
        }

        double totalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();

        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .customerEmail(request.getCustomerEmail())
                .items(orderItems)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        orders.put(order.getId(), order);
        log.info("✅ Commande {} créée - Total: {}€", order.getId(), totalAmount);

        return order;
    }

    public Optional<Order> getOrderById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public List<Order> getOrdersByCustomer(String customerEmail) {
        return orders.values().stream()
                .filter(order -> order.getCustomerEmail().equalsIgnoreCase(customerEmail))
                .toList();
    }

    public Optional<Order> updateOrderStatus(String orderId, Order.OrderStatus newStatus) {
        return getOrderById(orderId)
                .map(order -> {
                    log.info("🔄 Statut {} → {} pour commande {}",
                            order.getStatus(), newStatus, orderId);
                    order.setStatus(newStatus);
                    orders.put(orderId, order);
                    return order;
                });
    }

    private List<OrderItem> validateAndCreateOrderItems(List<String> dishIds) {
        log.debug("🔍 Validation de {} plats", dishIds.size());

        // L'appel est déjà protégé par CircuitBreaker dans MenuServiceClient
        List<OrderItem> orderItems = dishIds.stream()
                .map(this::createOrderItem)
                .filter(Objects::nonNull)  // Filtrer les plats indisponibles
                .toList();

        log.info("✅ {} plats valides créés pour la commande", orderItems.size());
        return orderItems;
    }

    private OrderItem createOrderItem(String dishId) {
        // L'appel getDishById est déjà résilient
        return menuClient.getDishById(dishId)
                .map(dish -> OrderItem.builder()
                        .dishId(dishId)
                        .dish(dish)
                        .quantity(1)
                        .unitPrice(dish.getPrice())
                        .totalPrice(dish.getPrice())
                        .build())
                .orElse(null);  // Null si plat indisponible (filtré après)
    }

}
