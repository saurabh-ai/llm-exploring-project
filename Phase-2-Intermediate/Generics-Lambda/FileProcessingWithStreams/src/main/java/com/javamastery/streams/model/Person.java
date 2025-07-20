package com.javamastery.streams.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable data model representing a person with various attributes
 * Used for demonstrating stream operations and data transformations
 */
public record Person(
    String id,
    String firstName, 
    String lastName,
    String email,
    int age,
    String city,
    String country,
    double salary,
    String department,
    LocalDate joinDate
) {
    
    /**
     * Compact constructor for validation
     */
    public Person {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(country, "Country cannot be null");
        Objects.requireNonNull(department, "Department cannot be null");
        Objects.requireNonNull(joinDate, "Join date cannot be null");
        
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    /**
     * Get full name by combining first and last name
     */
    public String fullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Check if person is senior (age >= 50)
     */
    public boolean isSenior() {
        return age >= 50;
    }
    
    /**
     * Check if person is high earner (salary >= 100000)
     */
    public boolean isHighEarner() {
        return salary >= 100_000;
    }
    
    /**
     * Calculate years of experience from join date
     */
    public long yearsOfExperience() {
        return java.time.temporal.ChronoUnit.YEARS.between(joinDate, LocalDate.now());
    }
}