-- ====================================================================
-- Hospital Management System - Database Schema & Sample Data
-- Subject: Database Management Systems (DBMS) + OOPS with Java
-- Partners: Poojitha (Database / DBMS) & Moksha (Frontend / UI)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- 1. Patient Table
DROP TABLE IF EXISTS Bill;
DROP TABLE IF EXISTS Appointment;
DROP TABLE IF EXISTS Doctor;
DROP TABLE IF EXISTS Patient;

CREATE TABLE Patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age > 0 AND age <= 125),
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- 2. Doctor Table
CREATE TABLE Doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL CHECK (consultation_fee >= 0.00)
);

-- 3. Appointment Table
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

-- 4. Bill Table
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
-- Sample Fictional Test Data (Safe, Educational Records)
-- ====================================================================

-- Insert Sample Patients
INSERT INTO Patient (patient_id, full_name, age, gender, phone, address) VALUES
(1, 'Aarav Sharma', 29, 'Male', '9876543210', '12 Lake View Road, Bengaluru'),
(2, 'Ananya Patel', 34, 'Female', '9811223344', '45 Green Park, Mumbai'),
(3, 'Rohan Verma', 52, 'Male', '9722334455', '78 MG Road, Pune'),
(4, 'Sneha Iyer', 23, 'Female', '9633445566', '10 Temple Street, Chennai');

-- Insert Sample Doctors
INSERT INTO Doctor (doctor_id, full_name, specialization, phone, consultation_fee) VALUES
(1, 'Dr. Rajesh Rao', 'Cardiology', '9900112233', 750.00),
(2, 'Dr. Priya Nambiar', 'Pediatrics', '9911223344', 500.00),
(3, 'Dr. Vikram Malhotra', 'Orthopedics', '9922334455', 600.00),
(4, 'Dr. Kavita Reddy', 'General Medicine', '9933445566', 400.00);

-- Insert Sample Appointments
INSERT INTO Appointment (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status) VALUES
(1, 1, 1, '2026-10-25', '10:00:00', 'Scheduled'),
(2, 2, 2, '2026-10-25', '11:30:00', 'Completed'),
(3, 3, 3, '2026-10-26', '14:00:00', 'Scheduled'),
(4, 4, 4, '2026-10-26', '16:30:00', 'Scheduled');

-- Insert Sample Bills
INSERT INTO Bill (bill_id, appointment_id, amount, payment_status, bill_date) VALUES
(1, 2, 500.00, 'Paid', '2026-10-25'),
(2, 1, 750.00, 'Unpaid', '2026-10-25');

-- Reset Auto Increment to continue sequentially after sample IDs
ALTER TABLE Patient AUTO_INCREMENT = 5;
ALTER TABLE Doctor AUTO_INCREMENT = 5;
ALTER TABLE Appointment AUTO_INCREMENT = 5;
ALTER TABLE Bill AUTO_INCREMENT = 3;

-- ====================================================================
-- Verification Queries (Can be used for Demo & Viva)
-- ====================================================================

-- 1. View all appointments with Patient and Doctor names (INNER JOIN)
-- SELECT 
--     a.appointment_id,
--     p.full_name AS patient_name,
--     d.full_name AS doctor_name,
--     d.specialization,
--     a.appointment_date,
--     a.appointment_time,
--     a.status
-- FROM Appointment a
-- INNER JOIN Patient p ON a.patient_id = p.patient_id
-- INNER JOIN Doctor d ON a.doctor_id = d.doctor_id;

-- 2. View all bills with Patient and Doctor details
-- SELECT 
--     b.bill_id,
--     b.appointment_id,
--     p.full_name AS patient_name,
--     d.full_name AS doctor_name,
--     b.amount,
--     b.payment_status,
--     b.bill_date
-- FROM Bill b
-- INNER JOIN Appointment a ON b.appointment_id = a.appointment_id
-- INNER JOIN Patient p ON a.patient_id = p.patient_id
-- INNER JOIN Doctor d ON a.doctor_id = d.doctor_id;
