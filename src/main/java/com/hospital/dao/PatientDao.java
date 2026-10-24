package com.hospital.dao;

import com.hospital.model.Patient;
import java.sql.SQLException;
import java.util.List;

/**
 * Demonstrates Abstraction: DAO Interface for Patient entity.
 */
public interface PatientDao {
    boolean insert(Patient patient) throws SQLException;
    boolean update(Patient patient) throws SQLException;
    boolean delete(int patientId) throws SQLException;
    Patient findById(int patientId) throws SQLException;
    List<Patient> findAll() throws SQLException;
    List<Patient> searchByName(String name) throws SQLException;
}
