package fr.eat_now.menu.Models;

public class Dish {
    private String id;
    private String name;
    private double price;

    public Dish(
        String id,
        String name,
        double price
    ) {
        setId(id);
        setName(name);
        setPrice(price);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
