package com.vanillacakes.cakes;

import com.vanillacakes.PagedResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    public PagedResult<Cake> findCakes(int pageNumber, int pageSize) {
        String sql = """
            SELECT id, name, description, price, active
            FROM cakes
            WHERE active IS TRUE
            ORDER BY id
            LIMIT ?
            OFFSET ?
            """;

        String countSql = """
            SELECT COUNT(*)
            FROM cakes
            WHERE active IS TRUE
        """;


        int offset = (pageNumber - 1) * pageSize;
        try (PreparedStatement statement =
                     connection.prepareStatement(sql);

             PreparedStatement countStatement =
                     connection.prepareStatement(countSql)
        ) {

            statement.setInt(1, pageSize);
            statement.setInt(2, offset);

            ResultSet resultSet = statement.executeQuery();
            List<Cake> cakes = new ArrayList<>();
            while (resultSet.next()) {

                Cake cake = new Cake();
                cake.setId(resultSet.getLong("id"));
                cake.setName(resultSet.getString("name"));
                cake.setDescription(resultSet.getString("description"));
                cake.setPrice(resultSet.getBigDecimal("price"));
                cake.setActive(resultSet.getBoolean("active"));

                cakes.add(cake);
            }

            ResultSet countResultSet = countStatement.executeQuery();
            countResultSet.next();
            long totalElements = countResultSet.getLong(1);
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);

            return new PagedResult<>(cakes, pageNumber, pageSize, totalElements, totalPages);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
