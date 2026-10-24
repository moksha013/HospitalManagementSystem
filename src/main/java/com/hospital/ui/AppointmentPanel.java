package com.hospital.ui;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import com.hospital.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Java Swing Panel for Appointment Booking and Management.
 * Implemented by Moksha (Frontend / UI Owner).
 * Demonstrates:
 * - Foreign Key linking to Patient and Doctor
 * - Displaying SQL INNER JOIN query results (Patient name + Doctor name)
 * - Status transition updates
 */
public class AppointmentPanel extends JPanel {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    // Booking Form
    private JComboBox<PatientItem> cmbPatients;
    private JComboBox<DoctorItem> cmbDoctors;
    private JTextField txtManualPatientId;
    private JTextField txtManualDoctorId;
    private JCheckBox chkManualInput;
    private JTextField txtDate;
    private JTextField txtTime;
    private JComboBox<String> cmbStatus;
    private JButton btnBook;
    private JButton btnClear;

    // Table
    private JTable tblAppointments;
    private DefaultTableModel tableModel;
    private JButton btnMarkCompleted;
    private JButton btnCancelAppointment;
    private JButton btnDeleteAppointment;
    private JButton btnRefresh;
    private JLabel lblStatus;

    public AppointmentPanel(AppointmentService appointmentService, PatientService patientService, DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        refreshAllData();
    }

    private void initComponents() {
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(UITheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Left Form Panel
        JPanel pnlForm = createFormPanel();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.38; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15);
        pnlContent.add(pnlForm, gbc);

        // 2. Right Table Panel
        JPanel pnlTable = createTablePanel();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.62; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlContent.add(pnlTable, gbc);

        add(pnlContent, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Book New Appointment");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        cmbPatients = new JComboBox<>();
        cmbDoctors = new JComboBox<>();
        txtManualPatientId = UITheme.createTextField(6);
        txtManualDoctorId = UITheme.createTextField(6);
        chkManualInput = new JCheckBox("Enter IDs manually (Test Foreign Key constraints)");
        chkManualInput.setBackground(Color.WHITE);
        chkManualInput.setFont(UITheme.FONT_SMALL);

        txtDate = UITheme.createTextField(10);
        txtDate.setText(LocalDate.now().plusDays(1).toString()); // Tomorrow by default

        txtTime = UITheme.createTextField(8);
        txtTime.setText("10:00:00");

        cmbStatus = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});

