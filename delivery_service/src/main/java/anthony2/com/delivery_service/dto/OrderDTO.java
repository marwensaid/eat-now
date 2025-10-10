package anthony2.com.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour récupérer les informations d'une commande depuis order_service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String userId;
    private String status;
    private String deliveryAddress;
    private String customerName;
    private String customerPhone;
    private Double totalAmount;
}
