package com.vanillacakes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HelloWorld {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/vanilla_db",
                "vanilla_admin",
            "vanilla_admin");

        LiquibaseRunner.run(connection);
    }
}