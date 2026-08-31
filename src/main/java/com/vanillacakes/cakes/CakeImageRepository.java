package com.vanillacakes.cakes;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CakeImageRepository {

    private final Connection connection;

    public CakeImageRepository(Connection connection) {
        this.connection = connection;
    }

    public CakeImageContent findByCakeId(Long cakeId) {
        String sql = """
                    SELECT mime_type, content 
                    FROM cake_images
                    WHERE cake_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cakeId);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return  new CakeImageContent(resultSet.getString(1),
                    new ByteArrayInputStream(resultSet.getBytes(2)));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Must be authorized
    public long save(CakeImage cakeImage) {

        String sql = """
                   INSERT INTO cake_images
                   (cake_id, mime_type, content)
                   VALUES
                   (?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, cakeImage.cakeId());
            statement.setString(2, cakeImage.mimeType());
            statement.setBytes(3, cakeImage.content());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (!keys.next()) {
                throw new SQLException("Could not save cake Image");
            }
            //Returns cakeImage ID
            return keys.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
