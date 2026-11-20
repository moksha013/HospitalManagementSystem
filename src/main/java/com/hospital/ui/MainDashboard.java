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
 * Main Java Swing Dashboard Frame for MediTrack.
 * Author: Moksha
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
        super("MediTrack");

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

        // 1. Top Header Banner
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
        banner.setBorder(new EmptyBorder(14, 22, 14, 22));

        // Left Branding
        JPanel pnlBrand = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlBrand.setBackground(UITheme.PRIMARY_COLOR);

        JLabel lblTitle = new JLabel("MEDITRACK");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Healthcare & Hospital Management Platform");
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

        // 2. Four Summary Metric Cards
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

        // 3. Center Section: Quick Actions + System Information Card
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 16, 16));
        pnlCenter.setBackground(UITheme.BG_LIGHT);
        pnlCenter.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left Card: Quick Actions
        JPanel cardActions = createQuickActionsCard();
        pnlCenter.add(cardActions);

        // Right Card: System Overview & Status Summary
        JPanel cardSystem = createSystemSummaryCard();
        pnlCenter.add(cardSystem);

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

        // Center Content
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

    private JPanel createSystemSummaryCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("System Modules & Operations");
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.PRIMARY_COLOR);

        JLabel lblSubtitle = new JLabel("Operational overview of active management modules");
        lblSubtitle.setFont(UITheme.FONT_SMALL);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);

        header.add(lblTitle);
        header.add(lblSubtitle);
        card.add(header, BorderLayout.NORTH);

        JPanel pnlModules = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlModules.setBackground(Color.WHITE);

        pnlModules.add(createModuleItem("Patient Registration & Records", "Manage patient profiles, demographics, contact details, and update history.", new Color(30, 115, 185)));
        pnlModules.add(createModuleItem("Doctor Staff Directory", "Directory of doctors, clinical specializations, contact numbers, and fees.", new Color(46, 125, 50)));
        pnlModules.add(createModuleItem("Appointment Scheduling", "Book consultations, link patient & doctor records, and track visit status.", new Color(110, 60, 160)));
        pnlModules.add(createModuleItem("Billing & Invoicing", "Auto-populate fees, track payment status (Paid/Unpaid), and generate receipts.", new Color(210, 105, 30)));

        card.add(pnlModules, BorderLayout.CENTER);
        return card;
    }

    private JPanel createModuleItem(String title, String desc, Color dotColor) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(new Color(248, 250, 252));
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlText.setBackground(new Color(248, 250, 252));

        JLabel lblT = new JLabel(title);
        lblT.setFont(UITheme.FONT_BOLD);
        lblT.setForeground(UITheme.TEXT_MAIN);

        JLabel lblD = new JLabel(desc);
        lblD.setFont(UITheme.FONT_SMALL);
        lblD.setForeground(UITheme.TEXT_MUTED);

        pnlText.add(lblT);
        pnlText.add(lblD);

        // Colored status bar on the left
        JPanel bar = new JPanel();
        bar.setBackground(dotColor);
        bar.setPreferredSize(new Dimension(4, 0));

        item.add(bar, BorderLayout.WEST);
        item.add(pnlText, BorderLayout.CENTER);

        return item;
    }

    private JPanel createFooterBar() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(7, 20, 7, 20)
        ));

        JLabel lblLeft = new JLabel("MediTrack v1.0 | Ready");
        lblLeft.setFont(UITheme.FONT_SMALL);
        lblLeft.setForeground(UITheme.TEXT_MUTED);

        footer.add(lblLeft, BorderLayout.WEST);
        return footer;
    }

    public void updateDbStatusBadge() {
        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            lblDbStatus.setText("● MySQL: Connected (" + DatabaseConnection.getDatabase() + ")");
            lblDbStatus.setForeground(new Color(175, 235, 175));
            if (pnlOfflineBanner != null) {
                pnlOfflineBanner.setVisible(false);
            }
        } else {
            lblDbStatus.setText("● MySQL: Offline");
            lblDbStatus.setForeground(new Color(255, 170, 170));
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
