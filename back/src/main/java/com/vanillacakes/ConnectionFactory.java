package com.vanillacakes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public Connection create() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5433/vanilla_db",
                "vanilla_admin",
                "vanilla_admin");
    }
}
