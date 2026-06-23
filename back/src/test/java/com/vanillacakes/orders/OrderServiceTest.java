package com.vanillacakes.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldCreateOrder() {
        Order order = new Order();
        OrderItem orderItem1 = new OrderItem(1L, 2, new BigDecimal("10.00"));
        OrderItem orderItem2 = new OrderItem(2L, 5, new BigDecimal("20.00"));
        order.setOrderItems(new ArrayList<>(List.of(orderItem1, orderItem2)));

        long orderId = 99L;
        when(orderRepository.save(ArgumentMatchers.any(Order.class))).thenReturn(fakeSave(order, orderId));

        Order createdOrder = orderService.createOrder(order);
        assertEquals(orderId, createdOrder.getId());
        OrderItem expectedOrderItem1 = new OrderItem(orderId, 1L, 2, new BigDecimal("10.00"));
        OrderItem expectedOrderItem2 = new OrderItem(orderId, 2L, 5, new BigDecimal("20.00"));
        assertEquals(Set.of(expectedOrderItem1, expectedOrderItem2), new HashSet<>(createdOrder.getOrderItems()));
    }

    @Test
    public void shouldNotCreateOrderWithoutItems() {
        Order order = new Order();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Cannot create order without order items", exception.getMessage());
    }

    @Test
    public void shouldNotCreateOrderThatSurpass30ItemsQuantityLimit() {
        Order order = new Order();
        OrderItem orderItem1 = new OrderItem(1L, 3, new BigDecimal("10.00"));
        OrderItem orderItem2 = new OrderItem(2L, 10, new BigDecimal("20.00"));
        OrderItem orderItem3 = new OrderItem(3L, 18, new BigDecimal("30.00"));
        order.setOrderItems(new ArrayList<>(List.of(orderItem1, orderItem2, orderItem3)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Max items per order exceeded", exception.getMessage());
    }

    @Test
    public void shouldAggregateOrderItemsWithSameCakeId() {
        Order order = new Order();
        OrderItem orderItem1 = new OrderItem(1L, 3, new BigDecimal("10.00"));
        OrderItem orderItem2 = new OrderItem(2L, 4, new BigDecimal("20.00"));
        OrderItem orderItem3 = new OrderItem(1L, 5, new BigDecimal("10.00"));
        order.setOrderItems(new ArrayList<>(List.of(orderItem1, orderItem2, orderItem3)));

        Order aggregateOrder = orderService.aggregateOrderItems(order);
        OrderItem expectedOrderItem1 = new OrderItem(1L, 8, new BigDecimal("10.00"));
        OrderItem expectedOrderItem2 = new OrderItem(2L, 4, new BigDecimal("20.00"));
        assertEquals(Set.of(expectedOrderItem1, expectedOrderItem2), new HashSet<>(aggregateOrder.getOrderItems()));
    }

    private Order fakeSave(Order order, long orderId) {
        Order savedOrder = order.copy();
        savedOrder.setId(orderId);
        savedOrder.setOrderItems(new ArrayList<>());

        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem oldOrderItem = order.getOrderItems().get(i);
            OrderItem newOrderItem = new OrderItem(i+1, orderId, oldOrderItem.getCakeId(), oldOrderItem.getQuantity(),
                    oldOrderItem.getUnitPrice());
            savedOrder.getOrderItems().add(newOrderItem);
        }

        return savedOrder;
    }
}