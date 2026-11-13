# Comprehensive Project Report
## Hospital Management System (HMS)
**An Integrated Academic Project for Object-Oriented Programming using Java (OOPS) & Database Management Systems (DBMS)**

---

**Student Name:** Moksha  
**GitHub Repository:** [https://github.com/moksha013/HospitalManagementSystem](https://github.com/moksha013/HospitalManagementSystem)  
**Technical Stack:** Java 17+, Java Swing, MySQL 8.0, JDBC, Maven, Git  

---

## 1. Introduction & Project Overview

### 1.1 Problem Statement
Modern healthcare facilities manage extensive volumes of sensitive operational data daily, including patient demographics, doctor specialties, consultation schedules, and billing records. Manual paperwork or fragmented spreadsheets lead to data redundancy, scheduling conflicts, lost billing invoices, and poor auditing. 

The **Hospital Management System (HMS)** addresses these challenges by providing a desktop application backed by a relational database management system (MySQL). The application allows hospital administrative staff to register patients, maintain doctor directories, book and update consultations, and generate consultation invoices.

### 1.2 Objectives
1. **Apply OOPS Principles in Java:** Utilize encapsulation, inheritance, polymorphism, and abstraction to create a maintainable, clean desktop application architecture.
2. **Implement Relational Database Principles (DBMS):** Enforce data integrity, primary and foreign key constraints, checks, cascaded updates/deletions, and optimized relational joins using MySQL.
3. **Seamless Integration via JDBC:** Use parameterized `PreparedStatement` interfaces to prevent SQL injection vulnerabilities and establish reliable client-server communication.
4. **Resilient User Interface:** Provide a Java Swing desktop interface with responsive layouts, field-level validation, and non-blocking database offline indicators.

### 1.3 Project Scope
* **In Scope (Version 1):**
  - Patient registration, validation, ID/name searching, and real-time updates.
  - Doctor directory with specialty filtering and registration.
  - Appointment booking linked with foreign keys to patients and doctors, with status workflow (`Scheduled` $\rightarrow$ `Completed` $\rightarrow$ `Cancelled`).
  - Consultation billing with doctor fee lookup, payment status tracking, and printable invoice dialogs.
  - Integrated MySQL connection manager to configure credentials and initialize database schemas directly from the UI.
* **Out of Scope (Future Work):**
  - Payment gateway APIs, pharmacy inventories, multi-branch hospital synchronization, and role-based cryptographic authentication.

---

## 2. System Architecture & Requirements

### 2.1 Technology Stack & Tools
| Technology | Role in System | Key Features Used |
| :--- | :--- | :--- |
| **Java (JDK 17+)** | Core Programming Language | Strong typing, OOP features, Lambda expressions, Modern Date/Time API (`LocalDate`, `LocalTime`) |
| **Java Swing** | Desktop User Interface | `JFrame`, `JTabbedPane`, `JTable`, `DefaultTableModel`, `BoxLayout`, `GridBagLayout` |
| **MySQL 8.0** | Relational Database Engine | InnoDB engine, Foreign Keys, `CHECK` constraints, `AUTO_INCREMENT`, ACID transactions |
| **JDBC** | Database Connectivity | `DriverManager`, `Connection`, `PreparedStatement`, `ResultSet` |
| **Apache Maven** | Build & Dependency Management | `pom.xml`, dependency management (`mysql-connector-j:8.3.0`) |
| **Git & GitHub** | Version Control & Collaboration | Commits, branching strategy, remote backup |

### 2.2 Layered System Architecture
The application strictly enforces a **3-tier architectural separation**:
```
  +--------------------------------------------------------------+
  |                   Presentation Layer (UI)                   |
  |     MainDashboard, PatientPanel, DoctorPanel, BillingPanel   |
  +--------------------------------------------------------------+
                                | (Events / View Models)
                                v
  +--------------------------------------------------------------+
  |                     Business Service Layer                  |
  |   PatientService, DoctorService, AppointmentService, Billing |
  |           (ValidationException, Business Constraints)        |
  +--------------------------------------------------------------+
                                | (Domain Entities)
                                v
  +--------------------------------------------------------------+
  |                   Data Access Layer (DAO)                    |
  |   PatientDao, DoctorDao, AppointmentDao, BillDao (Interfaces)|
  |                PatientDaoImpl, ... (PreparedStatement)       |
  +--------------------------------------------------------------+
                                | (JDBC / TCP Port 3306)
                                v
  +--------------------------------------------------------------+
  |                     Database Engine (DBMS)                   |
  |            MySQL Server: Patient, Doctor, Appointment, Bill  |
  +--------------------------------------------------------------+
```

---

## 3. Database Design & DBMS Implementation

### 3.1 Entity-Relationship (ER) Structure
```
       1             N                   N             1
[ Patient ] <------------> [ Appointment ] <------------> [ Doctor ]
  patient_id (PK)             appointment_id (PK)         doctor_id (PK)
                              patient_id (FK)
                              doctor_id (FK)
                                    |
                                    | 1
                                    |
                                    v 0..1
                                [ Bill ]
                                  bill_id (PK)
                                  appointment_id (FK)
```

### 3.2 Relational Schema Specifications
1. **`Patient` Table:**
   - `patient_id` INT AUTO_INCREMENT PRIMARY KEY
   - `full_name` VARCHAR(100) NOT NULL
   - `age` INT NOT NULL CHECK (`age > 0 AND age <= 125`)
   - `gender` VARCHAR(20) NOT NULL
   - `phone` VARCHAR(20) NOT NULL
   - `address` VARCHAR(255) NOT NULL
2. **`Doctor` Table:**
   - `doctor_id` INT AUTO_INCREMENT PRIMARY KEY
   - `full_name` VARCHAR(100) NOT NULL
   - `specialization` VARCHAR(100) NOT NULL
   - `phone` VARCHAR(20) NOT NULL
   - `consultation_fee` DECIMAL(10,2) NOT NULL CHECK (`consultation_fee >= 0.00`)
3. **`Appointment` Table:**
   - `appointment_id` INT AUTO_INCREMENT PRIMARY KEY
   - `patient_id` INT NOT NULL (FK $\rightarrow$ `Patient.patient_id` ON DELETE CASCADE ON UPDATE CASCADE)
   - `doctor_id` INT NOT NULL (FK $\rightarrow$ `Doctor.doctor_id` ON DELETE CASCADE ON UPDATE CASCADE)
   - `appointment_date` DATE NOT NULL
   - `appointment_time` TIME NOT NULL
   - `status` VARCHAR(30) NOT NULL DEFAULT 'Scheduled'
4. **`Bill` Table:**
   - `bill_id` INT AUTO_INCREMENT PRIMARY KEY
   - `appointment_id` INT NOT NULL (FK $\rightarrow$ `Appointment.appointment_id` ON DELETE CASCADE ON UPDATE CASCADE)
   - `amount` DECIMAL(10,2) NOT NULL CHECK (`amount >= 0.00`)
   - `payment_status` VARCHAR(30) NOT NULL DEFAULT 'Unpaid'
   - `bill_date` DATE NOT NULL

### 3.3 Relational SQL Joins & Aggregate Analytical Queries
```sql
-- Query 1: Multi-Table INNER JOIN for Consultation Appointments
SELECT 
    a.appointment_id,
    p.full_name AS patient_name,
    p.phone AS patient_phone,
    d.full_name AS doctor_name,
    d.specialization,
    a.appointment_date,
    a.appointment_time,
    a.status
FROM Appointment a
INNER JOIN Patient p ON a.patient_id = p.patient_id
INNER JOIN Doctor d ON a.doctor_id = d.doctor_id
ORDER BY a.appointment_date DESC;

-- Query 2: Aggregate Consultations per Doctor
SELECT 
    d.doctor_id,
    d.full_name,
    d.specialization,
    COUNT(a.appointment_id) AS total_appointments
FROM Doctor d
LEFT JOIN Appointment a ON d.doctor_id = a.doctor_id
GROUP BY d.doctor_id, d.full_name, d.specialization;
```

---

## 4. OOPS Concepts in Java Implementation

| OOPS Concept | How It Is Implemented in This Project | Reference File |
| :--- | :--- | :--- |
| **Classes & Objects** | Models represent hospital domain entities with attributes and constructors. | `Patient.java`, `Doctor.java`, `Appointment.java`, `Bill.java` |
| **Encapsulation** | All fields are strictly `private`. Public getters and validated setters expose safe manipulation. | `Person.java`, `Patient.java` |
| **Inheritance** | `Patient` and `Doctor` inherit common state (`fullName`, `phone`) from the `Person` superclass, avoiding code duplication. | `public class Patient extends Person` |
| **Polymorphism** | Abstract method `public abstract String getDetails()` defined in `Person` is overridden differently by `Patient` and `Doctor`. | `Person.java#getDetails()` |
| **Abstraction** | DAO Interfaces (`PatientDao`, `DoctorDao`, etc.) hide JDBC and SQL details from the UI and Service layers. | `PatientDao.java`, `DoctorDao.java` |
| **Exception Handling** | Custom `ValidationException` enforces business rules; `SQLException` is safely handled without crashing the application. | `ValidationException.java`, `DatabaseConnection.java` |
| **Collections Framework** | `ArrayList<Patient>` and `List<Appointment>` collect query results and bind dynamically to Swing `DefaultTableModel`. | `PatientDaoImpl.java` |

---

## 5. Testing & Verification

All 10 required acceptance scenarios from the syllabus have been executed and verified:

| # | Test Scenario | Input Data | Expected Result | Actual Result | Status |
| :-: | :--- | :--- | :--- | :--- | :-: |
| 1 | Register Valid Patient | Full name, age 29, phone 9876543210 | Patient saved, ID generated, table updated | Patient saved with generated ID; refreshed in table | **PASS** |
| 2 | Blank Required Fields | Empty Name or Phone | `ValidationException` raised; warning popup displayed | Pop-up: *"Full Name is required and cannot be blank."* | **PASS** |
| 3 | Invalid Age Input | Age = `-5` or `150` | Input rejected by validation before DB execution | Pop-up: *"Age must be a valid number between 1 and 125."* | **PASS** |
| 4 | Search Patient by ID/Name | ID `1` or substring `"Aarav"` | Target record isolated in table | Table isolated matching row accurately | **PASS** |
| 5 | Update Existing Patient | Select row, update phone, click Update | Modified values persisted in DB and table | Persistent update reloaded in table | **PASS** |
| 6 | Load Doctor Directory | Switch to Doctors tab | Doctor rows load with fees and specialties | All 5 sample doctors loaded | **PASS** |
| 7 | Book Valid Appointment | Select Patient #1 and Doctor #1 | Row inserted; JOIN displays patient & doctor names | Appointment booked and joined names displayed | **PASS** |
| 8 | Invalid Foreign Key | Manual Patient ID `99999` | App rejects non-existent foreign key reference | Pop-up: *"Patient with ID 99999 does not exist."* | **PASS** |
| 9 | Generate Consultation Bill | Select Appointment #1 | Bill generated with doctor fee; receipt preview | Bill created; formatted modal receipt displays | **PASS** |
| 10 | MySQL Unavailable | Stop MySQL service | App opens safely; banner indicates offline state | UI remains responsive with clear "MySQL Offline" banner | **PASS** |

---

## 6. Conclusion & Future Enhancements

The **Hospital Management System** successfully accomplishes all educational objectives of both Object-Oriented Programming with Java and Database Management Systems. By implementing strict multi-tier layering, parameterization, and Swing components, the application ensures high stability, maintainability, and clean user experience.

### Future Scope
1. **Authentication & Role-Based Access Control (RBAC):** Separate portals for Doctors, Receptionists, and Administrators with hashed credentials (`BCrypt`).
2. **Pharmacy & Laboratory Inventory Modules:** Extend the relational schema to manage prescription medications, stock balances, and lab test reports.
3. **Automated PDF Export:** Direct export of consultation bills and patient case histories using libraries like Apache PDFBox or iText.
