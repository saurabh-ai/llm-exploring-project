package com.javamastery.dataprocessor.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable record representing an employee
 * Demonstrates Java 14+ records for clean, immutable data structures
 */
public record Employee(
    Long id,
    String firstName,
    String lastName,
    String email,
    String department,
    String position,
    BigDecimal salary,
    LocalDate hireDate,
    int yearsOfExperience,
    String skills
) implements Comparable<Employee> {
    
    // Compact constructor for validation
    public Employee {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(department, "Department cannot be null");
        Objects.requireNonNull(salary, "Salary cannot be null");
        
        if (salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
    }
    
    // Convenience methods using functional programming
    public String fullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isHighEarner() {
        return salary.compareTo(new BigDecimal("100000")) > 0;
    }
    
    public boolean isSenior() {
        return yearsOfExperience >= 5;
    }
    
    public String getExperienceLevel() {
        return switch (yearsOfExperience) {
            case 0, 1 -> "Junior";
            case 2, 3, 4 -> "Mid-level";
            case 5, 6, 7, 8, 9 -> "Senior";
            default -> "Expert";
        };
    }
    
    // Implement Comparable for natural ordering by id
    @Override
    public int compareTo(Employee other) {
        return this.id.compareTo(other.id);
    }
}