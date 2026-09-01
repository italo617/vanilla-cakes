package com.vanillacakes;

import com.vanillacakes.cakes.CakeController;
import com.vanillacakes.cakes.CakeImageController;
import com.vanillacakes.cakes.CakeImageService;
import com.vanillacakes.cakes.CakeService;
import com.vanillacakes.orders.OrderController;
import com.vanillacakes.orders.OrderService;
import com.vanillacakes.transactions.TransactionManager;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        setupDatabase(connectionFactory);
        loadSeed(connectionFactory);
        setupWebServer(connectionFactory);
    }

    private static void setupDatabase(ConnectionFactory connectionFactory) throws SQLException {
        try (Connection connection = connectionFactory.create()) {
            LiquibaseRunner.run(connection);
        }
    }

    private static void loadSeed(ConnectionFactory connectionFactory) {
        TransactionManager transactionManager = new TransactionManager(connectionFactory);
        SeedLoader seedLoader = new SeedLoader(transactionManager);
        seedLoader.load();
    }

    private static void setupWebServer(ConnectionFactory connectionFactory) throws LifecycleException {
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

        TransactionManager transactionManager = new TransactionManager(connectionFactory);

        CakeService cakeService = new CakeService(transactionManager);
        CakeController cakeController = new CakeController(cakeService);

        Tomcat.addServlet(context,
                "cakeServlet",
                cakeController
        );
        context.addServletMappingDecoded("/api/cakes/*", "cakeServlet");

        CakeImageService cakeImageService = new CakeImageService(transactionManager);
        CakeImageController cakeImageController = new CakeImageController(cakeImageService);

        Tomcat.addServlet(context,
                "cakeImageServlet",
                cakeImageController
        );
        context.addServletMappingDecoded("/api/cake-images/by-cake/*", "cakeImageServlet");

        OrderService orderService = new OrderService(transactionManager);
        OrderController orderController = new OrderController(orderService);

        Tomcat.addServlet(context,
                "orderServlet",
                orderController);
        context.addServletMappingDecoded("/api/orders/*", "orderServlet");

        tomcat.start();
    }
}