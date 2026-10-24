package com.hospital.service;

import com.hospital.dao.AppointmentDao;
import com.hospital.dao.AppointmentDaoImpl;
import com.hospital.dao.BillDao;
import com.hospital.dao.BillDaoImpl;
import com.hospital.model.Bill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Billing and Invoicing operations.
 */
public class BillingService {

    private final BillDao billDao;
    private final AppointmentDao appointmentDao;

    public BillingService() {
        this.billDao = new BillDaoImpl();
        this.appointmentDao = new AppointmentDaoImpl();
    }

    public BillingService(BillDao billDao, AppointmentDao appointmentDao) {
        this.billDao = billDao;
        this.appointmentDao = appointmentDao;
    }

    public void validateBill(Bill bill) throws ValidationException, SQLException {
        if (bill == null) {
            throw new ValidationException("Bill data cannot be null.");
        }
        if (bill.getAppointmentId() <= 0) {
            throw new ValidationException("Please select or enter a valid Appointment ID.");
        }
        if (bill.getAmount() == null || bill.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Bill amount cannot be negative.");
        }
        if (bill.getPaymentStatus() == null || bill.getPaymentStatus().trim().isEmpty()) {
            bill.setPaymentStatus("Unpaid");
        }
        if (bill.getBillDate() == null) {
            throw new ValidationException("Bill date is required.");
        }

        // Verify that the appointment exists
        if (appointmentDao.findById(bill.getAppointmentId()) == null) {
            throw new ValidationException("Appointment with ID " + bill.getAppointmentId() + " does not exist.");
        }
    }

    public boolean generateBill(Bill bill) throws ValidationException, SQLException {
        validateBill(bill);
        return billDao.insert(bill);
    }

    public boolean updatePaymentStatus(int billId, String newStatus) throws ValidationException, SQLException {
        if (billId <= 0) {
            throw new ValidationException("Invalid Bill ID.");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new ValidationException("Payment status cannot be empty.");
        }
        return billDao.updatePaymentStatus(billId, newStatus.trim());
    }

    public boolean deleteBill(int billId) throws SQLException {
        return billDao.delete(billId);
    }

    public Bill getBillById(int billId) throws SQLException {
        return billDao.findById(billId);
    }

    public Bill getBillByAppointmentId(int appointmentId) throws SQLException {
        return billDao.findByAppointmentId(appointmentId);
    }

    public List<Bill> getAllBills() throws SQLException {
        return billDao.findAllWithDetails();
    }
}
