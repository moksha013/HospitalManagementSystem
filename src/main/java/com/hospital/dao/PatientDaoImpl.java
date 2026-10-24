package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PatientDao using JDBC and PreparedStatement.
 */
public class PatientDaoImpl implements PatientDao {

    @Override
    public boolean insert(Patient patient) throws SQLException {
        String sql = "INSERT INTO Patient (full_name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getFullName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getAddress());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setPatientId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean update(Patient patient) throws SQLException {
        String sql = "UPDATE Patient SET full_name = ?, age = ?, gender = ?, phone = ?, address = ? WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getFullName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getAddress());
            stmt.setInt(6, patient.getPatientId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int patientId) throws SQLException {
        String sql = "DELETE FROM Patient WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Patient findById(int patientId) throws SQLException {
        String sql = "SELECT * FROM Patient WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPatient(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Patient> findAll() throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient ORDER BY patient_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToPatient(rs));
            }
        }
        return list;
    }

    @Override
    public List<Patient> searchByName(String name) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient WHERE LOWER(full_name) LIKE ? ORDER BY patient_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPatient(rs));
                }
            }
        }
        return list;
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("patient_id"),
                rs.getString("full_name"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("phone"),
                rs.getString("address")
        );
    }
}
