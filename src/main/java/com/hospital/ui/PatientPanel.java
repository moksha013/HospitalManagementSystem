package com.hospital.ui;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import com.hospital.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Java Swing Panel for Patient Management.
 * Implemented by Moksha (Frontend / UI Owner).
 * Demonstrates:
 * - Form input and validation
 * - JTable integration with DefaultTableModel
 * - Calling PatientService (Separation of UI and Data layers)
 * - Clear user feedback and error messages
 */
public class PatientPanel extends JPanel {

    private final PatientService patientService;

    // Form Components
    private JLabel lblEditingId;
    private JTextField txtFullName;
    private JTextField txtAge;
    private JComboBox<String> cmbGender;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JButton btnRegister;
    private JButton btnUpdate;
    private JButton btnClear;

    // Search and Table Components
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JTable tblPatients;
    private DefaultTableModel tableModel;
    private JButton btnDelete;
    private JLabel lblStatus;

    private int selectedPatientId = -1;

    public PatientPanel(PatientService patientService) {
        this.patientService = patientService;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        loadPatients();
    }

    private void initComponents() {
        // Main split: Left is Form, Right is Table
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(UITheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Left Form Panel
        JPanel pnlFormCard = createFormCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15);
        pnlContent.add(pnlFormCard, gbc);

        // 2. Right Table Panel
        JPanel pnlTableCard = createTableCard();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlContent.add(pnlTableCard, gbc);

        add(pnlContent, BorderLayout.CENTER);
    }

