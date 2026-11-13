-- ====================================================================
-- Hospital Management System - Complete Database Schema & Query Suite
-- Subject: Database Management Systems (DBMS) + OOPS with Java
-- Author: Moksha
-- Description: Production-ready DDL, relational integrity constraints,
--              sample test records, and analytical queries for Viva.
-- ====================================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Clean existing tables in reverse dependency order
DROP TABLE IF EXISTS Bill;
DROP TABLE IF EXISTS Appointment;
DROP TABLE IF EXISTS Doctor;
DROP TABLE IF EXISTS Patient;

-- ====================================================================
-- 1. Patient Table
-- Demonstrates: Primary Key, Auto Increment, Check Constraint, Not Null
-- ====================================================================
CREATE TABLE Patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age > 0 AND age <= 125),
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- ====================================================================
-- 2. Doctor Table
-- Demonstrates: Primary Key, Auto Increment, Non-negative Check Constraint
-- ====================================================================
CREATE TABLE Doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL CHECK (consultation_fee >= 0.00)
);

-- ====================================================================
-- 3. Appointment Table
-- Demonstrates: Composite Foreign Keys referencing Patient & Doctor,
--               Referential Integrity with CASCADE actions.
-- ====================================================================
CREATE TABLE Appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Scheduled',
    CONSTRAINT fk_appointment_patient 
        FOREIGN KEY (patient_id) REFERENCES Patient(patient_id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_appointment_doctor 
        FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ====================================================================
-- 4. Bill Table
-- Demonstrates: 1-to-1/1-to-0 Relationship with Appointment via FK,
--               Decimal precision for financial amounts.
-- ====================================================================
CREATE TABLE Bill (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0.00),
    payment_status VARCHAR(30) NOT NULL DEFAULT 'Unpaid',
    bill_date DATE NOT NULL,
    CONSTRAINT fk_bill_appointment 
        FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ====================================================================
-- Sample Educational Test Data
-- ====================================================================

-- 1. Patients
INSERT INTO Patient (patient_id, full_name, age, gender, phone, address) VALUES
(1, 'Aarav Sharma', 29, 'Male', '9876543210', '12 Lake View Road, Bengaluru'),
(2, 'Ananya Patel', 34, 'Female', '9811223344', '45 Green Park, Mumbai'),
(3, 'Rohan Verma', 52, 'Male', '9722334455', '78 MG Road, Pune'),
(4, 'Sneha Iyer', 23, 'Female', '9633445566', '10 Temple Street, Chennai'),
(5, 'Vikramaditya Das', 45, 'Male', '9544332211', '88 Hill Cart Road, Siliguri');

-- 2. Doctors
INSERT INTO Doctor (doctor_id, full_name, specialization, phone, consultation_fee) VALUES
(1, 'Dr. Rajesh Rao', 'Cardiology', '9900112233', 750.00),
(2, 'Dr. Priya Nambiar', 'Pediatrics', '9911223344', 500.00),
(3, 'Dr. Vikram Malhotra', 'Orthopedics', '9922334455', 600.00),
(4, 'Dr. Kavita Reddy', 'General Medicine', '9933445566', 400.00),
(5, 'Dr. Sameer Joshi', 'Neurology', '9944556677', 850.00);

-- 3. Appointments
INSERT INTO Appointment (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status) VALUES
(1, 1, 1, '2026-10-25', '10:00:00', 'Completed'),
(2, 2, 2, '2026-10-25', '11:30:00', 'Completed'),
(3, 3, 3, '2026-10-26', '14:00:00', 'Scheduled'),
(4, 4, 4, '2026-10-26', '16:30:00', 'Scheduled'),
(5, 5, 5, '2026-10-27', '09:30:00', 'Scheduled');

-- 4. Bills
INSERT INTO Bill (bill_id, appointment_id, amount, payment_status, bill_date) VALUES
(1, 1, 750.00, 'Paid', '2026-10-25'),
(2, 2, 500.00, 'Paid', '2026-10-25'),
(3, 3, 600.00, 'Unpaid', '2026-10-26');

-- Reset Auto Increment to avoid collision with manual sample keys
ALTER TABLE Patient AUTO_INCREMENT = 6;
ALTER TABLE Doctor AUTO_INCREMENT = 6;
ALTER TABLE Appointment AUTO_INCREMENT = 6;
ALTER TABLE Bill AUTO_INCREMENT = 4;

-- ====================================================================
-- DBMS Concepts Showcase Queries (For Demonstration & Viva)
-- ====================================================================

-- 1. INNER JOIN across 3 tables: Appointment + Patient + Doctor
-- SELECT 
--     a.appointment_id,
--     p.full_name AS patient_name,
--     p.phone AS patient_phone,
--     d.full_name AS doctor_name,
--     d.specialization,
--     a.appointment_date,
--     a.appointment_time,
--     a.status
-- FROM Appointment a
-- INNER JOIN Patient p ON a.patient_id = p.patient_id
-- INNER JOIN Doctor d ON a.doctor_id = d.doctor_id
-- ORDER BY a.appointment_date DESC;

-- 2. Aggregate Query: Count appointments per Doctor with Group By
-- SELECT 
--     d.doctor_id,
--     d.full_name,
--     d.specialization,
--     COUNT(a.appointment_id) AS total_appointments
-- FROM Doctor d
-- LEFT JOIN Appointment a ON d.doctor_id = a.doctor_id
-- GROUP BY d.doctor_id, d.full_name, d.specialization;

-- 3. Financial Aggregate Query: Total Revenue and Unpaid Bills Summary
-- SELECT 
--     payment_status,
--     COUNT(*) AS bill_count,
--     SUM(amount) AS total_amount
-- FROM Bill
-- GROUP BY payment_status;
