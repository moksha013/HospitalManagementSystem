package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of AppointmentDao demonstrating SQL INNER JOINs.
 */
public class AppointmentDaoImpl implements AppointmentDao {

    @Override
    public boolean insert(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO Appointment (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(5, appointment.getStatus());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setAppointmentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean update(Appointment appointment) throws SQLException {
        String sql = "UPDATE Appointment SET patient_id = ?, doctor_id = ?, appointment_date = ?, appointment_time = ?, status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(5, appointment.getStatus());
            stmt.setInt(6, appointment.getAppointmentId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int appointmentId, String newStatus) throws SQLException {
        String sql = "UPDATE Appointment SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int appointmentId) throws SQLException {
        String sql = "DELETE FROM Appointment WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Appointment findById(int appointmentId) throws SQLException {
        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name, d.specialization AS doctor_specialization " +
                     "FROM Appointment a " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.appointment_id = ?";
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
    public List<Appointment> findAllWithDetails() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name, d.specialization AS doctor_specialization " +
                     "FROM Appointment a " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowWithDetails(rs));
            }
        }
        return list;
    }

    @Override
    public List<Appointment> findByPatientId(int patientId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name, d.specialization AS doctor_specialization " +
                     "FROM Appointment a " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowWithDetails(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Appointment> findByDoctorId(int doctorId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name, d.specialization AS doctor_specialization " +
                     "FROM Appointment a " +
                     "INNER JOIN Patient p ON a.patient_id = p.patient_id " +
                     "INNER JOIN Doctor d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.doctor_id = ? ORDER BY a.appointment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowWithDetails(rs));
                }
            }
        }
        return list;
    }

    private Appointment mapRowWithDetails(ResultSet rs) throws SQLException {
        Appointment apt = new Appointment(
                rs.getInt("appointment_id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getDate("appointment_date").toLocalDate(),
                rs.getTime("appointment_time").toLocalTime(),
                rs.getString("status")
        );
        apt.setPatientName(rs.getString("patient_name"));
        apt.setDoctorName(rs.getString("doctor_name"));
        apt.setDoctorSpecialization(rs.getString("doctor_specialization"));
        return apt;
    }
}
