package com.achillethomas.order_service.service;

import com.achillethomas.order_service.client.DeliveryServiceClient;
import com.achillethomas.order_service.client.MenuServiceClient;
import com.achillethomas.order_service.dto.DeliveryResponse;
import com.achillethomas.order_service.dto.DishValidationResponse;
import com.achillethomas.order_service.model.Order;
import com.achillethomas.order_service.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final Map<String, Order> orders = new HashMap<>();
    private final MenuServiceClient menuServiceClient;
    private final DeliveryServiceClient deliveryServiceClient;

    public OrderService(MenuServiceClient menuServiceClient, 
                       DeliveryServiceClient deliveryServiceClient) {
        this.menuServiceClient = menuServiceClient;
        this.deliveryServiceClient = deliveryServiceClient;
    }

    /**
     * Crée une nouvelle commande avec validation des plats et création automatique de la livraison
     * 
     * @param userId ID de l'utilisateur
     * @param dishIds Liste des IDs de plats
     * @param totalAmount Montant total
     * @param deliveryAddress Adresse de livraison
     * @param customerName Nom du client
     * @return Order créée
     * @throws RuntimeException si les plats ne sont pas valides ou si le menu-service est indisponible
     */
    public Order createOrder(String userId, List<String> dishIds, double totalAmount, 
                           String deliveryAddress, String customerName) {
        logger.info("Creating order for user {} with {} dishes", userId, dishIds.size());
        
        // Étape 1 : Valider les plats avec le menu-service
        CompletableFuture<DishValidationResponse> validationFuture = 
            menuServiceClient.validateDishes(dishIds);
        
        DishValidationResponse validation;
        try {
            validation = validationFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error validating dishes: {}", e.getMessage());
            throw new RuntimeException("Unable to validate dishes: " + e.getMessage(), e);
        }
        
        if (!validation.isValid()) {
            logger.warn("Dish validation failed: {}", validation.getMessage());
            throw new RuntimeException("Invalid dishes: " + validation.getMessage());
        }
        
        logger.info("Dishes validated successfully");
        
        // Étape 2 : Créer la commande
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
        order.setDeliveryAddress(deliveryAddress);
        order.setCustomerName(customerName);
        orders.put(orderId, order);
        
        logger.info("Order created with ID: {}", orderId);
        
        // Étape 3 : Créer la livraison de manière asynchrone
        CompletableFuture<DeliveryResponse> deliveryFuture = 
            deliveryServiceClient.createDelivery(orderId, deliveryAddress, customerName);
        
        try {
            DeliveryResponse delivery = deliveryFuture.get();
            if (delivery != null) {
                order.setDeliveryId(delivery.getId());
                logger.info("Delivery created with ID: {}", delivery.getId());
            } else {
                logger.warn("Delivery creation failed, but order was created. Manual delivery creation required.");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error creating delivery: {}. Order created but delivery needs manual creation.", 
                        e.getMessage());
            // On ne bloque pas la création de la commande si la livraison échoue
        }
        
        return order;
    }
    
    /**
     * Version de createOrder pour compatibilité avec l'ancien code
     */
    public Order createOrder(String userId, List<String> dishIds, double totalAmount) {
        // Valeurs par défaut pour la livraison
        return createOrder(userId, dishIds, totalAmount, "Address not provided", "Customer " + userId);
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