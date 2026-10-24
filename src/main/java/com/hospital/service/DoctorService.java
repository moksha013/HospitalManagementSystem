package com.hospital.service;

import com.hospital.dao.DoctorDao;
import com.hospital.dao.DoctorDaoImpl;
import com.hospital.model.Doctor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Doctor operations and validations.
 */
public class DoctorService {

    private final DoctorDao doctorDao;

    public DoctorService() {
        this.doctorDao = new DoctorDaoImpl();
    }

    public DoctorService(DoctorDao doctorDao) {
        this.doctorDao = doctorDao;
    }

    public void validateDoctor(Doctor doctor) throws ValidationException {
        if (doctor == null) {
            throw new ValidationException("Doctor information cannot be empty.");
        }
        if (doctor.getFullName() == null || doctor.getFullName().trim().isEmpty()) {
            throw new ValidationException("Doctor full name is required.");
        }
        if (doctor.getSpecialization() == null || doctor.getSpecialization().trim().isEmpty()) {
            throw new ValidationException("Specialization is required.");
        }
        if (doctor.getPhone() == null || doctor.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }
        if (doctor.getConsultationFee() == null || doctor.getConsultationFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Consultation fee cannot be negative.");
        }
    }

    public boolean registerDoctor(Doctor doctor) throws ValidationException, SQLException {
        validateDoctor(doctor);
        return doctorDao.insert(doctor);
    }

    public boolean updateDoctor(Doctor doctor) throws ValidationException, SQLException {
        if (doctor.getDoctorId() <= 0) {
            throw new ValidationException("Invalid Doctor ID for update.");
        }
        validateDoctor(doctor);
        return doctorDao.update(doctor);
    }

    public Doctor getDoctorById(int doctorId) throws SQLException {
        return doctorDao.findById(doctorId);
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        return doctorDao.findAll();
    }

    public List<Doctor> searchDoctors(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return getAllDoctors();
        }
        return doctorDao.searchByNameOrSpecialization(query.trim());
    }
}
