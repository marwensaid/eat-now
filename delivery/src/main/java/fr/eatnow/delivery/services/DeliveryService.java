package fr.eatnow.delivery.services;

import fr.eatnow.delivery.clients.OrderServiceClient;
import fr.eatnow.delivery.dtos.AssignDeliveryRequest;
import fr.eatnow.delivery.dtos.CreateDeliveryRequest;
import fr.eatnow.delivery.models.Delivery;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final OrderServiceClient orderClient;
    private final Map<String, Delivery> deliveries = new ConcurrentHashMap<>();

    /**
     * Créer une nouvelle livraison pour une commande
     */
    public Delivery createDelivery(CreateDeliveryRequest request) {
        log.info("🚚 Création livraison pour commande {} à l'adresse {}",
                request.getOrderId(), request.getDeliveryAddress());

        // Vérifier que la commande existe et est prête (statut READY)
        var orderInfo = orderClient.getOrderForDelivery(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Commande " + request.getOrderId() + " introuvable ou pas prête pour livraison"));

        if (!"READY".equals(orderInfo.getStatus())) {
            throw new IllegalStateException(
                    "Commande " + request.getOrderId() + " n'est pas prête pour livraison (statut: " +
                            orderInfo.getStatus() + ")");
        }

        // Pré-remplir l'adresse depuis la commande si disponible
        String address = orderInfo.getDeliveryAddress() != null ?
                orderInfo.getDeliveryAddress() : request.getDeliveryAddress();

        String deliveryId = UUID.randomUUID().toString();
        Delivery delivery = Delivery.builder()
                .id(deliveryId)
                .orderId(request.getOrderId())
                .status(Delivery.DeliveryStatus.PENDING)
                .deliveryAddress(address)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        deliveries.put(deliveryId, delivery);
        log.info("✅ Livraison {} créée pour commande {}", deliveryId, request.getOrderId());

        return delivery;
    }

    /**
     * Assigner un livreur à une livraison
     */
    public Delivery assignDelivery(String deliveryId, AssignDeliveryRequest request) {
        log.info("👤 Assignation livreur {} à la livraison {}",
                request.getDeliveryPerson(), deliveryId);

        Delivery delivery = getDeliveryById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Livraison " + deliveryId + " introuvable"));

        // Parser l'estimation d'arrivée
        LocalDateTime estimatedArrival;
        try {
            estimatedArrival = LocalDateTime.parse(request.getEstimatedArrival(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            throw new IllegalArgumentException("Format d'estimation d'arrivée invalide: " +
                    request.getEstimatedArrival());
        }

        // Vérifier que la livraison est dans le bon état
        if (delivery.getStatus() != Delivery.DeliveryStatus.PENDING) {
            throw new IllegalStateException(
                    "Livraison " + deliveryId + " ne peut pas être assignée (statut: " +
                            delivery.getStatus() + ")");
        }

        delivery.setDeliveryPerson(request.getDeliveryPerson());
        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
        delivery.setEstimatedArrival(estimatedArrival);
        delivery.setUpdatedAt(LocalDateTime.now());

        deliveries.put(deliveryId, delivery);
        log.info("✅ Livreur {} assigné à la livraison {}",
                request.getDeliveryPerson(), deliveryId);

        return delivery;
    }

    /**
     * Mettre à jour le statut de livraison
     */
    public Delivery updateDeliveryStatus(String deliveryId, Delivery.DeliveryStatus newStatus) {
        log.info("🔄 Mise à jour statut livraison {} : {} → {}",
                deliveryId, getDeliveryById(deliveryId).map(Delivery::getStatus).orElse(null), newStatus);

        Delivery delivery = getDeliveryById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Livraison " + deliveryId + " introuvable"));

        // Transitions valides
        switch (newStatus) {
            case IN_TRANSIT -> {
                if (delivery.getStatus() != Delivery.DeliveryStatus.ASSIGNED) {
                    throw new IllegalStateException("Livraison doit être ASSIGNED pour passer IN_TRANSIT");
                }
            }
            case DELIVERED -> {
                if (delivery.getStatus() != Delivery.DeliveryStatus.IN_TRANSIT) {
                    throw new IllegalStateException("Livraison doit être IN_TRANSIT pour passer DELIVERED");
                }
            }
            default -> throw new IllegalArgumentException("Statut invalide: " + newStatus);
        }

        delivery.setStatus(newStatus);
        delivery.setUpdatedAt(LocalDateTime.now());

        deliveries.put(deliveryId, delivery);

        if (newStatus == Delivery.DeliveryStatus.DELIVERED) {
            log.info("🎉 Livraison {} livrée avec succès !", deliveryId);
        }

        return delivery;
    }

    /**
     * Récupérer toutes les livraisons prêtes à assigner (PENDING)
     */
    public List<Delivery> getPendingDeliveries() {
        return deliveries.values().stream()
                .filter(delivery -> delivery.getStatus() == Delivery.DeliveryStatus.PENDING)
                .sorted(Comparator.comparing(Delivery::getCreatedAt))
                .toList();
    }

    /**
     * Récupérer les livraisons d'un livreur
     */
    public List<Delivery> getDeliveriesByPerson(String deliveryPerson) {
        return deliveries.values().stream()
                .filter(delivery -> deliveryPerson.equals(delivery.getDeliveryPerson()))
                .filter(delivery -> delivery.getStatus() != Delivery.DeliveryStatus.DELIVERED)
                .sorted(Comparator.comparing(Delivery::getUpdatedAt).reversed())
                .toList();
    }

    /**
     * Récupérer une livraison par ID
     */
    public Optional<Delivery> getDeliveryById(String deliveryId) {
        return Optional.ofNullable(deliveries.get(deliveryId));
    }

    /**
     * Récupérer toutes les livraisons
     */
    public List<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveries.values());
    }

    /**
     * Supprimer une livraison (pour les cas d'erreur)
     */
    public boolean cancelDelivery(String deliveryId) {
        log.info("❌ Annulation livraison {}", deliveryId);
        Delivery delivery = deliveries.get(deliveryId);
        if (delivery != null) {
            delivery.setStatus(Delivery.DeliveryStatus.CANCELLED);
            delivery.setUpdatedAt(LocalDateTime.now());
            deliveries.put(deliveryId, delivery);
            return true;
        }
        return false;
    }

    /**
     * Statistiques de livraison
     */
    public DeliveryStats getDeliveryStats() {
        long totalDeliveries = deliveries.size();
        long pending = deliveries.values().stream()
                .filter(d -> d.getStatus() == Delivery.DeliveryStatus.PENDING).count();
        long assigned = deliveries.values().stream()
                .filter(d -> d.getStatus() == Delivery.DeliveryStatus.ASSIGNED).count();
        long inTransit = deliveries.values().stream()
                .filter(d -> d.getStatus() == Delivery.DeliveryStatus.IN_TRANSIT).count();
        long delivered = deliveries.values().stream()
                .filter(d -> d.getStatus() == Delivery.DeliveryStatus.DELIVERED).count();

        return DeliveryStats.builder()
                .totalDeliveries((int) totalDeliveries)
                .pending((int) pending)
                .assigned((int) assigned)
                .inTransit((int) inTransit)
                .delivered((int) delivered)
                .successRate(delivered > 0 ? (double) delivered / totalDeliveries * 100 : 0)
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryStats {
        private int totalDeliveries;
        private int pending;
        private int assigned;
        private int inTransit;
        private int delivered;
        private double successRate;  // Pourcentage
    }
}