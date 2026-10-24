package com.hospital.ui;

import com.hospital.service.AppointmentService;
import com.hospital.service.BillingService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import com.hospital.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Java Swing Dashboard Frame.
 * Designed and implemented by Moksha (Frontend / UI Owner).
 * Demonstrates:
 * - Tabbed navigation and consistent modern desktop layout
 * - Real-time database connection monitoring and fallback status
 * - Overview statistics dashboard
 * - Separation of concerns (Delegates to Services and child Panels)
 */
public class MainDashboard extends JFrame {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final BillingService billingService;

    // Header Components
    private JLabel lblDbStatus;
    private JButton btnDbConfig;

    // Navigation Tabs
    private JTabbedPane tabbedPane;
    private JPanel pnlOverview;
    private PatientPanel pnlPatients;
    private DoctorPanel pnlDoctors;
    private AppointmentPanel pnlAppointments;
    private BillingPanel pnlBilling;

    // Overview Stats Labels
    private JLabel lblStatPatients;
    private JLabel lblStatDoctors;
    private JLabel lblStatAppointments;
    private JLabel lblStatBills;

    public MainDashboard() {
        super("Hospital Management System | OOPS Java + DBMS");

        this.patientService = new PatientService();
        this.doctorService = new DoctorService();
        this.appointmentService = new AppointmentService();
        this.billingService = new BillingService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);

        initUI();
        updateDbStatusBadge();
        refreshOverviewStats();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // 1. Top Header Banner
        JPanel pnlHeader = createHeaderBanner();
        root.add(pnlHeader, BorderLayout.NORTH);

        // 2. Main Tabbed Navigation
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        // Create Child Panels
        pnlOverview = createOverviewPanel();
        pnlPatients = new PatientPanel(patientService);
        pnlDoctors = new DoctorPanel(doctorService);
        pnlAppointments = new AppointmentPanel(appointmentService, patientService, doctorService);
        pnlBilling = new BillingPanel(billingService, appointmentService, doctorService);

        tabbedPane.addTab("  Dashboard Overview  ", pnlOverview);
        tabbedPane.addTab("  Patients  ", pnlPatients);
        tabbedPane.addTab("  Doctors  ", pnlDoctors);
        tabbedPane.addTab("  Appointments  ", pnlAppointments);
        tabbedPane.addTab("  Billing & Invoices  ", pnlBilling);

