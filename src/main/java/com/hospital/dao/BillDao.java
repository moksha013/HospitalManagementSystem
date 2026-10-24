package com.hospital.dao;

import com.hospital.model.Bill;
import java.sql.SQLException;
import java.util.List;

/**
 * Demonstrates Abstraction: DAO Interface for Billing operations.
 */
public interface BillDao {
    boolean insert(Bill bill) throws SQLException;
    boolean updatePaymentStatus(int billId, String newStatus) throws SQLException;
    boolean delete(int billId) throws SQLException;
    Bill findById(int billId) throws SQLException;
    Bill findByAppointmentId(int appointmentId) throws SQLException;
    List<Bill> findAllWithDetails() throws SQLException;
}
