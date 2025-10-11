package fr.eatnow.order.Controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping
    public Map<String, List> all() {
        return Map.of("data", new ArrayList<>());
    }

//    @GetMapping()
//   @Retry(name = "backendRetry", fallbackMethod = "fallbackResponse")
//    @CircuitBreaker(name = "backendCircuitBreaker", fallbackMethod = "fallbackResponse")
//    @TimeLimiter(name = "backendTimeout")
//    public CompletableFuture<String> getData() {
//
//    }

}
