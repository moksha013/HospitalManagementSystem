package com.hospital.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class to manage MySQL JDBC Database Connections.
 * Follows the Singleton pattern for connection configuration.
 * Demonstrates robust exception handling and flexible credentials.
 */
public class DatabaseConnection {

    private static String host = "localhost";
    private static int port = 3306;
    private static String database = "hospital_db";
    private static String username = "root";
    private static String password = "password"; // default placeholder, configurable

    private static String lastErrorMessage = "";
    private static final String CONFIG_FILE = "db.properties";

    static {
        loadConfig();
    }

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(in);
                host = props.getProperty("db.host", host);
                port = Integer.parseInt(props.getProperty("db.port", String.valueOf(port)));
                database = props.getProperty("db.name", database);
                username = props.getProperty("db.user", username);
                password = props.getProperty("db.password", password);
            } catch (Exception e) {
                System.err.println("Notice: Could not read " + CONFIG_FILE + ", using default settings: " + e.getMessage());
            }
        }
    }

    public static void saveConfig(String newHost, int newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        database = newDb;
        username = newUser;
        password = newPass;

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", database);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Hospital Management System - Database Configuration");
        } catch (IOException e) {
            System.err.println("Could not save configuration: " + e.getMessage());
        }
    }

    public static String getJdbcUrl() {
        return String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            host, port, database
        );
    }

    public static String getServerUrlOnly() {
        return String.format(
            "jdbc:mysql://%s:%d/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            host, port
        );
    }

    /**
     * Obtains a new JDBC Connection to MySQL.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            lastErrorMessage = "MySQL JDBC Driver (com.mysql.cj.jdbc.Driver) not found in classpath.";
            throw new SQLException(lastErrorMessage, e);
        }

        try {
            Connection conn = DriverManager.getConnection(getJdbcUrl(), username, password);
            lastErrorMessage = "";
            return conn;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            throw e;
        }
    }

    /**
     * Quick test to verify if the database is reachable.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Creates the hospital_db database if it does not exist yet.
     */
    public static void createDatabaseIfNotExists() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.");
        }

        try (Connection conn = DriverManager.getConnection(getServerUrlOnly(), username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
        }
    }

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDatabase() { return database; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}
