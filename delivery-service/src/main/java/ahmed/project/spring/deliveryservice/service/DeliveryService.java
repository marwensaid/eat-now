package ahmed.project.spring.deliveryservice.service;

import ahmed.project.spring.deliveryservice.client.OrderClient;
import ahmed.project.spring.deliveryservice.dto.CreateDeliveryRequest;
import ahmed.project.spring.deliveryservice.model.Delivery;
import ahmed.project.spring.deliveryservice.model.DeliveryStatus;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DeliveryService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);
    private final Map<Long, Delivery> deliveries = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final OrderClient orderClient;

    public DeliveryService(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @CircuitBreaker(name = "deliveryService", fallbackMethod = "createDeliveryFallback")
    @Retry(name = "deliveryService")
    @TimeLimiter(name = "deliveryService")
    public CompletableFuture<Delivery> createDelivery(CreateDeliveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Creating delivery for order: {}", request.getOrderId());

            // Vérifier que la commande existe
            try {
                Map<String, Object> order = orderClient.getOrderById(request.getOrderId());
                if (order == null) {
                    throw new RuntimeException("Order not found");
                }
                logger.info("Order {} found, creating delivery", request.getOrderId());
            } catch (Exception e) {
                throw new RuntimeException("Cannot verify order " + request.getOrderId(), e);
            }

            Delivery delivery = new Delivery();
            delivery.setId(idGenerator.getAndIncrement());
            delivery.setOrderId(request.getOrderId());
            delivery.setDeliveryAddress(request.getDeliveryAddress());
            delivery.setEstimatedTimeMinutes(
                    request.getEstimatedTimeMinutes() != null ? request.getEstimatedTimeMinutes() : 30
            );

            deliveries.put(delivery.getId(), delivery);
            logger.info("Delivery created successfully: {}", delivery.getId());

            // Mettre à jour le statut de la commande
            try {
                orderClient.updateOrderStatus(request.getOrderId(), "READY_FOR_DELIVERY");
            } catch (Exception e) {
                logger.warn("Cannot update order status: {}", e.getMessage());
            }

            return delivery;
        });
    }

    private CompletableFuture<Delivery> createDeliveryFallback(CreateDeliveryRequest request, Throwable ex) {
        logger.error("Fallback: Unable to create delivery for order {}: {}", request.getOrderId(), ex.getMessage());

        // Ici, on peut choisir de ne pas créer de livraison si l'ordre est introuvable
        Delivery delivery = new Delivery();
        delivery.setId(idGenerator.getAndIncrement());
        delivery.setOrderId(request.getOrderId());
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setEstimatedTimeMinutes(30);
        delivery.setStatus(DeliveryStatus.FAILED); // marquer la livraison comme échouée

        deliveries.put(delivery.getId(), delivery);
        return CompletableFuture.completedFuture(delivery);
    }

    public Optional<Delivery> getDeliveryByOrderId(Long orderId) {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getOrderId().equals(orderId))
                .findFirst();
    }

    public Optional<Delivery> getDeliveryById(Long id) {
        return Optional.ofNullable(deliveries.get(id));
    }

    public List<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveries.values());
    }

    public Optional<Delivery> assignDriver(Long id, String driverId, String driverName) {
        Delivery delivery = deliveries.get(id);
        if (delivery == null) {
            return Optional.empty();
        }

        delivery.setDriverId(driverId);
        delivery.setDriverName(driverName);
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        logger.info("Driver {} assigned to delivery {}", driverId, id);
        return Optional.of(delivery);
    }

    public Optional<Delivery> updateDeliveryStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveries.get(id);
        if (delivery == null) {
            return Optional.empty();
        }

        delivery.setStatus(status);
        logger.info("Delivery {} status updated to {}", id, status);

        if (status == DeliveryStatus.DELIVERED) {
            try {
                orderClient.updateOrderStatus(delivery.getOrderId(), "DELIVERED");
            } catch (Exception e) {
                logger.warn("Cannot update order status to DELIVERED: {}", e.getMessage());
            }
        }

        return Optional.of(delivery);
    }

    public Optional<Delivery> updateLocation(Long id, String location) {
        Delivery delivery = deliveries.get(id);
        if (delivery == null) {
            return Optional.empty();
        }

        delivery.setCurrentLocation(location);
        logger.info("Delivery {} location updated to {}", id, location);
        return Optional.of(delivery);
    }
}

