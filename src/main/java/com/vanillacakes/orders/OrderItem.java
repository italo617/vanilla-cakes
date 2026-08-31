package com.vanillacakes.orders;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {
    private long id;
    private long orderId;
    private long cakeId;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem(long cakeId, int quantity, BigDecimal unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be positive");
        }

        this.cakeId = cakeId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderItem(long orderId, long cakeId, int quantity, BigDecimal unitPrice) {
        this(cakeId, quantity, unitPrice);
        this.orderId = orderId;
    }

    public OrderItem(long id, long orderId, long cakeId, int quantity, BigDecimal unitPrice) {
        this(orderId, cakeId, quantity, unitPrice);
        this.id = id;
    }

    public OrderItem() {
        //Required by Jackson for deserialization
    }

    public long getId() {
        return id;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getCakeId() {
        return cakeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderItem updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be positive");
        }
        int updatedQuantity = this.quantity + quantity;
        return new OrderItem(this.id, this.orderId, this.cakeId, updatedQuantity, this.unitPrice);
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /*
    Equality intentionally ignores database id.
    Used for comparing business content of order items.
    */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return orderId == orderItem.orderId && cakeId == orderItem.cakeId && quantity == orderItem.quantity && Objects.equals(unitPrice, orderItem.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, cakeId, quantity, unitPrice);
    }
}
