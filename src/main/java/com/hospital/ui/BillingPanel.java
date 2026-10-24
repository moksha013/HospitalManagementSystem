package com.hospital.ui;

import com.hospital.model.Appointment;
import com.hospital.model.Bill;
import com.hospital.model.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.BillingService;
import com.hospital.service.DoctorService;
import com.hospital.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Java Swing Panel for Consultation Billing & Invoicing.
 * Implemented by Moksha (Frontend / UI Owner).
 * Demonstrates:
 * - Foreign Key reference to Appointment
 * - Calculation and display of consultation fees
 * - Invoice receipt generation
 */
public class BillingPanel extends JPanel {

    private final BillingService billingService;
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    // Form
    private JComboBox<AppointmentItem> cmbAppointments;
    private JTextField txtManualAptId;
    private JCheckBox chkManualAptId;
    private JTextField txtAmount;
    private JComboBox<String> cmbPaymentStatus;
    private JTextField txtBillDate;
    private JButton btnGenerateBill;
    private JButton btnClear;

    // Table & Summary
    private JTable tblBills;
    private DefaultTableModel tableModel;
    private JButton btnMarkPaid;
    private JButton btnViewReceipt;
    private JButton btnRefresh;
    private JLabel lblSummary;

    public BillingPanel(BillingService billingService, AppointmentService appointmentService, DoctorService doctorService) {
        this.billingService = billingService;
        this.appointmentService = appointmentService;
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

        // Left Form Panel
        JPanel pnlForm = createFormPanel();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.38; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15);
        pnlContent.add(pnlForm, gbc);

        // Right Table Panel
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

        JLabel lblTitle = new JLabel("Generate Consultation Bill");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        cmbAppointments = new JComboBox<>();
        txtManualAptId = UITheme.createTextField(6);
        chkManualAptId = new JCheckBox("Enter Appointment ID manually");
        chkManualAptId.setBackground(Color.WHITE);
        chkManualAptId.setFont(UITheme.FONT_SMALL);

        txtAmount = UITheme.createTextField(10);
        cmbPaymentStatus = new JComboBox<>(new String[]{"Unpaid", "Paid", "Pending"});
        txtBillDate = UITheme.createTextField(10);
        txtBillDate.setText(LocalDate.now().toString());

