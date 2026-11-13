# Viva Voce Master Guide - Hospital Management System
**Prepared for:** Moksha  
**Subjects:** Object-Oriented Programming with Java & Database Management Systems (DBMS)

---

## 🎯 Part 1: Java & Object-Oriented Programming (OOPS) Questions

### Q1: What design pattern did you use for database access, and why?
> **Answer:** "I used the **Data Access Object (DAO) Pattern** combined with **Abstraction**. I created DAO interfaces like `PatientDao`, `DoctorDao`, `AppointmentDao`, and `BillDao`, and implemented them in `PatientDaoImpl`, etc. This completely decouples the UI and Service layers from JDBC and SQL syntax. If the database ever changes, the UI doesn't need a single modification."

### Q2: How does your project demonstrate Inheritance and Polymorphism?
> **Answer:** 
> - **Inheritance:** "I designed an abstract base class called `Person` which holds common attributes like `fullName` and `phone`. Both `Patient` and `Doctor` inherit from `Person`, preventing code duplication."
> - **Polymorphism:** "`Person` declares an abstract method `public abstract String getDetails()`. Both `Patient` and `Doctor` override this method to return entity-specific formatted strings (e.g., patient age and address vs. doctor specialty and consultation fee)."

### Q3: What is Encapsulation, and how is it maintained?
> **Answer:** "All class fields in my models (`patientId`, `age`, `consultationFee`, etc.) are declared `private`. They can only be accessed or modified via public getter and setter methods. Additionally, business validation is performed before setting values, preserving object state integrity."

### Q4: How is Exception Handling implemented in your application?
> **Answer:** 
> 1. **Custom Exceptions:** I created `ValidationException` which is thrown whenever user input violates business rules (e.g., blank names, age outside 1–125, invalid phone length).
> 2. **Checked SQL Exceptions:** All database interactions catch `SQLException`. Rather than letting the application crash, user-friendly error dialogs are displayed, and connection errors trigger the offline indicator on the dashboard.

### Q5: Why did you separate the UI code from the Service and DAO layers?
> **Answer:** "To follow the **Single Responsibility Principle (SRP)** and avoid coupling. Event handlers in Swing buttons only handle user actions and delegate processing to `PatientService`, which runs validation and delegates persistence to `PatientDao`. There is zero SQL code inside any Swing panel."

---

## 🎯 Part 2: Database Management Systems (DBMS) Questions

### Q1: Explain the Primary Keys and Foreign Keys in your schema.
> **Answer:** 
> - `Patient` has `patient_id` as **PRIMARY KEY** (`AUTO_INCREMENT`).
> - `Doctor` has `doctor_id` as **PRIMARY KEY** (`AUTO_INCREMENT`).
> - `Appointment` has `appointment_id` as **PRIMARY KEY**, and two **FOREIGN KEYS**: `patient_id` referencing `Patient(patient_id)` and `doctor_id` referencing `Doctor(doctor_id)`.
> - `Bill` has `bill_id` as **PRIMARY KEY**, and `appointment_id` as a **FOREIGN KEY** referencing `Appointment(appointment_id)`.

### Q2: What referential actions did you specify for your Foreign Keys, and why?
> **Answer:** "I specified `ON DELETE CASCADE ON UPDATE CASCADE`. If a patient record or appointment is deleted or updated, all related appointment or billing rows are automatically updated or deleted by the MySQL engine, preventing orphaned records and foreign key constraint violations."

### Q3: What `CHECK` constraints did you enforce?
> **Answer:** 
> 1. In `Patient`: `CHECK (age > 0 AND age <= 125)` guarantees biological validity.
> 2. In `Doctor`: `CHECK (consultation_fee >= 0.00)` prevents negative pricing.
> 3. In `Bill`: `CHECK (amount >= 0.00)` ensures hospital bills cannot have negative values.

### Q4: Why did you use `PreparedStatement` instead of `Statement`?
> **Answer:** 
> 1. **Security:** `PreparedStatement` completely prevents **SQL Injection** by treating user inputs as literal parameters (`?`) rather than executable SQL fragments.
> 2. **Performance:** Prepared statements are pre-compiled by the database engine, resulting in faster query execution when invoked repeatedly.

### Q5: Give an example of an `INNER JOIN` in your project.
> **Answer:** "When fetching appointments for the dashboard table, the `Appointment` table only stores `patient_id` and `doctor_id`. I use an `INNER JOIN` with the `Patient` and `Doctor` tables to retrieve the patient's full name, doctor's full name, and doctor's specialty in a single query:
> ```sql
> SELECT a.appointment_id, p.full_name AS patient_name, d.full_name AS doctor_name, d.specialization, a.appointment_date, a.appointment_time, a.status
> FROM Appointment a
> INNER JOIN Patient p ON a.patient_id = p.patient_id
> INNER JOIN Doctor d ON a.doctor_id = d.doctor_id;
> ```"

### Q6: What happens if MySQL is offline when the application starts?
> **Answer:** "The application gracefully handles the failure in `DatabaseConnection.getConnection()`. Instead of crashing, the dashboard displays a coral-red `● MySQL: Offline` badge, shows an informative configuration banner, and sets the metric cards to `-- (Database Offline)`. Users can click 'DB Settings' right in the UI to reconnect once the service is started."
