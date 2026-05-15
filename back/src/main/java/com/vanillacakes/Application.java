package com.vanillacakes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws Exception {
        setupDatabase();
        setupWebServer();
    }

    private static void setupDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/vanilla_db",
                "vanilla_admin",
                "vanilla_admin");

        LiquibaseRunner.run(connection);
    }

    private static void setupWebServer() throws LifecycleException {
        Tomcat tomcat = new Tomcat();

        // Explicit port configuration (8080 is the default)
        tomcat.setPort(8080);

        // Initializes the HTTP connector on port 8080
        tomcat.getConnector();

        Context context = tomcat.addContext("", null);

        // Temporary servlet to test Tomcat setup
        Tomcat.addServlet(context,
                "helloWorldServlet",
                new HttpServlet() {
                    @Override
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                        String wavingHandEmoji = "&#x1F44B;";
                        resp.getWriter().write("<h1>Hello World! " + wavingHandEmoji + "</h1>");
                    }
                }
        );

        context.addServletMappingDecoded("/", "helloWorldServlet");

        tomcat.start();
    }
}