        // Row 0: Appointment Dropdown
        c.gridx = 0; c.gridy = 0; c.weightx = 0.35;
        pnlFields.add(new JLabel("Appointment * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(cmbAppointments, c);

        // Row 1: Manual toggle
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        pnlFields.add(chkManualAptId, c);
        c.gridwidth = 1;

        // Row 2: Manual Apt ID
        c.gridx = 0; c.gridy = 2; c.weightx = 0.35;
        pnlFields.add(new JLabel("Manual Apt ID:"), c);
        c.gridx = 1; c.weightx = 0.65;
        txtManualAptId.setEnabled(false);
        pnlFields.add(txtManualAptId, c);

        chkManualAptId.addActionListener(e -> {
            boolean manual = chkManualAptId.isSelected();
            txtManualAptId.setEnabled(manual);
            cmbAppointments.setEnabled(!manual);
        });

        // Auto-fill amount when appointment is chosen
        cmbAppointments.addActionListener(e -> {
            AppointmentItem item = (AppointmentItem) cmbAppointments.getSelectedItem();
            if (item != null && item.doctorId > 0) {
                try {
                    Doctor d = doctorService.getDoctorById(item.doctorId);
                    if (d != null && d.getConsultationFee() != null) {
                        txtAmount.setText(d.getConsultationFee().toPlainString());
                    }
                } catch (SQLException ignored) {}
            }
        });

        // Row 3: Amount
        c.gridx = 0; c.gridy = 3; c.weightx = 0.35;
        pnlFields.add(new JLabel("Amount ($) * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(txtAmount, c);

        // Row 4: Payment Status
        c.gridx = 0; c.gridy = 4; c.weightx = 0.35;
        pnlFields.add(new JLabel("Payment Status:"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(cmbPaymentStatus, c);

        // Row 5: Bill Date
        c.gridx = 0; c.gridy = 5; c.weightx = 0.35;
        pnlFields.add(new JLabel("Bill Date * :"), c);
        c.gridx = 1; c.weightx = 0.65;
        pnlFields.add(txtBillDate, c);

        card.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlBtns.setBackground(Color.WHITE);
        btnClear = UITheme.createSecondaryButton("Clear");
        btnGenerateBill = UITheme.createPrimaryButton("Generate Bill");

        btnClear.addActionListener(e -> clearForm());
        btnGenerateBill.addActionListener(e -> handleGenerateBill());

        pnlBtns.add(btnClear);
        pnlBtns.add(btnGenerateBill);
        card.add(pnlBtns, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Consultation Bills Directory");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);
        pnlTop.add(lblTitle, BorderLayout.WEST);

        btnRefresh = UITheme.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> refreshAllData());
        pnlTop.add(btnRefresh, BorderLayout.EAST);

        card.add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Bill ID", "Apt ID", "Patient Name", "Doctor Name", "Amount ($)", "Status", "Bill Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblBills = new JTable(tableModel);
        UITheme.styleTable(tblBills);

        JScrollPane scroll = new JScrollPane(tblBills);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(scroll, BorderLayout.CENTER);

        // Bottom
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);

        lblSummary = new JLabel("Total Bills: 0");
        lblSummary.setFont(UITheme.FONT_SMALL);
        lblSummary.setForeground(UITheme.TEXT_MUTED);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlActions.setBackground(Color.WHITE);

        btnMarkPaid = UITheme.createAccentButton("Mark as Paid");
        btnViewReceipt = UITheme.createSecondaryButton("View Receipt");

        btnMarkPaid.addActionListener(e -> handleMarkPaid());
        btnViewReceipt.addActionListener(e -> handleViewReceipt());

        pnlActions.add(btnMarkPaid);
        pnlActions.add(btnViewReceipt);

        pnlBottom.add(lblSummary, BorderLayout.WEST);
        pnlBottom.add(pnlActions, BorderLayout.EAST);
        card.add(pnlBottom, BorderLayout.SOUTH);

        return card;
    }

    private void handleGenerateBill() {
        try {
            int aptId;
            if (chkManualAptId.isSelected()) {
                String idStr = txtManualAptId.getText().trim();
                if (idStr.isEmpty()) throw new ValidationException("Please enter an Appointment ID.");
                aptId = Integer.parseInt(idStr);
            } else {
                AppointmentItem item = (AppointmentItem) cmbAppointments.getSelectedItem();
                if (item == null) throw new ValidationException("Please select an Appointment.");
                aptId = item.aptId;
            }

            String amtStr = txtAmount.getText().trim();
            if (amtStr.isEmpty()) throw new ValidationException("Amount is required.");
            BigDecimal amount;
            try {
                amount = new BigDecimal(amtStr);
            } catch (NumberFormatException nfe) {
                throw new ValidationException("Amount must be a valid decimal number (e.g., 500.00).");
            }

            String status = (String) cmbPaymentStatus.getSelectedItem();
            LocalDate date;
            try {
                date = LocalDate.parse(txtBillDate.getText().trim());
            } catch (Exception ex) {
                throw new ValidationException("Invalid date format. Use YYYY-MM-DD.");
            }

            Bill bill = new Bill(aptId, amount, status, date);
            boolean success = billingService.generateBill(bill);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Bill generated successfully!\nGenerated Bill ID: #" + bill.getBillId(),
                        "Bill Created", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshAllData();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "ID must be a whole number.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            String msg = sqle.getMessage();
            if (msg != null && msg.toLowerCase().contains("foreign key")) {
                JOptionPane.showMessageDialog(this, "Constraint Error: Appointment ID does not exist.", "Foreign Key Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleMarkPaid() {
        int row = tblBills.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(row, 0);
        try {
            boolean ok = billingService.updatePaymentStatus(billId, "Paid");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Bill #" + billId + " marked as Paid.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewReceipt() {
        int row = tblBills.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view receipt.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(row, 0);
        int aptId = (int) tableModel.getValueAt(row, 1);
        String patientName = String.valueOf(tableModel.getValueAt(row, 2));
        String doctorName = String.valueOf(tableModel.getValueAt(row, 3));
        String amount = String.valueOf(tableModel.getValueAt(row, 4));
        String status = String.valueOf(tableModel.getValueAt(row, 5));
        String date = String.valueOf(tableModel.getValueAt(row, 6));

        String receipt = String.format(
                "====================================================\n" +
                "         CITY GENERAL HOSPITAL - INVOICE RECEIPT\n" +
                "====================================================\n\n" +
                "  Invoice / Bill ID : #%d\n" +
                "  Date of Issue     : %s\n" +
                "  Appointment Ref   : #%d\n\n" +
                "----------------------------------------------------\n" +
                "  Patient Name      : %s\n" +
                "  Consulting Doctor : %s\n" +
                "----------------------------------------------------\n\n" +
                "  Consultation Fee  : $%s\n" +
                "  Payment Status    : [ %s ]\n\n" +
                "====================================================\n" +
                "          Thank you for choosing City General!      \n" +
                "====================================================\n",
                billId, date, aptId, patientName, doctorName, amount, status.toUpperCase()
        );

        JTextArea txtReceipt = new JTextArea(receipt);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setEditable(false);
        txtReceipt.setMargin(new Insets(10, 10, 10, 10));

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Invoice Receipt - Bill #" + billId, true);
        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(txtReceipt), BorderLayout.CENTER);

        JButton btnClose = UITheme.createPrimaryButton("Close");
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnl.add(btnClose);
        dlg.add(pnl, BorderLayout.SOUTH);

        dlg.setSize(440, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    public void refreshAllData() {
        // Reload Appointments in dropdown
        try {
            cmbAppointments.removeAllItems();
            List<Appointment> apts = appointmentService.getAllAppointments();
            for (Appointment a : apts) {
                cmbAppointments.addItem(new AppointmentItem(a.getAppointmentId(), a.getDoctorId(), a.getPatientName(), a.getDoctorName()));
            }
        } catch (SQLException ignored) {}

        // Reload Bills Table
        try {
            List<Bill> bills = billingService.getAllBills();
            tableModel.setRowCount(0);
            BigDecimal totalAmount = BigDecimal.ZERO;
            int unpaidCount = 0;

            for (Bill b : bills) {
                tableModel.addRow(new Object[]{
                        b.getBillId(),
                        b.getAppointmentId(),
                        b.getPatientName() != null ? b.getPatientName() : "-",
                        b.getDoctorName() != null ? b.getDoctorName() : "-",
                        String.format("%.2f", b.getAmount()),
                        b.getPaymentStatus(),
                        b.getBillDate()
                });
                totalAmount = totalAmount.add(b.getAmount());
                if (!"Paid".equalsIgnoreCase(b.getPaymentStatus())) {
                    unpaidCount++;
                }
            }
            lblSummary.setText(String.format("Total Bills: %d | Total Billed: $%.2f | Unpaid: %d", bills.size(), totalAmount, unpaidCount));
        } catch (SQLException sqle) {
            lblSummary.setText("Database error: " + sqle.getMessage());
        }
    }

    private void clearForm() {
        txtManualAptId.setText("");
        chkManualAptId.setSelected(false);
        txtManualAptId.setEnabled(false);
        cmbAppointments.setEnabled(true);
        txtAmount.setText("");
        cmbPaymentStatus.setSelectedIndex(0);
        txtBillDate.setText(LocalDate.now().toString());
        tblBills.clearSelection();
    }

    private static class AppointmentItem {
        final int aptId;
        final int doctorId;
        final String patientName;
        final String doctorName;

        AppointmentItem(int aptId, int doctorId, String patientName, String doctorName) {
            this.aptId = aptId;
            this.doctorId = doctorId;
            this.patientName = patientName;
            this.doctorName = doctorName;
        }

        @Override
        public String toString() {
            return String.format("Apt #%d: %s (Dr. %s)", aptId, 
                    patientName != null ? patientName : "Patient", 
                    doctorName != null ? doctorName : "Doctor");
        }
    }
}
