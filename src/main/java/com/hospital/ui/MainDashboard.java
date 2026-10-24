package com.hospital.ui;

import com.hospital.service.AppointmentService;
import com.hospital.service.BillingService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import com.hospital.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Java Swing Dashboard Frame.
 * Designed and implemented by Moksha (Frontend / UI Owner).
 * Demonstrates:
 * - Clean, accessible navigation and layout
 * - Explicit contrast and hover feedback for buttons
 * - Live MySQL connection monitoring and informative disconnected state
 * - Compact, dynamically connected metric cards
 * - Fully wrapped, non-scrolling Academic Viva concept mapping
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

    // Disconnected state banner in Overview
    private JPanel pnlOfflineBanner;
    private JLabel lblOfflineMessage;

    // Navigation Tabs
    private JTabbedPane tabbedPane;
    private JPanel pnlOverview;
    private PatientPanel pnlPatients;
    private DoctorPanel pnlDoctors;
    private AppointmentPanel pnlAppointments;
    private BillingPanel pnlBilling;

    // Overview Metric Labels & Subtext
    private JLabel lblStatPatients;
    private JLabel lblSubPatients;
    private JLabel lblStatDoctors;
    private JLabel lblSubDoctors;
    private JLabel lblStatAppointments;
    private JLabel lblSubAppointments;
    private JLabel lblStatBills;
    private JLabel lblSubBills;

    public MainDashboard() {
        super("Hospital Management System | OOPS Java + DBMS");

        this.patientService = new PatientService();
        this.doctorService = new DoctorService();
        this.appointmentService = new AppointmentService();
        this.billingService = new BillingService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        initUI();
        updateDbStatusBadge();
        refreshOverviewStats();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // 1. Top Header Banner (Preserved Deep Navy Theme)
        JPanel pnlHeader = createHeaderBanner();
        root.add(pnlHeader, BorderLayout.NORTH);

        // 2. Main Tabbed Navigation
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        // Child Panels
        pnlOverview = createOverviewPanel();
        pnlPatients = new PatientPanel(patientService);
        pnlDoctors = new DoctorPanel(doctorService);
        pnlAppointments = new AppointmentPanel(appointmentService, patientService, doctorService);
        pnlBilling = new BillingPanel(billingService, appointmentService, doctorService);

        // Wrap Overview in a responsive scroll pane without horizontal scroll
        JScrollPane scrollOverview = new JScrollPane(pnlOverview);
        scrollOverview.setBorder(null);
        scrollOverview.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollOverview.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollOverview.getVerticalScrollBar().setUnitIncrement(14);

        tabbedPane.addTab("  Dashboard Overview  ", scrollOverview);
        tabbedPane.addTab("  Patients  ", pnlPatients);
        tabbedPane.addTab("  Doctors  ", pnlDoctors);
        tabbedPane.addTab("  Appointments  ", pnlAppointments);
        tabbedPane.addTab("  Billing & Invoices  ", pnlBilling);

        // Auto-refresh when tabs change
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 0) {
                updateDbStatusBadge();
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
        JPanel banner = new JPanel(new BorderLayout(15, 0));
        banner.setBackground(UITheme.PRIMARY_COLOR);
        banner.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left Branding
        JPanel pnlBrand = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlBrand.setBackground(UITheme.PRIMARY_COLOR);

        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Object-Oriented Programming (Java) & Database Management Systems (DBMS)");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(210, 230, 248));

        pnlBrand.add(lblTitle);
        pnlBrand.add(lblSub);
        banner.add(pnlBrand, BorderLayout.WEST);

        // Right Status Indicator & Config Button
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        pnlRight.setBackground(UITheme.PRIMARY_COLOR);

        lblDbStatus = new JLabel("Checking MySQL...");
        lblDbStatus.setFont(UITheme.FONT_BOLD);
        lblDbStatus.setForeground(Color.WHITE);

        // Clean reliable text label with high contrast
        btnDbConfig = UITheme.createSecondaryButton("DB Settings");
        btnDbConfig.setToolTipText("Configure MySQL database host, port, credentials, and run schema");
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

        // 1. Connection Warning Banner (Shown only when database is unavailable)
        pnlOfflineBanner = new JPanel(new BorderLayout(10, 0));
        pnlOfflineBanner.setBackground(UITheme.WARNING_BG);
        pnlOfflineBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.WARNING_BORDER, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        pnlOfflineBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        pnlOfflineBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblOfflineMessage = new JLabel("MySQL Database is currently offline. Click 'DB Settings' in the top header to configure credentials or initialize tables.");
        lblOfflineMessage.setFont(UITheme.FONT_BOLD);
        lblOfflineMessage.setForeground(UITheme.WARNING_COLOR);
        pnlOfflineBanner.add(lblOfflineMessage, BorderLayout.WEST);

        JButton btnBannerConfig = UITheme.createPrimaryButton("Configure Database");
        btnBannerConfig.setFont(UITheme.FONT_SMALL);
        btnBannerConfig.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnBannerConfig.addActionListener(e -> btnDbConfig.doClick());
        pnlOfflineBanner.add(btnBannerConfig, BorderLayout.EAST);

        panel.add(pnlOfflineBanner);
        panel.add(Box.createVerticalStrut(12));

        // 2. Four Redesigned Summary Metric Cards
        JPanel pnlMetrics = new JPanel(new GridLayout(1, 4, 12, 12));
        pnlMetrics.setBackground(UITheme.BG_LIGHT);
        pnlMetrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        pnlMetrics.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStatPatients = new JLabel("--", SwingConstants.CENTER);
        lblSubPatients = new JLabel("Total Registered", SwingConstants.CENTER);
        lblStatDoctors = new JLabel("--", SwingConstants.CENTER);
        lblSubDoctors = new JLabel("Active on Roster", SwingConstants.CENTER);
        lblStatAppointments = new JLabel("--", SwingConstants.CENTER);
        lblSubAppointments = new JLabel("Scheduled Visits", SwingConstants.CENTER);
        lblStatBills = new JLabel("--", SwingConstants.CENTER);
        lblSubBills = new JLabel("Invoices Generated", SwingConstants.CENTER);

        pnlMetrics.add(createMetricCard("PATIENTS", lblStatPatients, lblSubPatients, new Color(30, 115, 185)));
        pnlMetrics.add(createMetricCard("DOCTORS", lblStatDoctors, lblSubDoctors, new Color(46, 125, 50)));
        pnlMetrics.add(createMetricCard("APPOINTMENTS", lblStatAppointments, lblSubAppointments, new Color(110, 60, 160)));
        pnlMetrics.add(createMetricCard("TOTAL BILLS", lblStatBills, lblSubBills, new Color(210, 105, 30)));

        panel.add(pnlMetrics);
        panel.add(Box.createVerticalStrut(16));

        // 3. Center Section: Left Quick Actions (40%), Right Viva Concepts (60%)
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(UITheme.BG_LIGHT);
        pnlCenter.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left: Quick Navigation Actions
        JPanel cardActions = createQuickActionsCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.38;
        gbc.insets = new Insets(0, 0, 0, 12);
        pnlCenter.add(cardActions, gbc);

        // Right: Academic Viva Concept Mapping
        JPanel cardViva = createVivaConceptCard();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.62;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlCenter.add(cardViva, gbc);

        panel.add(pnlCenter);
        return panel;
    }

    private JPanel createMetricCard(String title, JLabel lblValue, JLabel lblSubtext, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 10, 12)
        ));

        // Top Accent Strip
        JPanel topBar = new JPanel();
        topBar.setBackground(accentColor);
        topBar.setPreferredSize(new Dimension(0, 3));
        card.add(topBar, BorderLayout.NORTH);

        // Center Content: Category, Value, Subtext
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(UITheme.CARD_BG);

        JLabel lblCategory = new JLabel(title, SwingConstants.CENTER);
        lblCategory.setFont(UITheme.FONT_METRIC_LABEL);
        lblCategory.setForeground(UITheme.TEXT_MUTED);
        lblCategory.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue.setFont(UITheme.FONT_METRIC_VALUE);
        lblValue.setForeground(accentColor);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblSubtext.setFont(UITheme.FONT_SMALL);
        lblSubtext.setForeground(UITheme.TEXT_MUTED);
        lblSubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalStrut(4));
        center.add(lblCategory);
        center.add(Box.createVerticalStrut(2));
        center.add(lblValue);
        center.add(Box.createVerticalStrut(2));
        center.add(lblSubtext);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickActionsCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Quick Navigation Actions");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);

        JLabel lblSubtitle = new JLabel("Direct shortcuts to core hospital workflows");
        lblSubtitle.setFont(UITheme.FONT_SMALL);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        header.add(lblTitle);
        header.add(lblSubtitle);
        card.add(header, BorderLayout.NORTH);

        // Vertical action buttons list with explicit contrast and full readability
        JPanel pnlButtons = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlButtons.setBackground(Color.WHITE);

        JButton btnGoPatient = UITheme.createPrimaryButton("Register / Manage Patients");
        btnGoPatient.setToolTipText("Add new patients, search by ID/Name, or update patient info");
        btnGoPatient.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        JButton btnGoDoctor = UITheme.createSecondaryButton("View Doctors Directory");
        btnGoDoctor.setToolTipText("View registered doctors, specializations, and consultation fees");
        btnGoDoctor.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        JButton btnGoAppt = UITheme.createAccentButton("Schedule New Appointment");
        btnGoAppt.setToolTipText("Book patient-doctor appointments and manage consultation schedules");
        btnGoAppt.addActionListener(e -> tabbedPane.setSelectedIndex(3));

        JButton btnGoBill = UITheme.createSecondaryButton("Generate / View Invoices");
        btnGoBill.setToolTipText("Create consultation bills and view formal invoice receipts");
        btnGoBill.addActionListener(e -> tabbedPane.setSelectedIndex(4));

        pnlButtons.add(btnGoPatient);
        pnlButtons.add(btnGoDoctor);
        pnlButtons.add(btnGoAppt);
        pnlButtons.add(btnGoBill);

        card.add(pnlButtons, BorderLayout.CENTER);
        return card;
    }

    private JPanel createVivaConceptCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 8));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Academic Viva Concept Mapping");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);

        JLabel lblSubtitle = new JLabel("Quick reference mapping project components to syllabus concepts");
        lblSubtitle.setFont(UITheme.FONT_SMALL);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        header.add(lblTitle);
        header.add(lblSubtitle);
        card.add(header, BorderLayout.NORTH);

        // Use JEditorPane with text/html for proper word-wrapping without horizontal scrolling
        JEditorPane htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        htmlPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        String vivaHtml = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 11px; color: #212529; margin: 0; padding: 2px;'>" +
                "<div style='border: 1px solid #D6E4F0; background: #F8FBFE; padding: 8px; margin-bottom: 8px; border-radius: 4px;'>" +
                "  <b style='color: #184C78; font-size: 12px;'>[ OOPS with Java ]</b>" +
                "  <table style='font-size: 11px; margin-top: 4px; width: 100%; border-collapse: collapse;'>" +
                "    <tr><td style='width: 32%; vertical-align: top;'><b>Classes &amp; Objects:</b></td><td>Patient, Doctor, Appointment, Bill entities encapsulate domain state.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Inheritance:</b></td><td><code>Person</code> abstract base class extended by <code>Patient</code> and <code>Doctor</code>.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Polymorphism:</b></td><td>Overridden <code>getDetails()</code> method provides entity-specific string output.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Abstraction:</b></td><td>DAO interfaces (<code>PatientDao</code>, etc.) decouple callers from JDBC code.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Exception Handling:</b></td><td>Custom <code>ValidationException</code> and safe <code>SQLException</code> handling.</td></tr>" +
                "  </table>" +
                "</div>" +
                "<div style='border: 1px solid #D2E8DA; background: #F5FAF6; padding: 8px; border-radius: 4px;'>" +
                "  <b style='color: #2E7D32; font-size: 12px;'>[ DBMS with MySQL ]</b>" +
                "  <table style='font-size: 11px; margin-top: 4px; width: 100%; border-collapse: collapse;'>" +
                "    <tr><td style='width: 32%; vertical-align: top;'><b>Relational Schema:</b></td><td>4 tables with PRIMARY KEY, AUTO_INCREMENT, and CASCADE rules.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Foreign Keys:</b></td><td><code>Appointment</code> references Patient &amp; Doctor; <code>Bill</code> references Appointment.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Data Integrity:</b></td><td><code>NOT NULL</code> and <code>CHECK</code> constraints (age &gt; 0, amounts &ge; 0).</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>Parameterized SQL:</b></td><td>All queries use <code>PreparedStatement</code> to prevent SQL injection.</td></tr>" +
                "    <tr><td style='vertical-align: top;'><b>SQL Joins:</b></td><td><code>INNER JOIN</code> queries merge Patient and Doctor data for display.</td></tr>" +
                "  </table>" +
                "</div>" +
                "</body></html>";

        htmlPane.setText(vivaHtml);
        htmlPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(htmlPane);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFooterBar() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(6, 16, 6, 16)
        ));

        JLabel lblLeft = new JLabel("Hospital Management System v1.0 | Ready");
        lblLeft.setFont(UITheme.FONT_SMALL);
        lblLeft.setForeground(UITheme.TEXT_MUTED);

        JLabel lblRight = new JLabel("Frontend: Moksha  |  Database: Poojitha");
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
            lblDbStatus.setForeground(new Color(175, 235, 175)); // Crisp soft green on navy
            if (pnlOfflineBanner != null) {
                pnlOfflineBanner.setVisible(false);
            }
        } else {
            lblDbStatus.setText("● MySQL: Offline");
            lblDbStatus.setForeground(new Color(255, 170, 170)); // Clear red/coral indicator on navy
            if (pnlOfflineBanner != null) {
                pnlOfflineBanner.setVisible(true);
            }
        }
    }

    public void refreshOverviewStats() {
        boolean connected = DatabaseConnection.testConnection();

        if (!connected) {
            lblStatPatients.setText("--");
            lblSubPatients.setText("Database Offline");
            lblStatDoctors.setText("--");
            lblSubDoctors.setText("Database Offline");
            lblStatAppointments.setText("--");
            lblSubAppointments.setText("Database Offline");
            lblStatBills.setText("--");
            lblSubBills.setText("Database Offline");
            return;
        }

        try {
            int patients = patientService.getAllPatients().size();
            lblStatPatients.setText(String.valueOf(patients));
            lblSubPatients.setText("Total Registered");
        } catch (Exception e) {
            lblStatPatients.setText("--");
            lblSubPatients.setText("Unavailable");
        }

        try {
            int doctors = doctorService.getAllDoctors().size();
            lblStatDoctors.setText(String.valueOf(doctors));
            lblSubDoctors.setText("Active on Roster");
        } catch (Exception e) {
            lblStatDoctors.setText("--");
            lblSubDoctors.setText("Unavailable");
        }

        try {
            int apts = appointmentService.getAllAppointments().size();
            lblStatAppointments.setText(String.valueOf(apts));
            lblSubAppointments.setText("Scheduled Visits");
        } catch (Exception e) {
            lblStatAppointments.setText("--");
            lblSubAppointments.setText("Unavailable");
        }

        try {
            int bills = billingService.getAllBills().size();
            lblStatBills.setText(String.valueOf(bills));
            lblSubBills.setText("Invoices Generated");
        } catch (Exception e) {
            lblStatBills.setText("--");
            lblSubBills.setText("Unavailable");
        }
    }
}
