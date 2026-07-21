package com.vanillacakes;

import com.vanillacakes.cakes.CakeController;
import com.vanillacakes.cakes.CakeImageController;
import com.vanillacakes.cakes.CakeImageRepository;
import com.vanillacakes.cakes.CakeRepository;
import com.vanillacakes.orders.OrderController;
import com.vanillacakes.orders.OrderRepository;
import com.vanillacakes.orders.OrderService;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
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

        File staticDir = new File("src/main/resources/static");
        Context context = tomcat.addContext("",
                staticDir.getAbsolutePath());
        //To avoid the Request blocking of JS files.
        //See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Content-Type-Options
        context.addMimeMapping("js", "text/javascript");

        Tomcat.addServlet(context, "defaultServlet", new DefaultServlet());
        context.addServletMappingDecoded("/", "defaultServlet");

        // TODO Global connection is fragile! Fix this.
        Connection connection = createConnection();

        CakeRepository cakeRepository = new CakeRepository(connection);
        CakeController cakeController = new CakeController(cakeRepository);

        Tomcat.addServlet(context,
                "cakeServlet",
                cakeController
        );
        context.addServletMappingDecoded("/api/cakes/*", "cakeServlet");

        CakeImageRepository cakeImageRepository = new CakeImageRepository(connection);
        CakeImageController cakeImageController = new CakeImageController(cakeImageRepository);

        Tomcat.addServlet(context,
                "cakeImageServlet",
                cakeImageController
        );
        context.addServletMappingDecoded("/api/cake-images/by-cake/*", "cakeImageServlet");

        OrderRepository orderRepository = new OrderRepository(connection);
        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        Tomcat.addServlet(context,
                "orderServlet",
                orderController);
        context.addServletMappingDecoded("/api/orders/*", "orderServlet");

        tomcat.start();
    }

    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5433/vanilla_db",
                "vanilla_admin",
                "vanilla_admin");
    }
}