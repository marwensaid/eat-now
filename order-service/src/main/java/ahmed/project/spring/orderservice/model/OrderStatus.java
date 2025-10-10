package ahmed.project.spring.orderservice.model;


public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PREPARING,
    READY_FOR_DELIVERY,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}
