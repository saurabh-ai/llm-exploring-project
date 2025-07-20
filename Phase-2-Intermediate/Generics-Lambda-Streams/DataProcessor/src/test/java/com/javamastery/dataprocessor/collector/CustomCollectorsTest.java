package com.javamastery.dataprocessor.collector;

import com.javamastery.dataprocessor.model.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@DisplayName("Custom Collectors Tests")
class CustomCollectorsTest {
    
    private List<Employee> testEmployees;
    private List<Double> testNumbers;
    
    @BeforeEach
    void setUp() {
        testEmployees = Arrays.asList(
            createEmployee(1L, "Alice", "Smith", "Engineering", new BigDecimal("90000"), 5),
            createEmployee(2L, "Bob", "Johnson", "Engineering", new BigDecimal("85000"), 3),
            createEmployee(3L, "Carol", "Davis", "Marketing", new BigDecimal("70000"), 7),
            createEmployee(4L, "David", "Wilson", "Engineering", new BigDecimal("95000"), 8),
            createEmployee(5L, "Eva", "Brown", "Marketing", new BigDecimal("65000"), 2),
            createEmployee(6L, "Frank", "Miller", "Sales", new BigDecimal("80000"), 4)
        );
        
        testNumbers = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0);
    }
    
    @Test
    @DisplayName("Should collect statistics correctly")
    void testToStatistics() {
        // When
        CustomCollectors.Statistics stats = testNumbers.stream()
                .collect(CustomCollectors.toStatistics(x -> x));
        
        // Then
        assertEquals(5, stats.count());
        assertEquals(150.0, stats.sum());
        assertEquals(30.0, stats.average());
        assertEquals(10.0, stats.min());
        assertEquals(50.0, stats.max());
    }
    
    @Test
    @DisplayName("Should collect employee salary statistics correctly")
    void testEmployeeSalaryStatistics() {
        // When
        CustomCollectors.Statistics salaryStats = testEmployees.stream()
                .collect(CustomCollectors.toStatistics(emp -> emp.salary().doubleValue()));
        
        // Then
        assertEquals(6, salaryStats.count());
        assertEquals(485000.0, salaryStats.sum());
        assertEquals(80833.33, salaryStats.average(), 0.01);
        assertEquals(65000.0, salaryStats.min());
        assertEquals(95000.0, salaryStats.max());
    }
    
    @Test
    @DisplayName("Should group by department with minimum count filter")
    void testGroupingByWithMinCount() {
        // When - Only include departments with at least 2 employees
        Map<String, List<Employee>> result = testEmployees.stream()
                .collect(CustomCollectors.groupingByWithMinCount(Employee::department, 2));
        
        // Then
        assertEquals(2, result.size()); // Engineering and Marketing have >= 2
        assertTrue(result.containsKey("Engineering"));
        assertTrue(result.containsKey("Marketing"));
        assertFalse(result.containsKey("Sales")); // Only 1 employee
        
        assertEquals(3, result.get("Engineering").size());
        assertEquals(2, result.get("Marketing").size());
    }
    
    @Test
    @DisplayName("Should create salary buckets correctly")
    void testToBuckets() {
        // When
        Map<String, List<Employee>> buckets = testEmployees.stream()
                .collect(CustomCollectors.toBuckets(
                    emp -> emp.salary().doubleValue(),
                    70000.0, 85000.0
                ));
        
        // Then
        assertEquals(3, buckets.size());
        assertTrue(buckets.containsKey("0-70000.0"));
        assertTrue(buckets.containsKey("70000.0-85000.0"));
        assertTrue(buckets.containsKey("85000.0+"));
        
        assertEquals(2, buckets.get("0-70000.0").size()); // Eva: 65000, Carol: 70000
        assertEquals(2, buckets.get("70000.0-85000.0").size()); // Bob: 85000, Frank: 80000  
        assertEquals(2, buckets.get("85000.0+").size()); // Alice: 90000, David: 95000
    }
    
    @Test
    @DisplayName("Should find top frequent elements")
    void testTopFrequent() {
        // Given
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", 
                                         "banana", "apple", "date");
        
        // When
        List<Map.Entry<String, Long>> topFrequent = words.stream()
                .collect(CustomCollectors.toTopFrequent(3));
        
        // Then
        assertEquals(3, topFrequent.size());
        assertEquals("apple", topFrequent.get(0).getKey());
        assertEquals(3L, topFrequent.get(0).getValue());
        assertEquals("banana", topFrequent.get(1).getKey());
        assertEquals(2L, topFrequent.get(1).getValue());
    }
    
    @Test
    @DisplayName("Should create concurrent histogram")
    void testToConcurrentHistogram() {
        // When
        var histogram = testEmployees.parallelStream()
                .collect(CustomCollectors.toConcurrentHistogram(Employee::department));
        
        // Then
        assertEquals(3, histogram.size());
        assertEquals(3L, histogram.get("Engineering"));
        assertEquals(2L, histogram.get("Marketing"));
        assertEquals(1L, histogram.get("Sales"));
    }
    
    @Test
    @DisplayName("Should partition by multiple predicates")
    void testPartitioningByMultiple() {
        // Given
        Map<String, Predicate<Employee>> predicates = Map.of(
            "High Earners", emp -> emp.salary().compareTo(new BigDecimal("80000")) > 0,
            "Senior", emp -> emp.yearsOfExperience() >= 5,
            "Engineering", emp -> "Engineering".equals(emp.department())
        );
        
        // When
        Map<String, List<Employee>> partitions = testEmployees.stream()
                .collect(CustomCollectors.partitioningByMultiple(predicates));
        
        // Then
        assertEquals(3, partitions.size());
        assertEquals(3, partitions.get("High Earners").size()); // Alice, David, Bob (85k counts as not > 80k? Let me check...)
        assertEquals(3, partitions.get("Senior").size()); // Alice: 5, Carol: 7, David: 8
        assertEquals(3, partitions.get("Engineering").size()); // Alice, Bob, David
    }
    
    @Test
    @DisplayName("Should handle empty collections")
    void testEmptyCollections() {
        List<Employee> empty = Collections.emptyList();
        
        // Statistics should handle empty
        CustomCollectors.Statistics stats = empty.stream()
                .collect(CustomCollectors.toStatistics(emp -> emp.salary().doubleValue()));
        
        assertEquals(0, stats.count());
        assertEquals(0.0, stats.sum());
        assertEquals(0.0, stats.average());
        assertEquals(0.0, stats.min());
        assertEquals(0.0, stats.max());
        
        // Grouping should return empty map
        Map<String, List<Employee>> groups = empty.stream()
                .collect(CustomCollectors.groupingByWithMinCount(Employee::department, 1));
        assertTrue(groups.isEmpty());
    }
    
    @Test
    @DisplayName("Should handle single element collections")
    void testSingleElement() {
        List<Employee> single = List.of(testEmployees.get(0));
        
        CustomCollectors.Statistics stats = single.stream()
                .collect(CustomCollectors.toStatistics(emp -> emp.salary().doubleValue()));
        
        assertEquals(1, stats.count());
        assertEquals(90000.0, stats.sum());
        assertEquals(90000.0, stats.average());
        assertEquals(90000.0, stats.min());
        assertEquals(90000.0, stats.max());
    }
    
    @Test
    @DisplayName("Should create buckets with various ranges")
    void testBucketsWithDifferentRanges() {
        List<Integer> numbers = IntStream.rangeClosed(1, 100).boxed().toList();
        
        Map<String, List<Integer>> buckets = numbers.stream()
                .collect(CustomCollectors.toBuckets(
                    Integer::doubleValue,
                    25.0, 50.0, 75.0
                ));
        
        assertEquals(4, buckets.size());
        assertEquals(25, buckets.get("0-25.0").size());
        assertEquals(25, buckets.get("25.0-50.0").size());
        assertEquals(25, buckets.get("50.0-75.0").size());
        assertEquals(25, buckets.get("75.0+").size());
    }
    
    // Helper method
    private Employee createEmployee(Long id, String firstName, String lastName, 
                                  String department, BigDecimal salary, int experience) {
        return new Employee(id, firstName, lastName, firstName.toLowerCase() + "@company.com",
                          department, "Developer", salary, LocalDate.now(), experience, "Java");
    }
}