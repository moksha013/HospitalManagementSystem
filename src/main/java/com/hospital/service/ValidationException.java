package com.hospital.service;

/**
 * Custom Exception thrown when user input violates validation or business rules.
 * Demonstrates Exception Handling in Java.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
