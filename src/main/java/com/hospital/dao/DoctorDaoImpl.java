package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of DoctorDao using JDBC and PreparedStatement.
 */
public class DoctorDaoImpl implements DoctorDao {

    @Override
    public boolean insert(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO Doctor (full_name, specialization, phone, consultation_fee) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, doctor.getFullName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getPhone());
            stmt.setBigDecimal(4, doctor.getConsultationFee());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setDoctorId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean update(Doctor doctor) throws SQLException {
        String sql = "UPDATE Doctor SET full_name = ?, specialization = ?, phone = ?, consultation_fee = ? WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctor.getFullName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getPhone());
            stmt.setBigDecimal(4, doctor.getConsultationFee());
            stmt.setInt(5, doctor.getDoctorId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int doctorId) throws SQLException {
        String sql = "DELETE FROM Doctor WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Doctor findById(int doctorId) throws SQLException {
        String sql = "SELECT * FROM Doctor WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDoctor(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Doctor> findAll() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctor ORDER BY doctor_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToDoctor(rs));
            }
        }
        return list;
    }

    @Override
    public List<Doctor> searchByNameOrSpecialization(String query) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctor WHERE LOWER(full_name) LIKE ? OR LOWER(specialization) LIKE ? ORDER BY doctor_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String q = "%" + query.toLowerCase() + "%";
            stmt.setString(1, q);
            stmt.setString(2, q);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToDoctor(rs));
                }
            }
        }
        return list;
    }

    private Doctor mapRowToDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("doctor_id"),
                rs.getString("full_name"),
                rs.getString("specialization"),
                rs.getString("phone"),
                rs.getBigDecimal("consultation_fee")
        );
    }
}
