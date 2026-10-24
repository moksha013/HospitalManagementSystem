package com.hospital.dao;

import com.hospital.model.Bill;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BillDao demonstrating SQL INNER JOINs for consultation bills.
 */
public class BillDaoImpl implements BillDao {

    @Override
    public boolean insert(Bill bill) throws SQLException {
        String sql = "INSERT INTO Bill (appointment_id, amount, payment_status, bill_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bill.getAppointmentId());
            stmt.setBigDecimal(2, bill.getAmount());
            stmt.setString(3, bill.getPaymentStatus());
            stmt.setDate(4, Date.valueOf(bill.getBillDate()));

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bill.setBillId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean updatePaymentStatus(int billId, String newStatus) throws SQLException {
        String sql = "UPDATE Bill SET payment_status = ? WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, billId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int billId) throws SQLException {
        String sql = "DELETE FROM Bill WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Bill findById(int billId) throws SQLException {
        String sql = "SELECT b.*, p.full_name AS patient_name, d.full_name AS doctor_name " +
                     "FROM Bill b " +
                     "INNER JOIN Appointment a ON b.appointment_id = a.appointment_id " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "WHERE b.bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowWithDetails(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Bill findByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT b.*, p.full_name AS patient_name, d.full_name AS doctor_name " +
                     "FROM Bill b " +
                     "INNER JOIN Appointment a ON b.appointment_id = a.appointment_id " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "WHERE b.appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowWithDetails(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Bill> findAllWithDetails() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.*, p.full_name AS patient_name, d.full_name AS doctor_name " +
                     "FROM Bill b " +
                     "INNER JOIN Appointment a ON b.appointment_id = a.appointment_id " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY b.bill_date DESC, b.bill_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowWithDetails(rs));
            }
        }
        return list;
    }

    private Bill mapRowWithDetails(ResultSet rs) throws SQLException {
        Bill bill = new Bill(
                rs.getInt("bill_id"),
                rs.getInt("appointment_id"),
                rs.getBigDecimal("amount"),
                rs.getString("payment_status"),
                rs.getDate("bill_date").toLocalDate()
        );
        bill.setPatientName(rs.getString("patient_name"));
        bill.setDoctorName(rs.getString("doctor_name"));
        return bill;
    }
}
