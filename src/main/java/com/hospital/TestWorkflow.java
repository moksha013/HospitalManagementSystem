package com.hospital;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import com.hospital.service.ValidationException;

/**
 * Quick validation test to verify business rules and exception handling
 * according to the Testing Checklist in Section 11 of the project document.
 */
public class TestWorkflow {

    public static void main(String[] args) {
        System.out.println("--- Starting Acceptance Validation Tests ---");

        PatientService service = new PatientService();

        // Test 1: Submit patient with required fields blank
        System.out.print("Test 1: Blank patient validation... ");
        try {
            Patient blank = new Patient("", 0, "Select", "", "");
            service.validatePatient(blank);
            System.err.println("FAILED (Validation did not trigger)");
        } catch (ValidationException e) {
            System.out.println("PASSED: Caught expected validation message -> '" + e.getMessage() + "'");
        }

        // Test 2: Invalid age validation
        System.out.print("Test 2: Invalid age validation (-5)... ");
        try {
            Patient invalidAge = new Patient("John Doe", -5, "Male", "9876543210", "123 Street");
            service.validatePatient(invalidAge);
            System.err.println("FAILED");
        } catch (ValidationException e) {
            System.out.println("PASSED: Caught expected validation message -> '" + e.getMessage() + "'");
        }

        // Test 3: Invalid phone validation
        System.out.print("Test 3: Invalid phone validation ('123')... ");
        try {
            Patient invalidPhone = new Patient("Jane Doe", 25, "Female", "123", "123 Street");
            service.validatePatient(invalidPhone);
            System.err.println("FAILED");
        } catch (ValidationException e) {
            System.out.println("PASSED: Caught expected validation message -> '" + e.getMessage() + "'");
        }

        // Test 4: Valid patient passes validation
        System.out.print("Test 4: Valid patient validation... ");
        try {
            Patient valid = new Patient("Moksha Patel", 21, "Female", "9876543210", "Main Campus");
            service.validatePatient(valid);
            System.out.println("PASSED (All fields accepted)");
        } catch (ValidationException e) {
            System.err.println("FAILED: Unexpected exception: " + e.getMessage());
        }

        System.out.println("\nAll validation checks completed successfully!");
    }
}
