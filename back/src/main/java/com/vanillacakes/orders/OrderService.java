package com.vanillacakes.orders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private static final int MAX_ITEMS_PER_ORDER = 30;

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        validateOrderItemsQuantity(order);
        Order aggregatedOrder = aggregateOrderItems(order);
        return orderRepository.save(aggregatedOrder);
    }

    private void validateOrderItemsQuantity(Order order) {
        if (order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order without order items");
        }
        int totalOrderItems = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            totalOrderItems += orderItem.getQuantity();
        }
        if (totalOrderItems > MAX_ITEMS_PER_ORDER) {
            throw new IllegalArgumentException("Max items per order exceeded");
        }
    }

    //Visible for testing
    protected Order aggregateOrderItems(Order order) {
        List<OrderItem> unorderedItems = order.getOrderItems();
        Map<Long, OrderItem> itemsByCakeId = new LinkedHashMap<>();
        for (OrderItem item : unorderedItems) {
            OrderItem existing = itemsByCakeId.get(item.getCakeId());

            if (existing == null) {
                itemsByCakeId.put(item.getCakeId(), item);
            } else {
                itemsByCakeId.put(item.getCakeId(), existing.updateQuantity(item.getQuantity()));
            }
        }
        Order aggregatedOrder = order.copy();
        aggregatedOrder.setOrderItems(new ArrayList<>(itemsByCakeId.values()));
        return aggregatedOrder;
    }
}
