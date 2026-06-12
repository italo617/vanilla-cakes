package com.vanillacakes.orders;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {
    private long id;
    private long orderId;
    private long cakeId;
    private int quantity;
    private BigDecimal unitPrice;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getCakeId() {
        return cakeId;
    }

    public void setCakeId(long cakeId) {
        this.cakeId = cakeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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
