package ahmed.project.spring.menuservice.models;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Représente un plat dans le menu")
public class Dish {

    @Schema(description = "Identifiant unique du plat", example = "1")
    private Long id;

    @Schema(description = "Nom du plat", example = "Pizza Margherita")
    private String name;

    @Schema(description = "Description du plat", example = "Pizza traditionnelle italienne")
    private String description;

    @Schema(description = "Prix du plat", example = "12.50")
    private BigDecimal price;

    @Schema(description = "Catégorie du plat", example = "PIZZA")
    private String category;

    @Schema(description = "Disponibilité du plat")
    private Boolean available;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeurs
    public Dish() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.available = true;
    }

    public Dish(Long id, String name, String description, BigDecimal price, String category) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
