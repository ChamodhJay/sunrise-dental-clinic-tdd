package com.icbt.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnectionFactory {
    private static final String DEFAULT_URL =
            "jdbc:mysql://127.0.0.1:3306/sunrise_dental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private DBConnectionFactory() { }

    public static Connection getConnection() throws SQLException {
        String url = setting("sunrise.db.url", "SUNRISE_DB_URL", DEFAULT_URL);
        String username = setting("sunrise.db.user", "SUNRISE_DB_USER", "root");
        String password = setting("sunrise.db.password", "SUNRISE_DB_PASSWORD", "admin");
        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property != null && !property.isBlank()) {
            return property;
        }
        String environment = System.getenv(environmentName);
        return environment == null || environment.isBlank() ? defaultValue : environment;
    }
}
