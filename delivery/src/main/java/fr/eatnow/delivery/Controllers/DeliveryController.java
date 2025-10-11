package fr.eatnow.delivery.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @GetMapping
    public String getDeliveries() {
        return "List of deliveries";
    }
}
