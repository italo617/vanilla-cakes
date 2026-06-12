package com.vanillacakes.cakes;

import com.vanillacakes.LiquibaseRunner;
import com.vanillacakes.PagedResult;
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

    @Test
    void shouldFindActiveCakesWithPagination() throws SQLException {
        insertCakePaginationScenario();

        PagedResult<Cake> cakes =
                cakeRepository.findCakes(1, 2);
        assertEquals(2, cakes.content().size());
        assertEquals(1, cakes.page());
        assertEquals(2, cakes.pageSize());
        assertEquals(3, cakes.totalElements());
        assertEquals(2, cakes.totalPages());
        Cake firstCake = cakes.content().getFirst();

        assertEquals(1L, firstCake.getId());
        assertEquals("French Vanilla Cake", firstCake.getName());
        assertEquals("Classic vanilla cake with a smooth and rich flavor.", firstCake.getDescription());
        assertEquals(new BigDecimal("10.00"), firstCake.getPrice());
        assertTrue(firstCake.isActive());

        Cake secondCake = cakes.content().get(1);
        assertEquals(3L, secondCake.getId());
        assertEquals("Vanilla Cream Cake", secondCake.getName());
        assertEquals("Light vanilla cake filled with creamy vanilla frosting.", secondCake.getDescription());
        assertEquals(new BigDecimal("15.00"), secondCake.getPrice());
        assertTrue(secondCake.isActive());
    }

    @Test
    void shouldReturnSecondPageOfActiveCakes() throws SQLException {
        insertCakePaginationScenario();

        PagedResult<Cake> cakes =
                cakeRepository.findCakes(2, 2);
        assertEquals(1, cakes.content().size());
        assertEquals(2, cakes.page());
        assertEquals(2, cakes.pageSize());
        assertEquals(3, cakes.totalElements());
        assertEquals(2, cakes.totalPages());
        Cake cake = cakes.content().getFirst();

        assertEquals(4L, cake.getId());
        assertEquals("Vanilla Caramel Cake", cake.getName());
        assertEquals("Moist vanilla cake layered with smooth caramel cream.", cake.getDescription());
        assertEquals(new BigDecimal("18.99"), cake.getPrice());
        assertTrue(cake.isActive());
    }

    @Test
    void shouldReturnEmptyContentWhenPageIsOutOfRange() throws SQLException {
        insertCakePaginationScenario();

        PagedResult<Cake> cakes =
                cakeRepository.findCakes(3, 2);
        assertTrue(cakes.content().isEmpty());
        assertEquals(3, cakes.page());
        assertEquals(2, cakes.pageSize());
        assertEquals(3, cakes.totalElements());
        assertEquals(2, cakes.totalPages());
    }

    private void insertCakePaginationScenario() throws SQLException {
        String sql = """
                INSERT INTO cakes
                (id, name, description, price, active)
                VALUES
                (1, 'French Vanilla Cake', 'Classic vanilla cake with a smooth and rich flavor.', 10.00, true),
                (2, 'Vanilla Berry Cake', 'Soft vanilla cake topped with sweet mixed berries.', 12.00, false),
                (3, 'Vanilla Cream Cake', 'Light vanilla cake filled with creamy vanilla frosting.', 15.00, true),
                (4, 'Vanilla Caramel Cake', 'Moist vanilla cake layered with smooth caramel cream.', 18.99, true)
                """;

        connection.createStatement().executeUpdate(sql);
    }
}