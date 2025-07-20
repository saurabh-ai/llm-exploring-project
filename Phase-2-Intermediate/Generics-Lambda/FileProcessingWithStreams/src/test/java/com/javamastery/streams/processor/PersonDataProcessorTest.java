package com.javamastery.streams.processor;

import com.javamastery.streams.model.Person;
import com.javamastery.streams.collector.CustomCollectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PersonDataProcessor
 */
@DisplayName("Person Data Processor Tests")
class PersonDataProcessorTest {
    
    private PersonDataProcessor processor;
    private List<Person> testPeople;
    
    @BeforeEach
    void setUp() {
        testPeople = Arrays.asList(
            new Person("P001", "John", "Doe", "john@example.com", 30, "New York", "USA", 
                      75000.0, "Engineering", LocalDate.of(2020, 1, 15)),
            new Person("P002", "Jane", "Smith", "jane@example.com", 55, "Boston", "USA", 
                      120000.0, "Management", LocalDate.of(2015, 3, 10)),
            new Person("P003", "Bob", "Johnson", "bob@example.com", 28, "Seattle", "USA", 
                      65000.0, "Engineering", LocalDate.of(2021, 6, 1)),
            new Person("P004", "Alice", "Brown", "alice@example.com", 45, "San Francisco", "USA", 
                      95000.0, "Marketing", LocalDate.of(2018, 9, 20)),
            new Person("P005", "Charlie", "Wilson", "charlie@example.com", 32, "Chicago", "USA", 
                      85000.0, "Sales", LocalDate.of(2019, 11, 5))
        );
        processor = new PersonDataProcessor(testPeople);
    }
    
