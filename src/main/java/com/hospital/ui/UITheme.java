package com.hospital.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Styling and theme constants for a clean, modern Java Swing desktop UI.
 */
public class UITheme {

    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(24, 76, 120);     // Deep Medical Blue
    public static final Color PRIMARY_HOVER = new Color(32, 102, 160);
    public static final Color ACCENT_COLOR = new Color(0, 150, 136);      // Teal Accent
    public static final Color ACCENT_HOVER = new Color(0, 175, 158);
    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);     // Green
    public static final Color DANGER_COLOR = new Color(198, 40, 40);      // Crimson Red
    public static final Color WARNING_COLOR = new Color(239, 108, 0);     // Orange
    public static final Color BG_LIGHT = new Color(245, 247, 250);        // Off-white background
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = new Color(33, 37, 41);
    public static final Color TEXT_MUTED = new Color(108, 117, 125);
    public static final Color BORDER_COLOR = new Color(222, 226, 230);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(DANGER_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(28);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(220, 236, 252));
        table.setSelectionForeground(TEXT_MAIN);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(238, 242, 246));
        header.setForeground(TEXT_MAIN);
        header.setPreferredSize(new Dimension(0, 32));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Center align text for numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }
}
