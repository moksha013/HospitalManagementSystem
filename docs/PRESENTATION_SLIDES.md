# Hospital Management System (HMS) - Presentation Slides Structure
**Author:** Moksha  
**Course:** Object-Oriented Programming (Java) & Database Management Systems (DBMS)

---

### Slide 1: Title Slide
* **Title:** Hospital Management System (HMS)
* **Subtitle:** An Integrated Desktop Application for OOPS Java & Relational DBMS
* **Presenter:** Moksha
* **Tech Stack:** Java 17 | Java Swing | MySQL 8.0 | JDBC | Maven

---

### Slide 2: Problem Statement & Objectives
* **Challenges in Hospital Administration:**
  * High-risk paperwork errors, patient appointment double-booking, and untracked billing.
* **Core Project Goals:**
  * Build a desktop interface for hospital staff to manage patients, doctors, appointments, and bills.
  * Implement strict OOP design patterns in Java with zero SQL leakage in the UI.
  * Enforce relational data integrity using MySQL primary/foreign key constraints and joins.

---

### Slide 3: System Architecture & Design
* **3-Tier Layered Architecture:**
  1. **Presentation Layer (Swing UI):** `MainDashboard`, `PatientPanel`, `DoctorPanel`, `AppointmentPanel`, `BillingPanel`, `UITheme`.
  2. **Service Layer (Business Logic & Validation):** `PatientService`, `DoctorService`, `AppointmentService`, `BillingService`, `ValidationException`.
  3. **Data Access Layer (JDBC DAO):** Interface contracts (`PatientDao`, etc.) with `PreparedStatement` implementations.
  4. **Persistence Layer (DBMS):** MySQL database (`hospital_db`).

---

### Slide 4: OOPS Concepts Demonstrated
* **Classes & Objects:** Domain entities modeling hospital operations (`Patient`, `Doctor`, `Appointment`, `Bill`).
* **Encapsulation:** Private attributes with strongly-typed public getters and validated setters.
* **Inheritance:** `Person` base abstract class extended by `Patient` and `Doctor`.
* **Polymorphism:** Abstract method `getDetails()` polymorphically implemented across entities.
* **Abstraction:** DAO interfaces decouple business callers from MySQL implementation details.
* **Exception Handling:** Custom `ValidationException` + safe `SQLException` handling without crashing.

---

### Slide 5: Database Design (DBMS) & Schema
* **Relational Tables & Keys:**
  * `Patient` (PK: `patient_id`, `CHECK` age 1–125)
  * `Doctor` (PK: `doctor_id`, `CHECK` fee $\ge$ 0.00)
  * `Appointment` (PK: `appointment_id`, FK: `patient_id`, FK: `doctor_id` with `CASCADE`)
  * `Bill` (PK: `bill_id`, FK: `appointment_id`, `CHECK` amount $\ge$ 0.00)
* **ER Diagram Relationship:**
  * `Patient (1) <----> (M) Appointment (M) <----> (1) Doctor`
  * `Appointment (1) <----> (0..1) Bill`

---

### Slide 6: SQL Queries & Relational Joins
* **Multi-table `INNER JOIN`:**
  * Merges `Appointment`, `Patient`, and `Doctor` to retrieve doctor specializations and patient contact info in a single call.
* **Aggregate Functions:**
  * `COUNT(appointment_id)` per doctor to measure hospital workload.
  * `SUM(amount)` grouped by `payment_status` for revenue auditing.
* **Security via `PreparedStatement`:**
  * Eliminates SQL Injection risks through parameterized `?` placeholders.

---

### Slide 7: Live Demo & Key Screens
1. **Overview Dashboard:** Live metrics counters, dynamic offline/online MySQL indicators, and quick shortcuts.
2. **Patient Management:** Validated registration form, search filter, click-to-edit table.
3. **Doctor Directory:** Specialty lookup, consultation fees, and add doctor modal.
4. **Appointment Scheduling:** Foreign-key dropdowns and status updates (`Scheduled` $\rightarrow$ `Completed`).
5. **Billing & Invoices:** Consultation fee calculation and printable invoice receipt popup.

---

### Slide 8: Acceptance Testing Results
* **10 / 10 Tests Passed:**
  * Patient registration & boundary age checks.
  * Non-existent foreign key rejection with friendly alerts.
  * Persistent database updates.
  * Safe recovery when MySQL server is disconnected.

---

### Slide 9: Conclusion & Future Scope
* **Key Takeaways:** Clean code separation, responsive desktop UX, robust relational database integration.
* **Future Work:**
  * Role-based access control (RBAC) with cryptographic passwords.
  * Pharmacy & lab test management modules.
  * Automated PDF invoice generation.
