package anthony1.com.order_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'une commande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotBlank(message = "L'ID utilisateur est obligatoire")
    private String userId;

    @NotEmpty(message = "La commande doit contenir au moins un article")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String deliveryAddress;

    @NotBlank(message = "Le nom du client est obligatoire")
    private String customerName;

    @NotBlank(message = "Le téléphone du client est obligatoire")
    private String customerPhone;

    private String notes;
}
