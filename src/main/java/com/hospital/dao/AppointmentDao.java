package com.hospital.dao;

import com.hospital.model.Appointment;
import java.sql.SQLException;
import java.util.List;

/**
 * Demonstrates Abstraction: DAO Interface for Appointment operations.
 */
public interface AppointmentDao {
    boolean insert(Appointment appointment) throws SQLException;
    boolean update(Appointment appointment) throws SQLException;
    boolean updateStatus(int appointmentId, String newStatus) throws SQLException;
    boolean delete(int appointmentId) throws SQLException;
    Appointment findById(int appointmentId) throws SQLException;
    List<Appointment> findAllWithDetails() throws SQLException;
    List<Appointment> findByPatientId(int patientId) throws SQLException;
    List<Appointment> findByDoctorId(int doctorId) throws SQLException;
}
