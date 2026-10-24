package com.hospital.model;

/**
 * Model class representing a Patient.
 * Demonstrates:
 * - Inheritance: Patient is-a Person
 * - Encapsulation: private fields with getters/setters
 * - Polymorphism: overrides getDetails()
 */
public class Patient extends Person {
    private int patientId;
    private int age;
    private String gender;
    private String address;

    public Patient() {
        super();
    }

    public Patient(int patientId, String fullName, int age, String gender, String phone, String address) {
        super(fullName, phone);
        this.patientId = patientId;
        this.age = age;
        this.gender = gender;
        this.address = address;
    }

    public Patient(String fullName, int age, String gender, String phone, String address) {
        super(fullName, phone);
        this.age = age;
        this.gender = gender;
        this.address = address;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getDetails() {
        return String.format("Patient #%d: %s | Age: %d | Gender: %s | Phone: %s | Address: %s",
                patientId, getFullName(), age, gender, getPhone(), address);
    }

    @Override
    public String toString() {
        return String.format("#%d - %s (%s, %dy)", patientId, getFullName(), gender, age);
    }
}