        // Row 0: Select Patient Dropdown
        c.gridx = 0; c.gridy = 0; c.weightx = 0.35;
        pnlFields.add(new JLabel("Patient * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(cmbPatients, c);

        // Row 1: Select Doctor Dropdown
        c.gridx = 0; c.gridy = 1; c.weightx = 0.35;
        pnlFields.add(new JLabel("Doctor * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(cmbDoctors, c);

        // Row 2: Manual ID checkbox
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        pnlFields.add(chkManualInput, c);
        c.gridwidth = 1;

        // Row 3: Manual Patient ID
        c.gridx = 0; c.gridy = 3; c.weightx = 0.35;
        pnlFields.add(new JLabel("Manual Patient ID:"), c);
        c.gridx = 1; c.weightx = 0.65;
        txtManualPatientId.setEnabled(false);
        pnlFields.add(txtManualPatientId, c);

        // Row 4: Manual Doctor ID
        c.gridx = 0; c.gridy = 4; c.weightx = 0.35;
        pnlFields.add(new JLabel("Manual Doctor ID:"), c);
        c.gridx = 1; c.weightx = 0.65;
        txtManualDoctorId.setEnabled(false);
        pnlFields.add(txtManualDoctorId, c);

        // Toggle manual inputs
        chkManualInput.addActionListener(e -> {
            boolean manual = chkManualInput.isSelected();
            txtManualPatientId.setEnabled(manual);
            txtManualDoctorId.setEnabled(manual);
            cmbPatients.setEnabled(!manual);
            cmbDoctors.setEnabled(!manual);
        });

        // Row 5: Date
        c.gridx = 0; c.gridy = 5; c.weightx = 0.35;
        pnlFields.add(new JLabel("Date (YYYY-MM-DD) * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(txtDate, c);

        // Row 6: Time
        c.gridx = 0; c.gridy = 6; c.weightx = 0.35;
        pnlFields.add(new JLabel("Time (HH:MM:SS) * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(txtTime, c);

        // Row 7: Status
        c.gridx = 0; c.gridy = 7; c.weightx = 0.35;
        pnlFields.add(new JLabel("Status:"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(cmbStatus, c);

        card.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlBtns.setBackground(Color.WHITE);
        btnClear = UITheme.createSecondaryButton("Clear");
        btnBook = UITheme.createPrimaryButton("Book Appointment");

        btnClear.addActionListener(e -> clearForm());
        btnBook.addActionListener(e -> handleBookAppointment());

        pnlBtns.add(btnClear);
        pnlBtns.add(btnBook);
        card.add(pnlBtns, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Appointment Schedule (Joined Details)");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);
        pnlTop.add(lblTitle, BorderLayout.WEST);

        btnRefresh = UITheme.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> refreshAllData());
        pnlTop.add(btnRefresh, BorderLayout.EAST);

        card.add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Apt ID", "Patient Name", "Doctor Name", "Specialization", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblAppointments = new JTable(tableModel);
        UITheme.styleTable(tblAppointments);

        JScrollPane scroll = new JScrollPane(tblAppointments);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(scroll, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);
        lblStatus = new JLabel("Appointments: 0");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlActions.setBackground(Color.WHITE);

        btnMarkCompleted = UITheme.createAccentButton("Mark Completed");
        btnCancelAppointment = UITheme.createSecondaryButton("Cancel Apt");
        btnDeleteAppointment = UITheme.createDangerButton("Delete");

        btnMarkCompleted.addActionListener(e -> handleUpdateStatus("Completed"));
        btnCancelAppointment.addActionListener(e -> handleUpdateStatus("Cancelled"));
        btnDeleteAppointment.addActionListener(e -> handleDeleteAppointment());

        pnlActions.add(btnMarkCompleted);
        pnlActions.add(btnCancelAppointment);
        pnlActions.add(btnDeleteAppointment);

        pnlBottom.add(lblStatus, BorderLayout.WEST);
        pnlBottom.add(pnlActions, BorderLayout.EAST);
        card.add(pnlBottom, BorderLayout.SOUTH);

        return card;
    }

    private void handleBookAppointment() {
        try {
            int patientId;
            int doctorId;

            if (chkManualInput.isSelected()) {
                String pStr = txtManualPatientId.getText().trim();
                String dStr = txtManualDoctorId.getText().trim();
                if (pStr.isEmpty() || dStr.isEmpty()) {
                    throw new ValidationException("Please provide both Patient ID and Doctor ID.");
                }
                patientId = Integer.parseInt(pStr);
                doctorId = Integer.parseInt(dStr);
            } else {
                PatientItem pi = (PatientItem) cmbPatients.getSelectedItem();
                DoctorItem di = (DoctorItem) cmbDoctors.getSelectedItem();
                if (pi == null || di == null) {
                    throw new ValidationException("Please register and select at least one Patient and one Doctor.");
                }
                patientId = pi.id;
                doctorId = di.id;
            }

            LocalDate date;
            try {
                date = LocalDate.parse(txtDate.getText().trim());
            } catch (DateTimeParseException ex) {
                throw new ValidationException("Invalid date format. Please use YYYY-MM-DD (e.g. 2026-10-25).");
            }

            LocalTime time;
            try {
                time = LocalTime.parse(txtTime.getText().trim());
            } catch (DateTimeParseException ex) {
                throw new ValidationException("Invalid time format. Please use HH:MM:SS (e.g. 10:30:00).");
            }

            String status = (String) cmbStatus.getSelectedItem();

            Appointment apt = new Appointment(patientId, doctorId, date, time, status);
            boolean success = appointmentService.bookAppointment(apt);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Appointment booked successfully!\nAppointment ID: #" + apt.getAppointmentId(),
                        "Booking Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshAllData();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "IDs must be valid whole numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Notice", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            // Friendly error for foreign key violations
            String msg = sqle.getMessage();
            if (msg != null && msg.toLowerCase().contains("foreign key")) {
                JOptionPane.showMessageDialog(this,
                        "Database Constraint Error: The specified Patient ID or Doctor ID does not exist in the database.",
                        "Foreign Key Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUpdateStatus(String newStatus) {
        int row = tblAppointments.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int aptId = (int) tableModel.getValueAt(row, 0);
        try {
            boolean success = appointmentService.updateStatus(aptId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment #" + aptId + " marked as " + newStatus + ".", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAppointment() {
        int row = tblAppointments.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int aptId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete Appointment #" + aptId + "? Any linked consultation bill will also be removed.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentService.deleteAppointment(aptId);
                JOptionPane.showMessageDialog(this, "Appointment #" + aptId + " deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Error deleting appointment: " + sqle.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshAllData() {
        // Reload Patients into combobox
        try {
            cmbPatients.removeAllItems();
            List<Patient> patients = patientService.getAllPatients();
            for (Patient p : patients) {
                cmbPatients.addItem(new PatientItem(p.getPatientId(), p.getFullName()));
            }
        } catch (SQLException ignored) {}

        // Reload Doctors into combobox
        try {
            cmbDoctors.removeAllItems();
            List<Doctor> doctors = doctorService.getAllDoctors();
            for (Doctor d : doctors) {
                cmbDoctors.addItem(new DoctorItem(d.getDoctorId(), d.getFullName(), d.getSpecialization()));
            }
        } catch (SQLException ignored) {}

        // Reload Appointments table
        try {
            List<Appointment> list = appointmentService.getAllAppointments();
            tableModel.setRowCount(0);
            for (Appointment a : list) {
                tableModel.addRow(new Object[]{
                        a.getAppointmentId(),
                        a.getPatientName() != null ? a.getPatientName() : "ID #" + a.getPatientId(),
                        a.getDoctorName() != null ? a.getDoctorName() : "ID #" + a.getDoctorId(),
                        a.getDoctorSpecialization() != null ? a.getDoctorSpecialization() : "-",
                        a.getAppointmentDate(),
                        a.getAppointmentTime(),
                        a.getStatus()
                });
            }
            lblStatus.setText("Total Appointments: " + list.size());
        } catch (SQLException sqle) {
            lblStatus.setText("Database error: " + sqle.getMessage());
        }
    }

    private void clearForm() {
        txtManualPatientId.setText("");
        txtManualDoctorId.setText("");
        chkManualInput.setSelected(false);
        txtManualPatientId.setEnabled(false);
        txtManualDoctorId.setEnabled(false);
        cmbPatients.setEnabled(true);
        cmbDoctors.setEnabled(true);
        txtDate.setText(LocalDate.now().plusDays(1).toString());
        txtTime.setText("10:00:00");
        cmbStatus.setSelectedIndex(0);
        tblAppointments.clearSelection();
    }

    // Helper item classes for JComboBox
    private static class PatientItem {
        final int id;
        final String name;
        PatientItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return "#" + id + " - " + name; }
    }

    private static class DoctorItem {
        final int id;
        final String name;
        final String spec;
        DoctorItem(int id, String name, String spec) { this.id = id; this.name = name; this.spec = spec; }
        @Override public String toString() { return "#" + id + " - Dr. " + name + " (" + spec + ")"; }
    }
}
