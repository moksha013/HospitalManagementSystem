# Hospital Management System (HMS)
**An Integrated Project for OOPS with Java & Database Management Systems (DBMS)**

---

## 👥 Team Responsibilities
| Team Member | Primary Responsibility | Scope Owned |
| :--- | :--- | :--- |
| **Moksha** | **Frontend / Java Swing UI & User-Facing Screens** | Swing Dashboard, navigation tabs, Patient registration/update/search screens, Doctor directory, Appointment booking, Consultation Billing, input validation, UI error feedback. |
| **Poojitha** | **Database / MySQL Schema & Data Design** | Relational schema (`schema.sql`), primary/foreign key constraints, CHECK constraints, CRUD queries, table relationships, test dataset. |
| **Both** | **OOPS & Integration** | Domain model design (`Person`, `Patient`, `Doctor`, `Appointment`, `Bill`), JDBC PreparedStatement flow, testing, report, presentation viva. |

---

## 🏛 Project Architecture & Folder Structure

The project strictly follows layered separation of concerns:
```
HospitalManagementSystem/
├── pom.xml                                      # Maven build configuration
├── README.md                                    # Project documentation & viva guide
├── db.properties                                # MySQL connection settings
├── build.bat / run.bat                          # One-click Windows build and launch
├── lib/
│   └── mysql-connector-j-8.3.0.jar              # MySQL JDBC Driver
├── database/
│   └── schema.sql                               # Table creation DDL + sample records
└── src/main/java/com/hospital/
    ├── Main.java                                # Application launcher & EDT init
    ├── TestWorkflow.java                        # Automated business rule tests
    ├── model/                                   # Domain Entities (OOPS)
    │   ├── Person.java                          # Abstract base entity (Inheritance)
    │   ├── Patient.java                         # Patient model (extends Person)
    │   ├── Doctor.java                          # Doctor model (extends Person)
    │   ├── Appointment.java                     # Appointment model with joined fields
    │   └── Bill.java                            # Bill model with joined fields
    ├── dao/                                     # Data Access Layer (PreparedStatement)
    │   ├── PatientDao.java & PatientDaoImpl.java
    │   ├── DoctorDao.java & DoctorDaoImpl.java
    │   ├── AppointmentDao.java & AppointmentDaoImpl.java
    │   └── BillDao.java & BillDaoImpl.java
    ├── service/                                 # Business Logic & Field Validation
    │   ├── ValidationException.java             # Custom validation exception
    │   ├── PatientService.java                  # Patient rules & validations
    │   ├── DoctorService.java                   # Doctor rules & validations
    │   ├── AppointmentService.java              # FK verification & scheduling
    │   └── BillingService.java                  # Invoicing rules & fee calculations
    ├── ui/                                      # Java Swing GUI (Moksha's Work)
    │   ├── UITheme.java                         # Consistent styling & palette
    │   ├── MainDashboard.java                   # Frame, tabs & metrics overview
    │   ├── PatientPanel.java                    # Patient registration, search, update
    │   ├── DoctorPanel.java                     # Doctor directory & add doctor dialog
    │   ├── AppointmentPanel.java                # Appointment booking & status updates
    │   ├── BillingPanel.java                    # Bill generation & receipt view
    │   └── DatabaseConfigDialog.java            # MySQL credentials & schema runner
    └── util/
        └── DatabaseConnection.java              # Connection manager & error handler
```

---

## 🧠 Concepts Demonstrated for Viva

### 1. OOPS with Java Checklist
- **Classes and Objects:** `Patient`, `Doctor`, `Appointment`, and `Bill` represent real-world hospital domain entities.
- **Encapsulation:** Private attributes with strongly typed, validated public getter and setter methods.
- **Inheritance:** `Person` is an abstract base class containing common fields (`fullName`, `phone`). `Patient` and `Doctor` both inherit from `Person`, avoiding code duplication.
- **Polymorphism:** The abstract method `getDetails()` in `Person` is overridden differently by `Patient` and `Doctor`.
- **Abstraction:** DAO Interfaces (`PatientDao`, `DoctorDao`, `AppointmentDao`, `BillDao`) decouple data access contracts from MySQL-specific JDBC implementation details.
- **Exception Handling:** Custom `ValidationException` captures form errors cleanly; `SQLException` handles DB errors without crashing.
- **Collections:** `List<Patient>`, `List<Doctor>`, etc. are populated by the DAO and bound to `DefaultTableModel` inside Swing tables.

