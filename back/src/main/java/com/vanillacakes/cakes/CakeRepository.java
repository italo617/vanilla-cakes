package com.vanillacakes.cakes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public Cake save(Cake cake) {
        String sql = """
                    INSERT INTO cakes (
                       name,
                       description,
                       price,
                       active
                    ) VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, cake.getName());
            statement.setString(2, cake.getDescription());
            statement.setBigDecimal(3, cake.getPrice());
            statement.setBoolean(4, cake.isActive());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (!keys.next()) {
                throw new RuntimeException("Could not get cake id");
            }

            Long generatedId = keys.getLong(1);
            return this.findById(generatedId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
