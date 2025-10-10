package anthony1.com.order_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import anthony1.com.order_service.client.MenuServiceClient;
import anthony1.com.order_service.dto.OrderRequest;
import anthony1.com.order_service.dto.OrderResponse;
import anthony1.com.order_service.dto.OrderStatusUpdateRequest;
import anthony1.com.order_service.exception.DishNotFoundException;
import anthony1.com.order_service.exception.InvalidOrderStatusException;
import anthony1.com.order_service.exception.OrderNotFoundException;
import anthony1.com.order_service.model.Order;
import anthony1.com.order_service.model.OrderItem;
import anthony1.com.order_service.model.OrderStatus;
import anthony1.com.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service pour la gestion des commandes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;

    /**
     * Créer une nouvelle commande
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public Mono<OrderResponse> createOrder(OrderRequest orderRequest) {
        log.info("Création d'une commande pour l'utilisateur : {}", orderRequest.getUserId());

        // Récupérer les informations des plats depuis menu_service
        List<Mono<OrderItem>> itemMonos = orderRequest.getItems().stream()
                .map(itemRequest -> menuServiceClient.getDish(itemRequest.getDishId())
                .map(dish -> {
                    if (!dish.isAvailable()) {
                        throw new DishNotFoundException(
                                "Le plat " + dish.getName() + " n'est pas disponible");
                    }
                    return OrderItem.builder()
                            .dishId(dish.getId())
                            .dishName(dish.getName())
                            .dishPrice(dish.getPrice())
                            .quantity(itemRequest.getQuantity())
                            .build();
                }))
                .collect(Collectors.toList());

        // Combiner tous les items et créer la commande
        return Flux.merge(itemMonos)
                .collectList()
                .map(items -> {
                    Order order = Order.builder()
                            .userId(orderRequest.getUserId())
                            .items(items)
                            .deliveryAddress(orderRequest.getDeliveryAddress())
                            .customerName(orderRequest.getCustomerName())
                            .customerPhone(orderRequest.getCustomerPhone())
                            .notes(orderRequest.getNotes())
                            .build();

                    Order savedOrder = orderRepository.createOrder(order);
                    log.info("Commande {} créée avec succès pour l'utilisateur {}",
                            savedOrder.getId(), savedOrder.getUserId());
                    return OrderResponse.fromOrder(savedOrder);
                });
    }

    /**
     * Récupérer une commande par son ID
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderByIdFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public OrderResponse getOrderById(Long id) {
        log.info("Récupération de la commande avec l'id : {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.fromOrder(order);
    }

    /**
     * Récupérer toutes les commandes
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "getAllOrdersFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public List<OrderResponse> getAllOrders() {
        log.info("Récupération de toutes les commandes");
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les commandes d'un utilisateur
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "getUserOrdersFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public List<OrderResponse> getUserOrders(String userId) {
        log.info("Récupération des commandes de l'utilisateur : {}", userId);
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les commandes par statut
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrdersByStatusFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        log.info("Récupération des commandes avec le statut : {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour le statut d'une commande
     */
    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        log.info("Mise à jour du statut de la commande {} vers {}", id, request.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Valider la transition de statut
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.updateOrder(order);

        log.info("Statut de la commande {} mis à jour vers {}", id, request.getStatus());
        return OrderResponse.fromOrder(updatedOrder);
    }

    /**
     * Valider la transition de statut
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // On ne peut pas revenir en arrière dans le workflow (sauf annulation)
        if (newStatus == OrderStatus.CANCELLED) {
            return; // L'annulation est toujours permise
        }

        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Impossible de modifier le statut d'une commande " + currentStatus.getDescription());
        }

        // Vérifier que le nouveau statut est valide dans le workflow
        int currentOrder = currentStatus.ordinal();
        int newOrder = newStatus.ordinal();

        if (newOrder < currentOrder) {
            throw new InvalidOrderStatusException(
                    "Transition invalide : impossible de passer de "
                    + currentStatus.getDescription() + " à " + newStatus.getDescription());
        }
    }

    /**
     * Compter le nombre total de commandes
     */
    public long countOrders() {
        return orderRepository.count();
    }

    /**
     * Compter le nombre de commandes d'un utilisateur
     */
    public long countUserOrders(String userId) {
        return orderRepository.countByUserId(userId);
    }

    // ============= Méthodes de fallback =============
    private Mono<OrderResponse> createOrderFallback(OrderRequest orderRequest, Exception e) {
        log.error("Fallback : Impossible de créer la commande - {}", e.getMessage());
        return Mono.error(new RuntimeException(
                "Service temporairement indisponible pour la création de commandes"));
    }

    private OrderResponse getOrderByIdFallback(Long id, Exception e) {
        log.error("Fallback : Impossible de récupérer la commande {} - {}", id, e.getMessage());
        throw new OrderNotFoundException("Service temporairement indisponible");
    }

    private List<OrderResponse> getAllOrdersFallback(Exception e) {
        log.error("Fallback : Impossible de récupérer les commandes - {}", e.getMessage());
        return List.of();
    }

    private List<OrderResponse> getUserOrdersFallback(String userId, Exception e) {
        log.error("Fallback : Impossible de récupérer les commandes de l'utilisateur {} - {}",
                userId, e.getMessage());
        return List.of();
    }

    private List<OrderResponse> getOrdersByStatusFallback(OrderStatus status, Exception e) {
        log.error("Fallback : Impossible de récupérer les commandes avec le statut {} - {}",
                status, e.getMessage());
        return List.of();
    }

    private OrderResponse updateOrderStatusFallback(Long id, OrderStatusUpdateRequest request, Exception e) {
        log.error("Fallback : Impossible de mettre à jour le statut de la commande {} - {}",
                id, e.getMessage());
        throw new RuntimeException("Service temporairement indisponible pour la mise à jour du statut");
    }
}
