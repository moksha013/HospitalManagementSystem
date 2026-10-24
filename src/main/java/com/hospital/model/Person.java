package com.hospital.model;

/**
 * Demonstrates OOPS Concepts:
 * 1. Abstraction (abstract base class for common person attributes)
 * 2. Encapsulation (private fields with getters and setters)
 * 3. Polymorphism (abstract method getDetails() implemented by subclasses)
 */
public abstract class Person {
    private String fullName;
    private String phone;

    public Person() {
    }

    public Person(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Polymorphic method to display entity-specific details.
     */
    public abstract String getDetails();

    @Override
    public String toString() {
        return fullName + " (" + phone + ")";
    }
}