    private JPanel createFormCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        // Form Title
        JLabel lblTitle = new JLabel("Patient Registration & Details");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);

        lblEditingId = new JLabel("Mode: New Patient Registration");
        lblEditingId.setFont(UITheme.FONT_SMALL);
        lblEditingId.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblEditingId);
        card.add(pnlHeader, BorderLayout.NORTH);

        // Form Fields
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        txtFullName = UITheme.createTextField(15);
        txtAge = UITheme.createTextField(5);
        cmbGender = new JComboBox<>(new String[]{"Select", "Male", "Female", "Other"});
        cmbGender.setFont(UITheme.FONT_REGULAR);
        txtPhone = UITheme.createTextField(12);
        txtAddress = UITheme.createTextField(15);

        // Row 0: Full Name
        c.gridx = 0; c.gridy = 0; c.weightx = 0.3;
        pnlFields.add(new JLabel("Full Name * :"), c);
        c.gridx = 1; c.weightx = 0.7;
        pnlFields.add(txtFullName, c);

        // Row 1: Age
        c.gridx = 0; c.gridy = 1; c.weightx = 0.3;
        pnlFields.add(new JLabel("Age * :"), c);
        c.gridx = 1; c.weightx = 0.7;
        pnlFields.add(txtAge, c);

        // Row 2: Gender
        c.gridx = 0; c.gridy = 2; c.weightx = 0.3;
        pnlFields.add(new JLabel("Gender * :"), c);
        c.gridx = 1; c.weightx = 0.7;
        pnlFields.add(cmbGender, c);

        // Row 3: Phone
        c.gridx = 0; c.gridy = 3; c.weightx = 0.3;
        pnlFields.add(new JLabel("Phone * :"), c);
        c.gridx = 1; c.weightx = 0.7;
        pnlFields.add(txtPhone, c);

        // Row 4: Address
        c.gridx = 0; c.gridy = 4; c.weightx = 0.3;
        pnlFields.add(new JLabel("Address * :"), c);
        c.gridx = 1; c.weightx = 0.7;
        pnlFields.add(txtAddress, c);

        card.add(pnlFields, BorderLayout.CENTER);

        // Form Action Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlButtons.setBackground(Color.WHITE);

        btnClear = UITheme.createSecondaryButton("Clear");
        btnUpdate = UITheme.createAccentButton("Update Patient");
        btnRegister = UITheme.createPrimaryButton("Register Patient");

        btnUpdate.setEnabled(false);

        btnClear.addActionListener(e -> clearForm());
        btnRegister.addActionListener(e -> handleRegisterPatient());
        btnUpdate.addActionListener(e -> handleUpdatePatient());

        pnlButtons.add(btnClear);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnRegister);

        card.add(pnlButtons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTableCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        // Top Search Bar
        JPanel pnlSearch = new JPanel(new BorderLayout(8, 8));
        pnlSearch.setBackground(Color.WHITE);

        JLabel lblSearch = new JLabel("Search Patient (Name or ID): ");
        lblSearch.setFont(UITheme.FONT_BOLD);

        txtSearch = UITheme.createTextField(15);
        btnSearch = UITheme.createPrimaryButton("Search");
        btnRefresh = UITheme.createSecondaryButton("Refresh All");

        btnSearch.addActionListener(e -> handleSearch());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadPatients();
        });
        txtSearch.addActionListener(e -> handleSearch());

        JPanel pnlSearchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlSearchControls.setBackground(Color.WHITE);
        pnlSearchControls.add(lblSearch);
        pnlSearchControls.add(txtSearch);
        pnlSearchControls.add(btnSearch);
        pnlSearchControls.add(btnRefresh);

        pnlSearch.add(pnlSearchControls, BorderLayout.WEST);
        card.add(pnlSearch, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Full Name", "Age", "Gender", "Phone", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only
            }
        };

        tblPatients = new JTable(tableModel);
        UITheme.styleTable(tblPatients);

        // Table selection listener: populate form on click
        tblPatients.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPatients.getSelectedRow() != -1) {
                int selectedRow = tblPatients.getSelectedRow();
                selectedPatientId = (int) tableModel.getValueAt(selectedRow, 0);
                txtFullName.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                txtAge.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                cmbGender.setSelectedItem(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
                txtPhone.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
                txtAddress.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));

                lblEditingId.setText("Editing Selected Patient ID: #" + selectedPatientId);
                btnUpdate.setEnabled(true);
                btnRegister.setEnabled(false);
                btnDelete.setEnabled(true);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPatients);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom Bar
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);

        lblStatus = new JLabel("Records: 0");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);

        btnDelete = UITheme.createDangerButton("Delete Selected Patient");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> handleDeletePatient());

        pnlBottom.add(lblStatus, BorderLayout.WEST);
        pnlBottom.add(btnDelete, BorderLayout.EAST);
        card.add(pnlBottom, BorderLayout.SOUTH);

        return card;
    }

    private void handleRegisterPatient() {
        try {
            int age = parseAge();
            Patient patient = new Patient(
                    txtFullName.getText().trim(),
                    age,
                    (String) cmbGender.getSelectedItem(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim()
            );

            boolean success = patientService.registerPatient(patient);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Patient '" + patient.getFullName() + "' registered successfully!\nGenerated Patient ID: #" + patient.getPatientId(),
                        "Registration Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdatePatient() {
        if (selectedPatientId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the table to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int age = parseAge();
            Patient patient = new Patient(
                    selectedPatientId,
                    txtFullName.getText().trim(),
                    age,
                    (String) cmbGender.getSelectedItem(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim()
            );

            boolean success = patientService.updatePatient(patient);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Patient details for #" + selectedPatientId + " updated successfully!",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeletePatient() {
        if (selectedPatientId <= 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Patient #" + selectedPatientId + "?\nAll related appointments will also be deleted.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = patientService.deletePatient(selectedPatientId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Patient #" + selectedPatientId + " deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadPatients();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete patient.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadPatients();
            return;
        }

        try {
            // Check if keyword is numeric ID search
            if (keyword.matches("\\d+")) {
                int searchId = Integer.parseInt(keyword);
                Patient p = patientService.getPatientById(searchId);
                tableModel.setRowCount(0);
                if (p != null) {
                    tableModel.addRow(new Object[]{
                            p.getPatientId(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()
                    });
                    lblStatus.setText("Found 1 record matching ID #" + searchId);
                } else {
                    lblStatus.setText("No patient found with ID #" + searchId);
                    JOptionPane.showMessageDialog(this, "No patient found with ID #" + searchId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                List<Patient> list = patientService.searchPatients(keyword);
                tableModel.setRowCount(0);
                for (Patient p : list) {
                    tableModel.addRow(new Object[]{
                            p.getPatientId(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()
                    });
                }
                lblStatus.setText("Found " + list.size() + " record(s) matching '" + keyword + "'");
            }
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Database search error: " + sqle.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadPatients() {
        try {
            List<Patient> list = patientService.getAllPatients();
            tableModel.setRowCount(0);
            for (Patient p : list) {
                tableModel.addRow(new Object[]{
                        p.getPatientId(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()
                });
            }
            lblStatus.setText("Total Patients: " + list.size());
        } catch (SQLException sqle) {
            lblStatus.setText("Database unavailable: " + sqle.getMessage());
        }
    }

    private void clearForm() {
        selectedPatientId = -1;
        txtFullName.setText("");
        txtAge.setText("");
        cmbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtAddress.setText("");
        lblEditingId.setText("Mode: New Patient Registration");
        btnRegister.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        tblPatients.clearSelection();
    }

    private int parseAge() throws ValidationException {
        String ageStr = txtAge.getText().trim();
        if (ageStr.isEmpty()) {
            throw new ValidationException("Age is required.");
        }
        try {
            return Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a valid whole number (e.g., 28).");
        }
    }
}
