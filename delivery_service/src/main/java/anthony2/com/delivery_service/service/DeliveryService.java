package anthony2.com.delivery_service.service;

import anthony2.com.delivery_service.client.OrderServiceClient;
import anthony2.com.delivery_service.dto.*;
import anthony2.com.delivery_service.exception.DeliveryNotFoundException;
import anthony2.com.delivery_service.exception.InvalidDeliveryStatusException;
import anthony2.com.delivery_service.exception.OrderNotFoundException;
import anthony2.com.delivery_service.model.Delivery;
import anthony2.com.delivery_service.model.DeliveryStatus;
import anthony2.com.delivery_service.repository.DeliveryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des livraisons
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderServiceClient orderServiceClient;

    /**
     * Créer une nouvelle livraison pour une commande
     */
    @CircuitBreaker(name = "deliveryService", fallbackMethod = "createDeliveryFallback")
    @Retry(name = "deliveryService")
    @TimeLimiter(name = "deliveryService")
    public Mono<DeliveryResponse> createDelivery(DeliveryRequest request) {
        log.info("Création d'une livraison pour la commande {}", request.getOrderId());

        // Vérifier que la commande existe
        return orderServiceClient.getOrder(request.getOrderId())
                .flatMap(orderDTO -> {
                    if (orderDTO == null || orderDTO.getId() == null) {
                        return Mono.error(new OrderNotFoundException(request.getOrderId()));
                    }

                    // Vérifier qu'une livraison n'existe pas déjà pour cette commande
                    if (deliveryRepository.existsByOrderId(request.getOrderId())) {
                        return Mono.error(new InvalidDeliveryStatusException(
                                "Une livraison existe déjà pour la commande " + request.getOrderId()));
                    }

                    // Créer la livraison
                    Delivery delivery = Delivery.builder()
                            .orderId(request.getOrderId())
                            .status(DeliveryStatus.PENDING)
                            .deliveryAddress(request.getDeliveryAddress())
                            .customerName(request.getCustomerName())
                            .customerPhone(request.getCustomerPhone())
                            .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                            .notes(request.getNotes())
                            .build();

                    Delivery savedDelivery = deliveryRepository.save(delivery);
                    log.info("Livraison créée avec succès: {}", savedDelivery.getId());

                    return Mono.just(mapToResponse(savedDelivery));
                });
    }

    /**
     * Récupérer une livraison par ID
     */
    public DeliveryResponse getDeliveryById(Long id) {
        log.info("Récupération de la livraison {}", id);

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));

        return mapToResponse(delivery);
    }

    /**
     * Récupérer une livraison par ID de commande
     */
    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        log.info("Récupération de la livraison pour la commande {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(orderId));

        return mapToResponse(delivery);
    }

    /**
     * Récupérer toutes les livraisons
     */
    public List<DeliveryResponse> getAllDeliveries() {
        log.info("Récupération de toutes les livraisons");

        return deliveryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les livraisons par statut
     */
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        log.info("Récupération des livraisons avec le statut {}", status);

        return deliveryRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les livraisons d'un livreur
     */
    public List<DeliveryResponse> getDeliveriesByDeliveryPerson(Long deliveryPersonId) {
        log.info("Récupération des livraisons du livreur {}", deliveryPersonId);

        return deliveryRepository.findByDeliveryPersonId(deliveryPersonId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Assigner un livreur à une livraison
     */
    public DeliveryResponse assignDeliveryPerson(Long deliveryId, AssignDeliveryPersonRequest request) {
        log.info("Assignation du livreur {} à la livraison {}",
                request.getDeliveryPersonId(), deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        // Vérifier que la livraison est en attente
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new InvalidDeliveryStatusException(
                    "Impossible d'assigner un livreur. La livraison doit être en statut PENDING");
        }

        // Assigner le livreur
        delivery.setDeliveryPersonId(request.getDeliveryPersonId());
        delivery.setDeliveryPersonName(request.getDeliveryPersonName());
        delivery.setDeliveryPersonPhone(request.getDeliveryPersonPhone());
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Livreur assigné avec succès à la livraison {}", deliveryId);

        return mapToResponse(updatedDelivery);
    }

    /**
     * Mettre à jour le statut d'une livraison
     */
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatusUpdateRequest request) {
        log.info("Mise à jour du statut de la livraison {} vers {}", deliveryId, request.getStatus());

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        DeliveryStatus currentStatus = delivery.getStatus();
        DeliveryStatus newStatus = request.getStatus();

        // Valider la transition de statut
        validateStatusTransition(currentStatus, newStatus);

        // Mettre à jour le statut
        delivery.setStatus(newStatus);

        // Mettre à jour la localisation si fournie
        if (request.getCurrentLocation() != null) {
            delivery.setCurrentLocation(request.getCurrentLocation());
        }

        // Mettre à jour les timestamps selon le statut
        switch (newStatus) {
            case PICKED_UP:
                delivery.setPickupTime(LocalDateTime.now());
                break;
            case DELIVERED:
                delivery.setDeliveredAt(LocalDateTime.now());
                break;
            default:
                break;
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Statut de la livraison {} mis à jour vers {}", deliveryId, newStatus);

        return mapToResponse(updatedDelivery);
    }

    /**
     * Valider la transition de statut
     */
    private void validateStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        // Une livraison livrée ne peut plus changer de statut
        if (currentStatus == DeliveryStatus.DELIVERED) {
            throw new InvalidDeliveryStatusException("Impossible de modifier le statut d'une livraison déjà livrée");
        }

        // Une livraison annulée ne peut plus changer de statut (sauf reactivation)
        if (currentStatus == DeliveryStatus.CANCELLED && newStatus != DeliveryStatus.PENDING) {
            throw new InvalidDeliveryStatusException("Impossible de modifier le statut d'une livraison annulée");
        }

        // Une livraison échouée ne peut plus changer de statut (sauf reactivation)
        if (currentStatus == DeliveryStatus.FAILED && newStatus != DeliveryStatus.PENDING) {
            throw new InvalidDeliveryStatusException("Impossible de modifier le statut d'une livraison échouée");
        }

        // Vérifier l'ordre logique des statuts
        if (currentStatus == DeliveryStatus.PENDING
                && newStatus != DeliveryStatus.ASSIGNED
                && newStatus != DeliveryStatus.CANCELLED) {
            throw new InvalidDeliveryStatusException(
                    "Une livraison PENDING doit passer à ASSIGNED ou CANCELLED");
        }

        if (currentStatus == DeliveryStatus.ASSIGNED
                && newStatus != DeliveryStatus.PICKED_UP
                && newStatus != DeliveryStatus.CANCELLED) {
            throw new InvalidDeliveryStatusException(
                    "Une livraison ASSIGNED doit passer à PICKED_UP ou CANCELLED");
        }

        if (currentStatus == DeliveryStatus.PICKED_UP
                && newStatus != DeliveryStatus.IN_TRANSIT
                && newStatus != DeliveryStatus.CANCELLED
                && newStatus != DeliveryStatus.FAILED) {
            throw new InvalidDeliveryStatusException(
                    "Une livraison PICKED_UP doit passer à IN_TRANSIT, CANCELLED ou FAILED");
        }

        if (currentStatus == DeliveryStatus.IN_TRANSIT
                && newStatus != DeliveryStatus.DELIVERED
                && newStatus != DeliveryStatus.FAILED) {
            throw new InvalidDeliveryStatusException(
                    "Une livraison IN_TRANSIT doit passer à DELIVERED ou FAILED");
        }
    }

    /**
     * Mapper une entité Delivery vers DeliveryResponse
     */
    private DeliveryResponse mapToResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .deliveryPersonId(delivery.getDeliveryPersonId())
                .deliveryPersonName(delivery.getDeliveryPersonName())
                .deliveryPersonPhone(delivery.getDeliveryPersonPhone())
                .customerName(delivery.getCustomerName())
                .customerPhone(delivery.getCustomerPhone())
                .currentLocation(delivery.getCurrentLocation())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .deliveredAt(delivery.getDeliveredAt())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }

    /**
     * Fallback pour createDelivery
     */
    private Mono<DeliveryResponse> createDeliveryFallback(DeliveryRequest request, Throwable throwable) {
        log.error("Fallback activé pour createDelivery: {}", throwable.getMessage());
        return Mono.error(new RuntimeException("Service de livraison temporairement indisponible"));
    }
}
