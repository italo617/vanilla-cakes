package com.vanillacakes.orders;

import com.vanillacakes.LiquibaseRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {
    private static Connection connection;
    private OrderRepository orderRepository;

    @BeforeAll
    static void beforeAll() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/vanilla_db_test",
                "vanilla_test",
                "vanilla_test");

        LiquibaseRunner.run(connection);
        connection.close();

         /*
           New connection for because Liquibase changes the connection auto-commit mode internally,
           which affect the test transaction behavior afterwards.
        */
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5434/vanilla_db_test",
                "vanilla_test",
                "vanilla_test");

        connection.createStatement().executeUpdate("DELETE FROM cakes");
        String sql = """
                INSERT INTO cakes
                (id, name, description, price, active)
                VALUES
                (100, 'Vanilla & Strawberry Cake', 'Everybody loves this cake!', 22.99, true), 
                (200, 'Moist Vanilla Cake', 'Very delicious cake.', 20.99, true),
                (300, 'Fluffy Vanilla Cake', 'For discerning palates', 30.99, true)
                """;
        connection.createStatement().executeUpdate(sql);
    }

    @BeforeEach
    void setUp() throws SQLException {
        orderRepository = new OrderRepository(connection);
        //Ensures the tables are empty before each test
        connection.createStatement().executeUpdate("DELETE FROM order_items");
        connection.createStatement().executeUpdate("DELETE FROM orders");
    }

    @AfterAll
    static void afterAll() throws SQLException {
        //Ensures the tables are empty after each test
        connection.createStatement().executeUpdate("DELETE FROM order_items");
        connection.createStatement().executeUpdate("DELETE FROM orders");
        connection.close();
    }

    @Test
    void shouldSaveOrder() throws SQLException {
        Order order = new Order();

        long orderItem1CakeId = 100L;
        int orderItem1Quantity = 2;
        BigDecimal orderItem1UnitPrice = new BigDecimal("22.99");
        order.getOrderItems().add(createOrderItem(orderItem1CakeId, orderItem1Quantity, orderItem1UnitPrice));

        long orderItem2CakeId = 300L;
        int orderItem2Quantity = 1;
        BigDecimal orderItem2UnitPrice = new BigDecimal("30.99");
        order.getOrderItems().add(createOrderItem(orderItem2CakeId, orderItem2Quantity, orderItem2UnitPrice));

        Order savedOrder = orderRepository.save(order);

        String sql_order = """
                    SELECT created_at 
                    FROM orders
                    WHERE id = ?
                """;

        PreparedStatement orderStatement = connection.prepareStatement(sql_order);
        orderStatement.setLong(1, savedOrder.getId());
        ResultSet orderResultSet = orderStatement.executeQuery();
        orderResultSet.next();
        Timestamp createdAt = orderResultSet.getTimestamp(1);
        assertNotNull(createdAt);

        String sql_order_items = """
                    SELECT cake_id, quantity, unit_price, order_id
                    FROM order_items
                    WHERE order_id = ?
                """;
        PreparedStatement orderItemsStatement = connection.prepareStatement(sql_order_items);
        orderItemsStatement.setLong(1, savedOrder.getId());
        ResultSet orderItemsResultSet = orderItemsStatement.executeQuery();

        Set<OrderItem> obtainedOrderItems = new HashSet<>();

        while (orderItemsResultSet.next()) {
            long obtainedOrderItemCakeId = orderItemsResultSet.getLong(1);
            int obtainedOrderItemQuantity = orderItemsResultSet.getInt(2);
            BigDecimal obtainedOrderItemUnitPrice = orderItemsResultSet.getBigDecimal(3);
            long obtainedOrderItemOrderId = orderItemsResultSet.getLong(4);
            OrderItem orderItem = createOrderItem(obtainedOrderItemCakeId, obtainedOrderItemQuantity,
                    obtainedOrderItemUnitPrice, obtainedOrderItemOrderId);
            obtainedOrderItems.add(orderItem);
        }

        Set<OrderItem> expectedOrderItems = Set.of(
                createOrderItem(orderItem1CakeId, orderItem1Quantity, orderItem1UnitPrice, savedOrder.getId()),
                createOrderItem(orderItem2CakeId, orderItem2Quantity, orderItem2UnitPrice, savedOrder.getId()));
        assertEquals(expectedOrderItems, obtainedOrderItems);
    }

    @Test
    void shouldFindExistingOrderAndItsItems() throws SQLException {
        String sql_order = """
                    INSERT INTO orders
                    (id, created_at)
                    VALUES
                    (100, ?)
                """;
        PreparedStatement orderStatement = connection.prepareStatement(sql_order);
        orderStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        orderStatement.executeUpdate();

        String sql_order_items = """
                    INSERT INTO order_items
                    (order_id, cake_id, quantity, unit_price)
                    VALUES
                    (100, ?, ?, ?),
                    (100, ?, ?, ?),
                    (100, ?, ?, ?)
                """;
        PreparedStatement orderItemsStatement = connection.prepareStatement(sql_order_items);

        long orderItem1CakeId = 100L;
        int orderItem1Quantity = 1;
        BigDecimal orderItem1UnitPrice = new BigDecimal("22.99");
        orderItemsStatement.setLong(1, orderItem1CakeId);
        orderItemsStatement.setInt(2, orderItem1Quantity);
        orderItemsStatement.setBigDecimal(3, orderItem1UnitPrice);

        long orderItem2CakeId = 200L;
        int orderItem2Quantity = 2;
        BigDecimal orderItem2UnitPrice = new BigDecimal("20.99");
        orderItemsStatement.setLong(4, orderItem2CakeId);
        orderItemsStatement.setInt(5, orderItem2Quantity);
        orderItemsStatement.setBigDecimal(6, orderItem2UnitPrice);

        long orderItem3CakeId = 300L;
        int orderItem3Quantity = 3;
        BigDecimal orderItem3UnitPrice = new BigDecimal("30.99");
        orderItemsStatement.setLong(7, orderItem3CakeId);
        orderItemsStatement.setInt(8, orderItem3Quantity);
        orderItemsStatement.setBigDecimal(9, orderItem3UnitPrice);

        orderItemsStatement.executeUpdate();

        Order order = orderRepository.findById(100L);

        Set<OrderItem> expectedOrderItems = Set.of(
                createOrderItem(orderItem1CakeId, orderItem1Quantity, orderItem1UnitPrice, 100L),
                createOrderItem(orderItem2CakeId, orderItem2Quantity, orderItem2UnitPrice, 100L),
                createOrderItem(orderItem3CakeId, orderItem3Quantity, orderItem3UnitPrice, 100L));
        assertEquals(expectedOrderItems, new HashSet<>(order.getOrderItems()));
    }

    @Test
    void shouldReturnNullWhenOrderDoesNotExist() throws SQLException {
        Order nonExistingOrder = orderRepository.findById(999L);
        assertNull(nonExistingOrder);
    }

    private OrderItem createOrderItem(long cakeId, int quantity, BigDecimal unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCakeId(cakeId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        return orderItem;
    }

    private OrderItem createOrderItem(long cakeId, int quantity, BigDecimal unitPrice, long orderId) {
        OrderItem orderItem = createOrderItem(cakeId, quantity, unitPrice);
        orderItem.setOrderId(orderId);
        return orderItem;
    }

}