package com.vanillacakes.cakes;

import java.math.BigDecimal;

public class Cake {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;

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
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Cake() {
        //Default constructor
    }

    public Cake(Long id, String name, String description, BigDecimal price, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = active;
    }

    @Override
    public String toString() {
        return "Cake{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", active=" + active +
                '}';
    }
}
