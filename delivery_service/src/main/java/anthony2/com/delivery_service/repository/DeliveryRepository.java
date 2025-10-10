package anthony2.com.delivery_service.repository;

import anthony2.com.delivery_service.model.Delivery;
import anthony2.com.delivery_service.model.DeliveryStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository pour gérer les livraisons en mémoire
 */
@Repository
public class DeliveryRepository {

    private final Map<Long, Delivery> deliveries = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Créer une nouvelle livraison
     */
    public Delivery save(Delivery delivery) {
        if (delivery.getId() == null) {
            delivery.setId(idGenerator.getAndIncrement());
            delivery.setCreatedAt(LocalDateTime.now());
        }
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveries.put(delivery.getId(), delivery);
        return delivery;
    }

    /**
     * Trouver une livraison par ID
     */
    public Optional<Delivery> findById(Long id) {
        return Optional.ofNullable(deliveries.get(id));
    }

    /**
     * Trouver toutes les livraisons
     */
    public List<Delivery> findAll() {
        return new ArrayList<>(deliveries.values());
    }

    /**
     * Trouver une livraison par ID de commande
     */
    public Optional<Delivery> findByOrderId(Long orderId) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getOrderId().equals(orderId))
                .findFirst();
    }

    /**
     * Trouver toutes les livraisons par statut
     */
    public List<Delivery> findByStatus(DeliveryStatus status) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Trouver toutes les livraisons assignées à un livreur
     */
    public List<Delivery> findByDeliveryPersonId(Long deliveryPersonId) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getDeliveryPersonId() != null
                && delivery.getDeliveryPersonId().equals(deliveryPersonId))
                .collect(Collectors.toList());
    }

    /**
     * Supprimer une livraison
     */
    public void deleteById(Long id) {
        deliveries.remove(id);
    }

    /**
     * Vérifier si une livraison existe
     */
    public boolean existsById(Long id) {
        return deliveries.containsKey(id);
    }

    /**
     * Vérifier si une livraison existe pour une commande
     */
    public boolean existsByOrderId(Long orderId) {
        return deliveries.values().stream()
                .anyMatch(delivery -> delivery.getOrderId().equals(orderId));
    }
}
