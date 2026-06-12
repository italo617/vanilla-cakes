package com.vanillacakes.orders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OrderRepository {

    private final Connection connection;

    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    public Order findById(Long id) {
        String sql_order = """
                    SELECT id, created_at
                    FROM orders
                    WHERE id = ?
                """;

        Order order = new Order();
        try (PreparedStatement statement = connection.prepareStatement(sql_order)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            order.setId(resultSet.getLong(1));
            Timestamp createdAtTimestamp = resultSet.getTimestamp(2);
            order.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql_order_items = """
                    SELECT id, cake_id, quantity, unit_price
                    FROM order_items
                    WHERE order_id = ?
                """;


        try (PreparedStatement statement = connection.prepareStatement(sql_order_items)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(resultSet.getLong(1));
                orderItem.setOrderId(id);
                orderItem.setCakeId(resultSet.getLong(2));
                orderItem.setQuantity(resultSet.getInt(3));
                orderItem.setUnitPrice(resultSet.getBigDecimal(4));

                order.getOrderItems().add(orderItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return order;
    }

    // TODO: execute save inside a transaction
    public Order save(Order order) {
        String sql_order = """
                    INSERT INTO orders
                    (created_at)
                    VALUES
                    (?)
                """;

        long orderId;
        try (PreparedStatement statement = connection.prepareStatement(sql_order,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, LocalDateTime.now());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (!keys.next()) {
                throw new RuntimeException("Could not get order id");
            }

            orderId = keys.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql_order_item = """
                    INSERT INTO order_items
                    (order_id, cake_id, quantity, unit_price)
                    VALUES
                    (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql_order_item)) {
            for (OrderItem orderItem : order.getOrderItems()) {

                statement.setLong(1, orderId);
                statement.setLong(2, orderItem.getCakeId());
                statement.setLong(3, orderItem.getQuantity());
                statement.setBigDecimal(4, orderItem.getUnitPrice());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return findById(orderId);
    }
}
