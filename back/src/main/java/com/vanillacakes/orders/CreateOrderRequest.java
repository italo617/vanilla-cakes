package com.vanillacakes.orders;

import java.util.List;

public record CreateOrderRequest(List<OrderItem> orderItems) {

}
