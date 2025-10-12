package fr.eatnow.order.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Modèle d'une commande")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class Order {
    @NotNull(message = "L'ID ne peut pas être null")
    @UUID
    private String id;

    @NotBlank(message = "L'email du client est obligatoire")
    @Email(message = "Email invalide")
    @Schema(description = "Email du client", example = "client@example.com")
    private String customerEmail;

    @NotEmpty(message = "La commande doit contenir au moins un plat")
    @Size(min = 1, max = 50, message = "Maximum 50 plats par commande")
    private List<OrderItem> items;

    @NotNull(message = "Le total est obligatoire")
    @Positive
    @DecimalMin(value = "0.01")
    @Schema(description = "Total de la commande en euros")
    private Double totalAmount;

    @NotNull
    @Schema(description = "Statut de la commande", example = "CREATED")
    private OrderStatus status;

    @NotNull
    private LocalDateTime createdAt;

    public enum OrderStatus {
        CREATED, CONFIRMED, PREPARING, READY, DELIVERED, CANCELLED
    }
}