package com.achillethomas.order_service.dto;

import java.util.List;

/**
 * Réponse de validation des plats depuis le menu-service
 */
public class DishValidationResponse {
    private boolean valid;
    private List<String> invalidDishIds;
    private String message;

    public DishValidationResponse() {
    }

    public DishValidationResponse(boolean valid, List<String> invalidDishIds, String message) {
        this.valid = valid;
        this.invalidDishIds = invalidDishIds;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getInvalidDishIds() {
        return invalidDishIds;
    }

    public void setInvalidDishIds(List<String> invalidDishIds) {
        this.invalidDishIds = invalidDishIds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
