package com.hospital.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Styling and theme constants for a clean, modern Java Swing desktop UI.
 * Provides accessible, high-contrast controls with explicit hover, pressed, and disabled states.
 */
public class UITheme {

    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(24, 76, 120);     // Deep Medical Blue #184C78
    public static final Color PRIMARY_HOVER = new Color(32, 102, 160);    // #2066A0
    public static final Color PRIMARY_PRESSED = new Color(18, 53, 84);    // #123554

    public static final Color ACCENT_COLOR = new Color(0, 150, 136);      // Teal Accent #009688
    public static final Color ACCENT_HOVER = new Color(0, 175, 158);      // #00AF9E
    public static final Color ACCENT_PRESSED = new Color(0, 121, 107);    // #00796B

    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);     // Forest Green #2E7D32
    public static final Color DANGER_COLOR = new Color(198, 40, 40);      // Crimson Red #C62828
    public static final Color DANGER_HOVER = new Color(211, 47, 47);      // #D32F2F
    public static final Color DANGER_PRESSED = new Color(183, 28, 28);    // #B71C1C

    public static final Color WARNING_COLOR = new Color(230, 81, 0);      // Dark Orange #E65100
    public static final Color WARNING_BG = new Color(255, 243, 224);       // Light Orange Banner #FFF3E0
    public static final Color WARNING_BORDER = new Color(255, 183, 77);   // #FFB74D

    public static final Color BG_LIGHT = new Color(245, 247, 250);        // Off-white background #F5F7FA
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = new Color(33, 37, 41);          // #212529
    public static final Color TEXT_MUTED = new Color(108, 117, 125);      // #6C757D
    public static final Color BORDER_COLOR = new Color(222, 226, 230);    // #DEE2E6

    public static final Color DISABLED_BG = new Color(233, 236, 239);     // #E9ECEF
    public static final Color DISABLED_TEXT = new Color(140, 145, 150);   // #8C9196

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC_VALUE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_METRIC_LABEL = new Font("Segoe UI", Font.BOLD, 11);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(PRIMARY_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(PRIMARY_COLOR);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(PRIMARY_PRESSED);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(btn.contains(e.getPoint()) ? PRIMARY_HOVER : PRIMARY_COLOR);
                }
            }
        });
        return btn;
    }

    public static JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(ACCENT_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(ACCENT_COLOR);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(ACCENT_PRESSED);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(btn.contains(e.getPoint()) ? ACCENT_HOVER : ACCENT_COLOR);
                }
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border normalBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 210), 1),
                BorderFactory.createEmptyBorder(9, 15, 9, 15)
        );
        Border hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(9, 15, 9, 15)
        );
        btn.setBorder(normalBorder);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(238, 244, 252));
                    btn.setForeground(PRIMARY_HOVER);
                    btn.setBorder(hoverBorder);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(PRIMARY_COLOR);
                    btn.setBorder(normalBorder);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(220, 235, 250));
                    btn.setForeground(PRIMARY_PRESSED);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (btn.isEnabled()) {
                    if (btn.contains(e.getPoint())) {
                        btn.setBackground(new Color(238, 244, 252));
                        btn.setForeground(PRIMARY_HOVER);
                    } else {
                        btn.setBackground(Color.WHITE);
                        btn.setForeground(PRIMARY_COLOR);
                        btn.setBorder(normalBorder);
                    }
                }
            }
        });
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(DANGER_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(DANGER_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(DANGER_COLOR);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(DANGER_PRESSED);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(btn.contains(e.getPoint()) ? DANGER_HOVER : DANGER_COLOR);
                }
            }
        });
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
                new EmptyBorder(14, 16, 14, 16)
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
