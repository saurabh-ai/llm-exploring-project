package com.javamastery.dataprocessor.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@DisplayName("Employee Model Tests")
class EmployeeTest {
    
    @Test
    @DisplayName("Should create valid employee with all fields")
    void testEmployeeCreation() {
        // Given
        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@company.com";
        String department = "Engineering";
        String position = "Senior Developer";
        BigDecimal salary = new BigDecimal("85000");
        LocalDate hireDate = LocalDate.of(2020, 1, 15);
        int experience = 5;
        String skills = "Java, Spring Boot, SQL";
        
        // When
        Employee employee = new Employee(id, firstName, lastName, email, 
                                       department, position, salary, hireDate, 
                                       experience, skills);
        
        // Then
        assertEquals(id, employee.id());
        assertEquals(firstName, employee.firstName());
        assertEquals(lastName, employee.lastName());
        assertEquals(email, employee.email());
        assertEquals(department, employee.department());
        assertEquals(position, employee.position());
        assertEquals(salary, employee.salary());
        assertEquals(hireDate, employee.hireDate());
        assertEquals(experience, employee.yearsOfExperience());
        assertEquals(skills, employee.skills());
    }
    
    @Test
    @DisplayName("Should generate correct full name")
    void testFullName() {
        // Given
        Employee employee = createTestEmployee("John", "Doe");
        
        // When
        String fullName = employee.fullName();
        
        // Then
        assertEquals("John Doe", fullName);
    }
    
    @Test
    @DisplayName("Should identify high earners correctly")
    void testIsHighEarner() {
        // Given
        Employee highEarner = createTestEmployee(new BigDecimal("120000"));
        Employee regularEarner = createTestEmployee(new BigDecimal("75000"));
        
        // Then
        assertTrue(highEarner.isHighEarner());
        assertFalse(regularEarner.isHighEarner());
    }
    
    @Test
    @DisplayName("Should identify senior employees correctly")
    void testIsSenior() {
        // Given
        Employee senior = createTestEmployee(7);
        Employee junior = createTestEmployee(2);
        
        // Then
        assertTrue(senior.isSenior());
        assertFalse(junior.isSenior());
    }
    
    @Test
    @DisplayName("Should categorize experience levels correctly")
    void testGetExperienceLevel() {
        assertEquals("Junior", createTestEmployee(0).getExperienceLevel());
        assertEquals("Junior", createTestEmployee(1).getExperienceLevel());
        assertEquals("Mid-level", createTestEmployee(3).getExperienceLevel());
        assertEquals("Senior", createTestEmployee(6).getExperienceLevel());
        assertEquals("Expert", createTestEmployee(12).getExperienceLevel());
    }
    
    @Test
    @DisplayName("Should throw exception for null required fields")
    void testNullValidation() {
        assertThrows(NullPointerException.class, () -> 
            new Employee(null, "John", "Doe", "john@company.com", 
                        "Engineering", "Developer", new BigDecimal("75000"), 
                        LocalDate.now(), 3, "Java"));
        
        assertThrows(NullPointerException.class, () -> 
            new Employee(1L, null, "Doe", "john@company.com", 
                        "Engineering", "Developer", new BigDecimal("75000"), 
                        LocalDate.now(), 3, "Java"));
        
        assertThrows(NullPointerException.class, () -> 
            new Employee(1L, "John", "Doe", null, 
                        "Engineering", "Developer", new BigDecimal("75000"), 
                        LocalDate.now(), 3, "Java"));
    }
    
    @Test
    @DisplayName("Should throw exception for negative salary")
    void testNegativeSalaryValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            createTestEmployee(new BigDecimal("-1000")));
    }
    
    @Test
    @DisplayName("Should throw exception for negative experience")
    void testNegativeExperienceValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            createTestEmployee(-1));
    }
    
    @Test
    @DisplayName("Should handle boundary values correctly")
    void testBoundaryValues() {
        // Boundary cases that should work
        assertDoesNotThrow(() -> createTestEmployee(new BigDecimal("0")));
        assertDoesNotThrow(() -> createTestEmployee(0));
        assertDoesNotThrow(() -> createTestEmployee(new BigDecimal("100000")));
        
        Employee boundaryEmployee = createTestEmployee(new BigDecimal("100000"));
        assertFalse(boundaryEmployee.isHighEarner()); // exactly 100k is not > 100k
        
        Employee justOverBoundary = createTestEmployee(new BigDecimal("100001"));
        assertTrue(justOverBoundary.isHighEarner());
    }
    
    // Helper methods
    private Employee createTestEmployee(String firstName, String lastName) {
        return new Employee(1L, firstName, lastName, "test@company.com",
                          "Engineering", "Developer", new BigDecimal("75000"),
                          LocalDate.now(), 3, "Java");
    }
    
    private Employee createTestEmployee(BigDecimal salary) {
        return new Employee(1L, "John", "Doe", "john@company.com",
                          "Engineering", "Developer", salary,
                          LocalDate.now(), 3, "Java");
    }
    
    private Employee createTestEmployee(int experience) {
        return new Employee(1L, "John", "Doe", "john@company.com",
                          "Engineering", "Developer", new BigDecimal("75000"),
                          LocalDate.now(), experience, "Java");
    }
}