    @Test
    @DisplayName("Should filter people by age range")
    void shouldFilterPeopleByAgeRange() {
        // When
        List<Person> result = processor.filterByAgeRange(30, 45);
        
        // Then
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(p -> p.age() >= 30 && p.age() <= 45));
        // Should be sorted by age
        assertEquals("John", result.get(0).firstName()); // age 30
        assertEquals("Charlie", result.get(1).firstName()); // age 32
        assertEquals("Alice", result.get(2).firstName()); // age 45
    }
    
    @Test
    @DisplayName("Should calculate average salary by department")
    void shouldCalculateAverageSalaryByDepartment() {
        // When
        Map<String, Double> averages = processor.getAverageSalaryByDepartment();
        
        // Then
        assertEquals(70000.0, averages.get("Engineering")); // (75000 + 65000) / 2
        assertEquals(120000.0, averages.get("Management"));
        assertEquals(95000.0, averages.get("Marketing"));
        assertEquals(85000.0, averages.get("Sales"));
    }
    
    @Test
    @DisplayName("Should find top earners")
    void shouldFindTopEarners() {
        // When
        List<Person> topEarners = processor.getTopEarners(3);
        
        // Then
        assertEquals(3, topEarners.size());
        assertEquals("Jane", topEarners.get(0).firstName()); // 120000
        assertEquals("Alice", topEarners.get(1).firstName()); // 95000
        assertEquals("Charlie", topEarners.get(2).firstName()); // 85000
    }
    
    @Test
    @DisplayName("Should calculate salary statistics")
    void shouldCalculateSalaryStatistics() {
        // When
        CustomCollectors.Statistics stats = processor.getSalaryStatistics();
        
        // Then
        assertEquals(5, stats.getCount());
        assertEquals(440000.0, stats.getSum());
        assertEquals(88000.0, stats.getAverage());
        assertEquals(65000.0, stats.getMin());
        assertEquals(120000.0, stats.getMax());
    }
    
    @Test
    @DisplayName("Should group by country and city")
    void shouldGroupByCountryAndCity() {
        // When
        Map<String, Map<String, List<Person>>> grouped = processor.groupByCountryAndCity();
        
        // Then
        assertEquals(1, grouped.size()); // Only USA
        Map<String, List<Person>> usaCities = grouped.get("USA");
        assertEquals(5, usaCities.size()); // 5 different cities
        assertEquals(1, usaCities.get("New York").size());
        assertEquals(1, usaCities.get("Boston").size());
        assertEquals(1, usaCities.get("Seattle").size());
        assertEquals(1, usaCities.get("San Francisco").size());
        assertEquals(1, usaCities.get("Chicago").size());
    }
    
    @Test
    @DisplayName("Should find people joined in specific year")
    void shouldFindPeopleJoinedInSpecificYear() {
        // When
        List<Person> joined2020 = processor.getPeopleJoinedInYear(2020);
        
        // Then
        assertEquals(1, joined2020.size());
        assertEquals("John", joined2020.get(0).firstName());
    }
    
    @Test
    @DisplayName("Should get emails for high earning experienced people")
    void shouldGetEmailsForHighEarningExperiencedPeople() {
        // When
        List<String> emails = processor.getEmailsForHighEarningRockets(80000.0);
        
        // Then
        // Should include people with salary >= 80000 and experience >= 3 years
        assertTrue(emails.contains("jane@example.com")); // High salary, long experience
        assertTrue(emails.contains("alice@example.com")); // High salary, long experience
        assertTrue(emails.contains("charlie@example.com")); // High salary, experience >= 3
        assertEquals(3, emails.size());
        // Should be sorted
        assertEquals("alice@example.com", emails.get(0));
        assertEquals("charlie@example.com", emails.get(1));
        assertEquals("jane@example.com", emails.get(2));
    }
    
    @Test
    @DisplayName("Should partition by senior status")
    void shouldPartitionBySeniorStatus() {
        // When
        Map<Boolean, List<Person>> partitioned = processor.partitionBySeniorStatus();
        
        // Then
        assertEquals(1, partitioned.get(true).size()); // Only Jane is senior (55)
        assertEquals(4, partitioned.get(false).size()); // Others are not senior
        assertEquals("Jane", partitioned.get(true).get(0).firstName());
    }
    
    @Test
    @DisplayName("Should calculate department demographics")
    void shouldCalculateDepartmentDemographics() {
        // When
        Map<String, Map<String, Long>> demographics = processor.getDepartmentDemographics();
        
        // Then
        assertTrue(demographics.containsKey("Engineering"));
        assertTrue(demographics.containsKey("Management"));
        assertTrue(demographics.containsKey("Marketing"));
        assertTrue(demographics.containsKey("Sales"));
        
        // Check age group classifications
        Map<String, Long> engineeringDemo = demographics.get("Engineering");
        assertEquals(1L, engineeringDemo.get("Young (< 30)")); // Just Bob (28)
        assertEquals(1L, engineeringDemo.get("Middle (30-49)")); // John (30)
    }
    
    @Test
    @DisplayName("Should generate summary report")
    void shouldGenerateSummaryReport() {
        // When
        String report = processor.generateSummaryReport();
        
        // Then
        assertTrue(report.contains("Total People: 5"));
        assertTrue(report.contains("Average Salary: $88000.00"));
        assertTrue(report.contains("High Earners")); // Should mention high earners count
        assertTrue(report.contains("Top Departments"));
    }
    
    @Test
    @DisplayName("Should throw exception for null people list")
    void shouldThrowExceptionForNullPeopleList() {
        // When & Then
        assertThrows(NullPointerException.class, () -> new PersonDataProcessor(null));
    }
    
    @Test
    @DisplayName("Should handle empty people list")
    void shouldHandleEmptyPeopleList() {
        // Given
        PersonDataProcessor emptyProcessor = new PersonDataProcessor(List.of());
        
        // When & Then
        assertTrue(emptyProcessor.filterByAgeRange(20, 60).isEmpty());
        assertTrue(emptyProcessor.getAverageSalaryByDepartment().isEmpty());
        assertTrue(emptyProcessor.getTopEarners(5).isEmpty());
    }
}