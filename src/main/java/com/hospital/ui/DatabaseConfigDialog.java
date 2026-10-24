package com.hospital.ui;

import com.hospital.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuration Dialog for MySQL Database connection.
 * Enables live testing of credentials and running schema.sql initialization.
 */
public class DatabaseConfigDialog extends JDialog {

    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtDbName;
    private JTextField txtUser;
    private JPasswordField txtPassword;
    private JLabel lblStatus;
    private final Runnable onConnectionChanged;

    public DatabaseConfigDialog(Frame owner, Runnable onConnectionChanged) {
        super(owner, "Database Connection Settings", true);
        this.onConnectionChanged = onConnectionChanged;

        setSize(460, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("MySQL Database Configuration");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);
        pnlMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        txtHost = UITheme.createTextField(15);
        txtHost.setText(DatabaseConnection.getHost());

        txtPort = UITheme.createTextField(5);
        txtPort.setText(String.valueOf(DatabaseConnection.getPort()));

        txtDbName = UITheme.createTextField(15);
        txtDbName.setText(DatabaseConnection.getDatabase());

        txtUser = UITheme.createTextField(15);
        txtUser.setText(DatabaseConnection.getUsername());

        txtPassword = new JPasswordField(DatabaseConnection.getPassword(), 15);
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        c.gridx = 0; c.gridy = 0; pnlFields.add(new JLabel("Host:"), c);
        c.gridx = 1; pnlFields.add(txtHost, c);

        c.gridx = 0; c.gridy = 1; pnlFields.add(new JLabel("Port:"), c);
        c.gridx = 1; pnlFields.add(txtPort, c);

        c.gridx = 0; c.gridy = 2; pnlFields.add(new JLabel("Database:"), c);
        c.gridx = 1; pnlFields.add(txtDbName, c);

        c.gridx = 0; c.gridy = 3; pnlFields.add(new JLabel("Username:"), c);
        c.gridx = 1; pnlFields.add(txtUser, c);

        c.gridx = 0; c.gridy = 4; pnlFields.add(new JLabel("Password:"), c);
        c.gridx = 1; pnlFields.add(txtPassword, c);

        pnlMain.add(pnlFields, BorderLayout.CENTER);

        // Status & Buttons
        JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
        pnlBottom.setBackground(Color.WHITE);

        lblStatus = new JLabel("Status: Unknown");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        pnlActions.setBackground(Color.WHITE);

        JButton btnTest = UITheme.createSecondaryButton("Test Connection");
        JButton btnInitDb = UITheme.createAccentButton("Initialize Schema");
        JButton btnSave = UITheme.createPrimaryButton("Save & Connect");

        btnTest.addActionListener(e -> testCurrentFields());
        btnSave.addActionListener(e -> saveAndApply());
        btnInitDb.addActionListener(e -> initializeDatabaseSchema());

        pnlActions.add(btnTest);
        pnlActions.add(btnInitDb);
        pnlActions.add(btnSave);

        pnlBottom.add(lblStatus, BorderLayout.NORTH);
        pnlBottom.add(pnlActions, BorderLayout.SOUTH);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        add(pnlMain);
    }

    private void testCurrentFields() {
        applyFieldsToConfig();
        boolean ok = DatabaseConnection.testConnection();
        if (ok) {
            lblStatus.setText("Status: Connection Successful! (MySQL online)");
            lblStatus.setForeground(UITheme.SUCCESS_COLOR);
            JOptionPane.showMessageDialog(this, "Successfully connected to MySQL database!", "Connection Test", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblStatus.setText("Status: Connection Failed!");
            lblStatus.setForeground(UITheme.DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Connection failed:\n" + DatabaseConnection.getLastErrorMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAndApply() {
        applyFieldsToConfig();
        boolean ok = DatabaseConnection.testConnection();
        if (onConnectionChanged != null) {
            onConnectionChanged.run();
        }
        if (ok) {
            JOptionPane.showMessageDialog(this, "Database settings saved and connected successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            int proceed = JOptionPane.showConfirmDialog(this,
                    "Connection failed with provided credentials:\n" + DatabaseConnection.getLastErrorMessage() +
                    "\n\nDo you still want to keep these settings?",
                    "Connection Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (proceed == JOptionPane.YES_OPTION) {
                dispose();
            }
        }
    }

    private void applyFieldsToConfig() {
        try {
            int port = Integer.parseInt(txtPort.getText().trim());
            DatabaseConnection.saveConfig(
                    txtHost.getText().trim(),
                    port,
                    txtDbName.getText().trim(),
                    txtUser.getText().trim(),
                    new String(txtPassword.getPassword())
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Port must be an integer.", "Invalid Port", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initializeDatabaseSchema() {
        applyFieldsToConfig();
        try {
            // Ensure database exists
            DatabaseConnection.createDatabaseIfNotExists();

            // Read database/schema.sql
            String schemaContent = "";
            if (Files.exists(Paths.get("database/schema.sql"))) {
                schemaContent = new String(Files.readAllBytes(Paths.get("database/schema.sql")));
            } else if (Files.exists(Paths.get("HospitalManagementSystem/database/schema.sql"))) {
                schemaContent = new String(Files.readAllBytes(Paths.get("HospitalManagementSystem/database/schema.sql")));
            }

            if (schemaContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "schema.sql file not found in database/ directory.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                String[] statements = schemaContent.split(";");
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        try {
                            stmt.execute(trimmed);
                        } catch (SQLException ignoreCommentOrDrop) {
                            // Continue running subsequent table creation
                        }
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Schema tables and fictional sample data initialized successfully in MySQL!", "Database Initialized", JOptionPane.INFORMATION_MESSAGE);
            if (onConnectionChanged != null) {
                onConnectionChanged.run();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to initialize schema: " + ex.getMessage(), "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
