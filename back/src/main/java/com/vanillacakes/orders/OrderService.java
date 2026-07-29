package com.vanillacakes.orders;

import com.vanillacakes.transactions.TransactionManager;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private static final int MAX_ITEMS_PER_ORDER = 30;

    private final TransactionManager transactionManager;

    public OrderService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Order createOrder(Order order) {
        validateOrderItemsQuantity(order);
        validateCustomerInformation(order);
        Order aggregatedOrder = aggregateOrderItems(order);
        return transactionManager.execute(connection -> {
            OrderRepository repository = new OrderRepository(connection);
            return repository.save(aggregatedOrder);
        });
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

    private void validateCustomerInformation(Order order) {
        if (StringUtils.isBlank(order.getClientName())) {
            throw new IllegalArgumentException("Client name cannot be empty");
        }
        if (StringUtils.isBlank(order.getFullAddress())) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        if (order.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method cannot be empty");
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
