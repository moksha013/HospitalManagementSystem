package com.hospital;

import com.hospital.ui.MainDashboard;
import com.hospital.util.DatabaseConnection;

import javax.swing.*;

/**
 * Entry point for Hospital Management System.
 * Connects the UI to the service/database architecture.
 */
public class Main {

    public static void main(String[] args) {
        // Set modern Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing Look and Feel
        }

        System.out.println("=================================================");
        System.out.println("Starting Hospital Management System");
        System.out.println("Team: Moksha (Frontend/UI) & Poojitha (Database)");
        System.out.println("=================================================");

        // Test database connection at startup
        boolean dbOk = DatabaseConnection.testConnection();
        if (dbOk) {
            System.out.println("✓ MySQL connection established successfully.");
        } else {
            System.out.println("! Notice: MySQL is not currently connected: " + DatabaseConnection.getLastErrorMessage());
            System.out.println("  The application UI will open normally. You can configure MySQL credentials via 'DB Settings'.");
        }

        // Launch Dashboard on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        });
    }
}
