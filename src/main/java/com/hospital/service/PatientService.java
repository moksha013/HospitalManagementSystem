package com.hospital.service;

import com.hospital.dao.PatientDao;
import com.hospital.dao.PatientDaoImpl;
import com.hospital.model.Patient;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Patient operations.
 * Enforces business logic and validation rules before delegating to DAO.
 */
public class PatientService {

    private final PatientDao patientDao;

    public PatientService() {
        this.patientDao = new PatientDaoImpl();
    }

    public PatientService(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    public void validatePatient(Patient patient) throws ValidationException {
        if (patient == null) {
            throw new ValidationException("Patient information cannot be empty.");
        }
        if (patient.getFullName() == null || patient.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full Name is required and cannot be blank.");
        }
        if (patient.getFullName().trim().length() > 100) {
            throw new ValidationException("Full Name cannot exceed 100 characters.");
        }
        if (patient.getAge() <= 0 || patient.getAge() > 125) {
            throw new ValidationException("Age must be a valid number between 1 and 125.");
        }
        if (patient.getGender() == null || patient.getGender().trim().isEmpty() || 
            patient.getGender().equalsIgnoreCase("Select")) {
            throw new ValidationException("Please select a valid gender.");
        }
        if (patient.getPhone() == null || patient.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }
        String cleanPhone = patient.getPhone().replaceAll("[\\s\\-\\(\\)]", "");
        if (cleanPhone.length() < 7 || cleanPhone.length() > 15) {
            throw new ValidationException("Phone number must contain between 7 and 15 digits.");
        }
        if (patient.getAddress() == null || patient.getAddress().trim().isEmpty()) {
            throw new ValidationException("Address is required.");
        }
    }

    public boolean registerPatient(Patient patient) throws ValidationException, SQLException {
        validatePatient(patient);
        return patientDao.insert(patient);
    }

    public boolean updatePatient(Patient patient) throws ValidationException, SQLException {
        if (patient.getPatientId() <= 0) {
            throw new ValidationException("Invalid Patient ID for update.");
        }
        validatePatient(patient);
        return patientDao.update(patient);
    }

    public boolean deletePatient(int patientId) throws SQLException {
        return patientDao.delete(patientId);
    }

    public Patient getPatientById(int patientId) throws SQLException {
        return patientDao.findById(patientId);
    }

    public List<Patient> getAllPatients() throws SQLException {
        return patientDao.findAll();
    }

    public List<Patient> searchPatients(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientDao.searchByName(name.trim());
    }
}
