package fr.eatnow.delivery.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Informations d'une commande pour la livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfo {

    private String id;

    @Schema(description = "Email du client")
    private String customerEmail;

    @Schema(description = "Adresse de livraison")
    private String deliveryAddress;

    @Schema(description = "Détails des articles (plats)")
    private List<OrderItemInfo> items;

    @Schema(description = "Total de la commande")
    private Double totalAmount;

    @Schema(description = "Statut de la commande")
    private String status;

    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemInfo {
        private String dishId;
        private String dishName;
        private Integer quantity;
        private Double unitPrice;
    }
}