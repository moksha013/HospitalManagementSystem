package com.hospital.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class representing a Bill.
 * Demonstrates Encapsulation and Domain Entity modeling.
 */
public class Bill {
    private int billId;
    private int appointmentId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDate billDate;

    // Joined display fields
    private String patientName;
    private String doctorName;

    public Bill() {
    }

    public Bill(int billId, int appointmentId, BigDecimal amount, String paymentStatus, LocalDate billDate) {
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    public Bill(int appointmentId, BigDecimal amount, String paymentStatus, LocalDate billDate) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Override
    public String toString() {
        return String.format("Bill #%d: Apt #%d | Amount: $%.2f | Status: %s | Date: %s",
                billId, appointmentId, amount, paymentStatus, billDate);
    }
}
