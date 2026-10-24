package com.hospital.dao;

import com.hospital.model.Doctor;
import java.sql.SQLException;
import java.util.List;

/**
 * Demonstrates Abstraction: DAO Interface for Doctor entity.
 */
public interface DoctorDao {
    boolean insert(Doctor doctor) throws SQLException;
    boolean update(Doctor doctor) throws SQLException;
    boolean delete(int doctorId) throws SQLException;
    Doctor findById(int doctorId) throws SQLException;
    List<Doctor> findAll() throws SQLException;
    List<Doctor> searchByNameOrSpecialization(String query) throws SQLException;
}
