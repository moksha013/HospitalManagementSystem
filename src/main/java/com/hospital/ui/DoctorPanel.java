package com.hospital.ui;

import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import com.hospital.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Java Swing Panel for Doctor Records and Directory.
 * Implemented by Moksha (Frontend / UI Owner).
 */
public class DoctorPanel extends JPanel {

    private final DoctorService doctorService;

    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnAddDoctor;
    private JTable tblDoctors;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public DoctorPanel(DoctorService doctorService) {
        this.doctorService = doctorService;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        loadDoctors();
    }

    private void initComponents() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(12, 12));

        // Header and Search Bar
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Doctors Directory");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlControls.setBackground(Color.WHITE);

        pnlControls.add(new JLabel("Search (Name / Specialization):"));
        txtSearch = UITheme.createTextField(16);
        btnSearch = UITheme.createPrimaryButton("Search");
        btnRefresh = UITheme.createSecondaryButton("Refresh");
        btnAddDoctor = UITheme.createAccentButton("+ Add Doctor");

        pnlControls.add(txtSearch);
        pnlControls.add(btnSearch);
        pnlControls.add(btnRefresh);
        pnlControls.add(btnAddDoctor);

        pnlTop.add(pnlControls, BorderLayout.CENTER);
        card.add(pnlTop, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Doctor ID", "Doctor Name", "Specialization", "Phone", "Consultation Fee"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDoctors = new JTable(tableModel);
        UITheme.styleTable(tblDoctors);

        JScrollPane scrollPane = new JScrollPane(tblDoctors);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom status
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);
        lblStatus = new JLabel("Total Doctors: 0");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        pnlBottom.add(lblStatus, BorderLayout.WEST);

        card.add(pnlBottom, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        // Listeners
        btnSearch.addActionListener(e -> handleSearch());
        txtSearch.addActionListener(e -> handleSearch());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadDoctors();
        });
        btnAddDoctor.addActionListener(e -> showAddDoctorDialog());
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        try {
            List<Doctor> list = doctorService.searchDoctors(query);
            populateTable(list);
            lblStatus.setText("Found " + list.size() + " doctor(s) matching '" + query + "'");
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Error searching doctors: " + sqle.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadDoctors() {
        try {
            List<Doctor> list = doctorService.getAllDoctors();
            populateTable(list);
            lblStatus.setText("Total Doctors Registered: " + list.size());
        } catch (SQLException sqle) {
            lblStatus.setText("Database unavailable: " + sqle.getMessage());
        }
    }

    private void populateTable(List<Doctor> list) {
        tableModel.setRowCount(0);
        for (Doctor d : list) {
            tableModel.addRow(new Object[]{
                    d.getDoctorId(),
                    d.getFullName(),
                    d.getSpecialization(),
                    d.getPhone(),
                    String.format("$%.2f", d.getConsultationFee())
            });
        }
    }

    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Doctor", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        JTextField txtName = UITheme.createTextField(15);
        JTextField txtSpec = UITheme.createTextField(15);
        JTextField txtPhone = UITheme.createTextField(15);
        JTextField txtFee = UITheme.createTextField(15);

        c.gridx = 0; c.gridy = 0; pnl.add(new JLabel("Full Name * :"), c);
        c.gridx = 1; pnl.add(txtName, c);

        c.gridx = 0; c.gridy = 1; pnl.add(new JLabel("Specialization * :"), c);
        c.gridx = 1; pnl.add(txtSpec, c);

        c.gridx = 0; c.gridy = 2; pnl.add(new JLabel("Phone * :"), c);
        c.gridx = 1; pnl.add(txtPhone, c);

        c.gridx = 0; c.gridy = 3; pnl.add(new JLabel("Consultation Fee * :"), c);
        c.gridx = 1; pnl.add(txtFee, c);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setBackground(Color.WHITE);
        JButton btnCancel = UITheme.createSecondaryButton("Cancel");
        JButton btnSave = UITheme.createPrimaryButton("Save Doctor");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String feeStr = txtFee.getText().trim();
                BigDecimal fee = feeStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(feeStr);
                Doctor doc = new Doctor(txtName.getText().trim(), txtSpec.getText().trim(), txtPhone.getText().trim(), fee);
                doctorService.registerDoctor(doc);
                JOptionPane.showMessageDialog(dialog, "Doctor '" + doc.getFullName() + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadDoctors();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Consultation fee must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(dialog, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + sqle.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);

        dialog.add(pnl, BorderLayout.CENTER);
        dialog.add(pnlButtons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
