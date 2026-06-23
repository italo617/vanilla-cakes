package com.vanillacakes.orders;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class OrderController extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Order createdOrder;
        try {
            CreateOrderRequest createOrderRequest =
                    mapper.readValue(req.getInputStream(), CreateOrderRequest.class);

            Order order = new Order();
            order.setOrderItems(createOrderRequest.orderItems());
            createdOrder = orderService.createOrder(order);
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
            return;
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        String createdOrderJson = mapper.writeValueAsString(createdOrder);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(createdOrderJson);
    }
}
