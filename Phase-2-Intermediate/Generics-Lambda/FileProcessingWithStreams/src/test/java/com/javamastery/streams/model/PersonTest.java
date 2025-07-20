package com.javamastery.streams.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Person model
 */
@DisplayName("Person Model Tests")
class PersonTest {
    
    @Test
    @DisplayName("Should create Person with valid data")
    void shouldCreatePersonWithValidData() {
        // Given
        LocalDate joinDate = LocalDate.of(2020, 1, 15);
        
        // When
        Person person = new Person(
            "P001", "John", "Doe", "john.doe@example.com",
            30, "New York", "USA", 75000.0, "Engineering", joinDate
        );
        
        // Then
        assertEquals("P001", person.id());
        assertEquals("John", person.firstName());
        assertEquals("Doe", person.lastName());
        assertEquals("john.doe@example.com", person.email());
        assertEquals(30, person.age());
        assertEquals("New York", person.city());
        assertEquals("USA", person.country());
        assertEquals(75000.0, person.salary());
        assertEquals("Engineering", person.department());
        assertEquals(joinDate, person.joinDate());
    }
    
    @Test
    @DisplayName("Should generate full name correctly")
    void shouldGenerateFullNameCorrectly() {
        // Given
        Person person = createValidPerson();
        
        // When
        String fullName = person.fullName();
        
        // Then
        assertEquals("John Doe", fullName);
    }
    
    @Test
    @DisplayName("Should identify senior person correctly")
    void shouldIdentifySeniorPersonCorrectly() {
        // Given
        Person senior = new Person(
            "P001", "Jane", "Smith", "jane@example.com",
            52, "Boston", "USA", 90000.0, "Management", LocalDate.now().minusYears(10)
        );
        Person junior = createValidPerson(); // age 30
        
        // When & Then
        assertTrue(senior.isSenior());
        assertFalse(junior.isSenior());
    }
    
    @Test
    @DisplayName("Should identify high earner correctly")
    void shouldIdentifyHighEarnerCorrectly() {
        // Given
        Person highEarner = new Person(
            "P001", "Rich", "Person", "rich@example.com",
            35, "Silicon Valley", "USA", 150000.0, "Tech", LocalDate.now().minusYears(5)
        );
        Person regularEarner = createValidPerson(); // salary 75000
        
        // When & Then
        assertTrue(highEarner.isHighEarner());
        assertFalse(regularEarner.isHighEarner());
    }
    
    @Test
    @DisplayName("Should calculate years of experience correctly")
    void shouldCalculateYearsOfExperienceCorrectly() {
        // Given
        Person person = new Person(
            "P001", "John", "Doe", "john@example.com",
            30, "New York", "USA", 75000.0, "Engineering", 
            LocalDate.now().minusYears(3).minusMonths(6) // 3.5 years ago
        );
        
        // When
        long experience = person.yearsOfExperience();
        
        // Then
        assertEquals(3, experience); // Should be 3 (truncated)
    }
    
    @Test
    @DisplayName("Should throw exception for null id")
    void shouldThrowExceptionForNullId() {
        // When & Then
        assertThrows(NullPointerException.class, () -> new Person(
            null, "John", "Doe", "john@example.com",
            30, "New York", "USA", 75000.0, "Engineering", LocalDate.now()
        ));
    }
    
    @Test
    @DisplayName("Should throw exception for negative age")
    void shouldThrowExceptionForNegativeAge() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Person(
            "P001", "John", "Doe", "john@example.com",
            -1, "New York", "USA", 75000.0, "Engineering", LocalDate.now()
        ));
    }
    
    @Test
    @DisplayName("Should throw exception for negative salary")
    void shouldThrowExceptionForNegativeSalary() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Person(
            "P001", "John", "Doe", "john@example.com",
            30, "New York", "USA", -1000.0, "Engineering", LocalDate.now()
        ));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid email")
    void shouldThrowExceptionForInvalidEmail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new Person(
            "P001", "John", "Doe", "invalid-email",
            30, "New York", "USA", 75000.0, "Engineering", LocalDate.now()
        ));
    }
    
    @Test
    @DisplayName("Should have proper equals and hashCode")
    void shouldHaveProperEqualsAndHashCode() {
        // Given
        Person person1 = createValidPerson();
        Person person2 = createValidPerson(); // Same data
        Person person3 = new Person(
            "P002", "Jane", "Smith", "jane@example.com",
            25, "Boston", "USA", 65000.0, "Marketing", LocalDate.now()
        );
        
        // When & Then
        assertEquals(person1, person2);
        assertNotEquals(person1, person3);
        assertEquals(person1.hashCode(), person2.hashCode());
    }
    
    private Person createValidPerson() {
        return new Person(
            "P001", "John", "Doe", "john.doe@example.com",
            30, "New York", "USA", 75000.0, "Engineering", LocalDate.now().minusYears(3)
        );
    }
}