        // Auto-refresh when tabs change
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 0) {
                refreshOverviewStats();
            } else if (selected == 1) {
                pnlPatients.loadPatients();
            } else if (selected == 2) {
                pnlDoctors.loadDoctors();
            } else if (selected == 3) {
                pnlAppointments.refreshAllData();
            } else if (selected == 4) {
                pnlBilling.refreshAllData();
            }
        });

        root.add(tabbedPane, BorderLayout.CENTER);

        // 3. Footer Bar
        JPanel pnlFooter = createFooterBar();
        root.add(pnlFooter, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeaderBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UITheme.PRIMARY_COLOR);
        banner.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left Branding
        JPanel pnlBrand = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlBrand.setBackground(UITheme.PRIMARY_COLOR);

        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Project for OOPS (Java) & DBMS | Team: Moksha (UI) & Poojitha (Database)");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(200, 225, 245));

        pnlBrand.add(lblTitle);
        pnlBrand.add(lblSub);
        banner.add(pnlBrand, BorderLayout.WEST);

        // Right Status & DB Button
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        pnlRight.setBackground(UITheme.PRIMARY_COLOR);

        lblDbStatus = new JLabel("Checking MySQL...");
        lblDbStatus.setFont(UITheme.FONT_BOLD);
        lblDbStatus.setForeground(Color.WHITE);

        btnDbConfig = UITheme.createSecondaryButton("⚙ DB Settings");
        btnDbConfig.addActionListener(e -> {
            DatabaseConfigDialog dlg = new DatabaseConfigDialog(this, () -> {
                updateDbStatusBadge();
                refreshOverviewStats();
                pnlPatients.loadPatients();
                pnlDoctors.loadDoctors();
                pnlAppointments.refreshAllData();
                pnlBilling.refreshAllData();
            });
            dlg.setVisible(true);
        });

        pnlRight.add(lblDbStatus);
        pnlRight.add(btnDbConfig);
        banner.add(pnlRight, BorderLayout.EAST);

        return banner;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Metric Cards Row
        JPanel pnlMetrics = new JPanel(new GridLayout(1, 4, 15, 15));
        pnlMetrics.setBackground(UITheme.BG_LIGHT);

        lblStatPatients = new JLabel("0", SwingConstants.CENTER);
        lblStatDoctors = new JLabel("0", SwingConstants.CENTER);
        lblStatAppointments = new JLabel("0", SwingConstants.CENTER);
        lblStatBills = new JLabel("0", SwingConstants.CENTER);

        pnlMetrics.add(createMetricCard("Total Patients", lblStatPatients, new Color(41, 128, 185)));
        pnlMetrics.add(createMetricCard("Registered Doctors", lblStatDoctors, new Color(39, 174, 96)));
        pnlMetrics.add(createMetricCard("Appointments", lblStatAppointments, new Color(142, 68, 173)));
        pnlMetrics.add(createMetricCard("Total Bills", lblStatBills, new Color(230, 126, 34)));

        panel.add(pnlMetrics, BorderLayout.NORTH);

        // 2. Center Info Card & Quick Actions
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 15, 15));
        pnlCenter.setBackground(UITheme.BG_LIGHT);

        // Left Card: Quick Actions
        JPanel cardActions = UITheme.createCardPanel();
        cardActions.setLayout(new GridLayout(5, 1, 10, 10));

        JLabel lblActTitle = new JLabel("Quick Navigation Actions");
        lblActTitle.setFont(UITheme.FONT_SUBTITLE);
        lblActTitle.setForeground(UITheme.PRIMARY_COLOR);
        cardActions.add(lblActTitle);

        JButton btnGoPatient = UITheme.createPrimaryButton("➕ Register / Manage Patients");
        btnGoPatient.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        cardActions.add(btnGoPatient);

        JButton btnGoDoctor = UITheme.createSecondaryButton("👨‍⚕️ View Doctors Directory");
        btnGoDoctor.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        cardActions.add(btnGoDoctor);

        JButton btnGoAppt = UITheme.createAccentButton("📅 Schedule New Appointment");
        btnGoAppt.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        cardActions.add(btnGoAppt);

        JButton btnGoBill = UITheme.createSecondaryButton("💳 Generate / View Invoices");
        btnGoBill.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        cardActions.add(btnGoBill);

        pnlCenter.add(cardActions);

        // Right Card: OOPS & DBMS Concept Showcase (For Viva!)
        JPanel cardViva = UITheme.createCardPanel();
        cardViva.setLayout(new BorderLayout(8, 8));

        JLabel lblVivaTitle = new JLabel("Academic Viva Concept Mapping");
        lblVivaTitle.setFont(UITheme.FONT_SUBTITLE);
        lblVivaTitle.setForeground(UITheme.PRIMARY_COLOR);
        cardViva.add(lblVivaTitle, BorderLayout.NORTH);

        String vivaHtml = "<html><body style='font-family: Segoe UI; font-size: 11px; color: #333333; line-height: 1.5;'>" +
                "<p><b>OOPS Concepts Demonstrated:</b></p>" +
                "<ul>" +
                "<li><b>Classes & Objects:</b> Patient, Doctor, Appointment, Bill models.</li>" +
                "<li><b>Encapsulation:</b> Private fields with validated getters/setters.</li>" +
                "<li><b>Inheritance:</b> <i>Person</i> base class inherited by <i>Patient</i> and <i>Doctor</i>.</li>" +
                "<li><b>Polymorphism:</b> Overridden <code>getDetails()</code> across subclasses.</li>" +
                "<li><b>Abstraction:</b> DAO interfaces (<code>PatientDao</code>, etc.) separating interface from JDBC logic.</li>" +
                "<li><b>Exception Handling:</b> <code>ValidationException</code> and <code>SQLException</code> handling.</li>" +
                "</ul>" +
                "<p><b>DBMS Concepts Demonstrated:</b></p>" +
                "<ul>" +
                "<li><b>Relational Schema:</b> 4 tables with PK, FK, NOT NULL, and CHECK constraints.</li>" +
                "<li><b>SQL Queries:</b> CRUD via <code>PreparedStatement</code> (SQL injection prevention).</li>" +
                "<li><b>INNER JOIN:</b> Appointments & Bills joined with Patient & Doctor tables.</li>" +
                "</ul>" +
                "</body></html>";

        JLabel lblVivaContent = new JLabel(vivaHtml);
        cardViva.add(new JScrollPane(lblVivaContent), BorderLayout.CENTER);
        pnlCenter.add(cardViva);

        panel.add(pnlCenter, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String title, JLabel lblValue, Color accentColor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(8, 8));

        JPanel topBar = new JPanel();
        topBar.setBackground(accentColor);
        topBar.setPreferredSize(new Dimension(0, 4));
        card.add(topBar, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BOLD);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.CENTER);
        card.add(lblValue, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFooterBar() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(6, 15, 6, 15)
        ));

        JLabel lblLeft = new JLabel("Hospital Management System v1.0 | Ready");
        lblLeft.setFont(UITheme.FONT_SMALL);
        lblLeft.setForeground(UITheme.TEXT_MUTED);

        JLabel lblRight = new JLabel("Frontend Owner: Moksha  |  Database Owner: Poojitha");
        lblRight.setFont(UITheme.FONT_SMALL);
        lblRight.setForeground(UITheme.TEXT_MUTED);

        footer.add(lblLeft, BorderLayout.WEST);
        footer.add(lblRight, BorderLayout.EAST);
        return footer;
    }

    public void updateDbStatusBadge() {
        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            lblDbStatus.setText("● MySQL: Connected (" + DatabaseConnection.getDatabase() + ")");
            lblDbStatus.setForeground(new Color(170, 255, 170));
        } else {
            lblDbStatus.setText("● MySQL: Disconnected (Click Settings)");
            lblDbStatus.setForeground(new Color(255, 180, 180));
        }
    }

    public void refreshOverviewStats() {
        try {
            int patients = patientService.getAllPatients().size();
            lblStatPatients.setText(String.valueOf(patients));
        } catch (Exception e) {
            lblStatPatients.setText("-");
        }

        try {
            int doctors = doctorService.getAllDoctors().size();
            lblStatDoctors.setText(String.valueOf(doctors));
        } catch (Exception e) {
            lblStatDoctors.setText("-");
        }

        try {
            int apts = appointmentService.getAllAppointments().size();
            lblStatAppointments.setText(String.valueOf(apts));
        } catch (Exception e) {
            lblStatAppointments.setText("-");
        }

        try {
            int bills = billingService.getAllBills().size();
            lblStatBills.setText(String.valueOf(bills));
        } catch (Exception e) {
            lblStatBills.setText("-");
        }
    }
}
