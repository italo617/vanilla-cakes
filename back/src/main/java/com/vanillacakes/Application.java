package com.vanillacakes;

import com.vanillacakes.cakes.CakeController;
import com.vanillacakes.cakes.CakeRepository;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws Exception {
        setupDatabase();
        setupWebServer();
    }

    private static void setupDatabase() throws SQLException {
        try (Connection connection = createConnection()) {
            LiquibaseRunner.run(connection);
        }
    }

    private static void setupWebServer() throws LifecycleException, SQLException {
        Tomcat tomcat = new Tomcat();

        // Explicit port configuration (8080 is the default)
        tomcat.setPort(8080);

        // Initializes the HTTP connector on port 8080
        tomcat.getConnector();

        Context context = tomcat.addContext("", null);

        // TODO Global connection is fragile! Fix this.
        Connection connection = createConnection();
        CakeRepository cakeRepository = new CakeRepository(connection);
        CakeController cakeController = new CakeController(cakeRepository);

        Tomcat.addServlet(context,
                "cakeServlet",
                cakeController
        );

        context.addServletMappingDecoded("/api/cakes/*", "cakeServlet");

        tomcat.start();
    }

    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5433/vanilla_db",
                "vanilla_admin",
                "vanilla_admin");
    }
}