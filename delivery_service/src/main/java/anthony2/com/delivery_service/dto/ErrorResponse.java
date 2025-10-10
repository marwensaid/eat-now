package anthony2.com.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO pour les réponses d'erreur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;
}
