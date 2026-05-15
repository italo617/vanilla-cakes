package com.vanillacakes;

import com.vanillacakes.cakes.Cake;
import com.vanillacakes.cakes.CakeRepository;
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

import static org.junit.jupiter.api.Assertions.*;

class CakeRepositoryTest {

    private static Connection connection;
    private CakeRepository cakeRepository;

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
    }

    @BeforeEach
    void setUp() throws SQLException {
        cakeRepository = new CakeRepository(connection);
        //Ensures the table is empty before each test
        connection.createStatement().executeUpdate("DELETE FROM cakes");
    }

    @AfterAll
    static void afterAll() throws SQLException{
        connection.close();
    }

    @Test
    void shouldSaveCake() throws SQLException {
        Cake cake = new Cake(
                "French Vanilla Cake",
                "Very delicious cake",
                new BigDecimal("14.99"),
                true);

        Long createdCakeId = cakeRepository.save(cake).getId();

        String sql = "SELECT COUNT(*) FROM cakes WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, createdCakeId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);

        assertEquals(1, count);
    }

    @Test
    void shouldFindExistingCake() throws SQLException {
        String sql = """
                    INSERT INTO cakes
                    (id, name, description, price, active)
                    VALUES
                    (100, 'Vanilla & Strawberry Cake', 'Everybody loves this cake!', 22.99, true) 
                    """;
        connection.createStatement().executeUpdate(sql);

        Cake cake = cakeRepository.findById(100L);

        assertNotNull(cake);
        assertEquals(100L, cake.getId());
        assertEquals("Vanilla & Strawberry Cake", cake.getName());
        assertEquals("Everybody loves this cake!", cake.getDescription());
        assertEquals(new BigDecimal("22.99"), cake.getPrice());
        assertTrue(cake.isActive());
    }

    @Test
    void shouldReturnNullWhenCakeDoesNotExist() {
        Cake nonExistingCake = cakeRepository.findById(999L);
        assertNull(nonExistingCake);
    }
}