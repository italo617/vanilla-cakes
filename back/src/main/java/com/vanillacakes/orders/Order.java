package com.vanillacakes.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private LocalDateTime createdAt;
    private List<OrderItem> orderItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Method to copy all attributes from an order object to another. This method doesn't clone order items.
     */
    public Order copy() {
        Order clone = new Order();
        clone.setId(this.getId());
        clone.setCreatedAt(this.getCreatedAt());
        clone.setOrderItems(new ArrayList<>(this.getOrderItems()));
        return clone;
    }
}
