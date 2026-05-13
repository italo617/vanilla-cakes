package com.vanillacakes.cakes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CakeRepository {

    private final Connection connection;

    public CakeRepository(Connection connection) {
        this.connection = connection;
    }

    public Cake findById(Long id) {
        String sql = """
                    SELECT id, name, description, price, active
                    FROM cakes
                    WHERE id = ?         
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            Cake cake = new Cake();
            cake.setId(resultSet.getLong("id"));
            cake.setName(resultSet.getString("name"));
            cake.setDescription(resultSet.getString("description"));
            cake.setPrice(resultSet.getBigDecimal("price"));
            cake.setActive(resultSet.getBoolean("active"));

            return cake;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Cake cake) {
        String sql = """
                    INSERT INTO cakes (
                       id,
                       name,
                       description,
                       price,
                       active
                    ) VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cake.getId());
            statement.setString(2, cake.getName());
            statement.setString(3, cake.getDescription());
            statement.setBigDecimal(4, cake.getPrice());
            statement.setBoolean(5, cake.isActive());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