### 2. DBMS Checklist
- **Relational Integrity:**
  - `Patient`: `patient_id` (Primary Key, AUTO_INCREMENT), `CHECK (age > 0 AND age <= 125)`
  - `Doctor`: `doctor_id` (Primary Key, AUTO_INCREMENT), `CHECK (consultation_fee >= 0)`
  - `Appointment`: `patient_id` (Foreign Key referencing Patient), `doctor_id` (Foreign Key referencing Doctor)
  - `Bill`: `appointment_id` (Foreign Key referencing Appointment), `CHECK (amount >= 0)`
- **SQL PreparedStatement:** All queries use parameterized statements (`?`) to prevent SQL injection and ensure type safety.
- **INNER JOIN Queries:** Demonstrates multi-table joins to retrieve patient name, doctor name, and specialization alongside appointment and billing records.

---

## 🚀 How to Set Up & Run

### Prerequisites
1. **Java Development Kit (JDK 17 or higher)**
2. **MySQL Server 8.0** (or higher)

### Step 1: Database Setup
You can set up the database in **either** of two ways:

#### Option A: Directly from the Java Swing UI (Easiest)
1. Start the app using `run.bat` (or via IDE).
2. Click the **"⚙ DB Settings"** button in the top-right corner.
3. Enter your MySQL root password.
4. Click **"Initialize Schema"** — this automatically creates the `hospital_db` database, all four tables, and populates the sample data!
5. Click **"Save & Connect"**.

#### Option B: Via MySQL Command Line / Workbench
```bash
mysql -u root -p < database/schema.sql
```

### Step 2: Configure Credentials
Edit `db.properties` in the project root:
```properties
db.host=localhost
db.port=3306
db.name=hospital_db
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

### Step 3: Compile and Run

#### Using Batch Files (Windows Command Prompt or PowerShell):
```cmd
build.bat
run.bat
```

#### Or using Maven:
```bash
mvn compile exec:java
```

#### Or using direct javac/java:
```bash
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d target/classes src/main/java/com/hospital/**/*.java
java -cp "lib/mysql-connector-j-8.3.0.jar;target/classes" com.hospital.Main
```

---

## 🧪 Acceptance Testing Checklist (Section 11)

| Test Scenario | Action | Expected Result | Status |
| :--- | :--- | :--- | :--- |
| **1. Register Valid Patient** | Fill all fields in Patient tab and click "Register Patient". | Patient is inserted into MySQL, alert shows new ID, appears in table. | Verified |
| **2. Blank Fields Validation** | Leave Name or Phone blank and click Register. | Warning modal displays exact validation error without DB call. | Verified |
| **3. Invalid Age** | Enter age `-5` or `200`. | Warning modal: "Age must be a valid number between 1 and 125". | Verified |
| **4. Search Patient** | Search by ID (e.g. `1`) or name substring (e.g. `Aarav`). | Table filters to matching patient records; clear message if not found. | Verified |
| **5. Update Patient** | Click a row in Patient table, edit contact details, click "Update Patient". | Changes persist in MySQL and update in table immediately. | Verified |
| **6. Doctor Directory** | Switch to Doctors tab. | Displays doctor list, specialization, and consultation fees from MySQL. | Verified |
| **7. Book Appointment (Valid)** | Select Patient and Doctor, set date/time, click "Book Appointment". | Record created with foreign keys; shows patient & doctor names via JOIN. | Verified |
| **8. Book Appointment (Invalid FK)** | Check "Enter IDs manually", enter Patient ID `99999` and Doctor ID `88888`. | App shows friendly error: "Patient with ID 99999 does not exist." | Verified |
| **9. Consultation Billing** | Select appointment, fee auto-fills from doctor, click "Generate Bill". | Bill saved with amount, status, date; receipt modal preview available. | Verified |
| **10. MySQL Unavailable** | Run app when MySQL is stopped or password is wrong. | App opens cleanly, top badge displays "MySQL: Disconnected", no unhandled crash. | Verified |
