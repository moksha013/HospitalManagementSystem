package com.hospital.service;

import com.hospital.dao.AppointmentDao;
import com.hospital.dao.AppointmentDaoImpl;
import com.hospital.dao.DoctorDao;
import com.hospital.dao.DoctorDaoImpl;
import com.hospital.dao.PatientDao;
import com.hospital.dao.PatientDaoImpl;
import com.hospital.model.Appointment;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Appointment management.
 * Verifies foreign key constraints and scheduling rules.
 */
public class AppointmentService {

    private final AppointmentDao appointmentDao;
    private final PatientDao patientDao;
    private final DoctorDao doctorDao;

    public AppointmentService() {
        this.appointmentDao = new AppointmentDaoImpl();
        this.patientDao = new PatientDaoImpl();
        this.doctorDao = new DoctorDaoImpl();
    }

    public AppointmentService(AppointmentDao appointmentDao, PatientDao patientDao, DoctorDao doctorDao) {
        this.appointmentDao = appointmentDao;
        this.patientDao = patientDao;
        this.doctorDao = doctorDao;
    }

    public void validateAppointment(Appointment appointment) throws ValidationException, SQLException {
        if (appointment == null) {
            throw new ValidationException("Appointment data cannot be null.");
        }
        if (appointment.getPatientId() <= 0) {
            throw new ValidationException("Please select or enter a valid Patient ID.");
        }
        if (appointment.getDoctorId() <= 0) {
            throw new ValidationException("Please select or enter a valid Doctor ID.");
        }
        if (appointment.getAppointmentDate() == null) {
            throw new ValidationException("Appointment Date is required.");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new ValidationException("Appointment Time is required.");
        }
        if (appointment.getStatus() == null || appointment.getStatus().trim().isEmpty()) {
            appointment.setStatus("Scheduled");
        }

        // Verify Patient exists (Foreign key pre-check for friendly message)
        if (patientDao.findById(appointment.getPatientId()) == null) {
            throw new ValidationException("Patient with ID " + appointment.getPatientId() + " does not exist.");
        }

        // Verify Doctor exists
        if (doctorDao.findById(appointment.getDoctorId()) == null) {
            throw new ValidationException("Doctor with ID " + appointment.getDoctorId() + " does not exist.");
        }
    }

    public boolean bookAppointment(Appointment appointment) throws ValidationException, SQLException {
        validateAppointment(appointment);
        return appointmentDao.insert(appointment);
    }

    public boolean updateStatus(int appointmentId, String newStatus) throws ValidationException, SQLException {
        if (appointmentId <= 0) {
            throw new ValidationException("Invalid Appointment ID.");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new ValidationException("Status cannot be empty.");
        }
        return appointmentDao.updateStatus(appointmentId, newStatus.trim());
    }

    public boolean deleteAppointment(int appointmentId) throws SQLException {
        return appointmentDao.delete(appointmentId);
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        return appointmentDao.findById(appointmentId);
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        return appointmentDao.findAllWithDetails();
    }
}
