package com.hospital.model;

import java.math.BigDecimal;

/**
 * Model class representing a Doctor.
 * Demonstrates:
 * - Inheritance: Doctor is-a Person
 * - Encapsulation: private fields with getters/setters
 * - Polymorphism: overrides getDetails()
 */
public class Doctor extends Person {
    private int doctorId;
    private String specialization;
    private BigDecimal consultationFee;

    public Doctor() {
        super();
    }

    public Doctor(int doctorId, String fullName, String specialization, String phone, BigDecimal consultationFee) {
        super(fullName, phone);
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public Doctor(String fullName, String specialization, String phone, BigDecimal consultationFee) {
        super(fullName, phone);
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String getDetails() {
        return String.format("Dr. %s | ID: %d | Specialization: %s | Phone: %s | Fee: $%.2f",
                getFullName(), doctorId, specialization, getPhone(), consultationFee);
    }

    @Override
    public String toString() {
        return String.format("#%d - %s (%s)", doctorId, getFullName(), specialization);
    }
}
