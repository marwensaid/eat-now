package anthony1.com.order_service.repository;

import anthony1.com.order_service.model.Order;
import anthony1.com.order_service.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository pour le stockage en mémoire des commandes
 */
@Repository
public class OrderRepository {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Créer une nouvelle commande
     */
    public Order createOrder(Order order) {
        Long id = idGenerator.getAndIncrement();
        order.setId(id);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.calculateTotalAmount();
        orders.put(id, order);
        return order;
    }

    /**
     * Récupérer une commande par son ID
     */
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    /**
     * Récupérer toutes les commandes
     */
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    /**
     * Récupérer les commandes d'un utilisateur
     */
    public List<Order> findByUserId(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les commandes par statut
     */
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une commande
     */
    public Order updateOrder(Order order) {
        order.setUpdatedAt(LocalDateTime.now());
        order.calculateTotalAmount();
        orders.put(order.getId(), order);
        return order;
    }

    /**
     * Supprimer une commande
     */
    public boolean deleteOrder(Long id) {
        return orders.remove(id) != null;
    }

    /**
     * Vérifier si une commande existe
     */
    public boolean existsById(Long id) {
        return orders.containsKey(id);
    }

    /**
     * Compter le nombre total de commandes
     */
    public long count() {
        return orders.size();
    }

    /**
     * Compter le nombre de commandes d'un utilisateur
     */
    public long countByUserId(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .count();
    }
}
