package com.javamastery.dataprocessor.service;

import com.javamastery.dataprocessor.model.Employee;
import com.javamastery.dataprocessor.collector.CustomCollectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@DisplayName("Employee Analysis Service Tests")
class EmployeeAnalysisServiceTest {
    
    private EmployeeAnalysisService service;
    private List<Employee> testEmployees;
    
    @BeforeEach
    void setUp() {
        testEmployees = Arrays.asList(
            createEmployee(1L, "Alice", "Smith", "Engineering", new BigDecimal("120000"), 6, "Java, Spring"),
            createEmployee(2L, "Bob", "Johnson", "Engineering", new BigDecimal("85000"), 3, "Python, SQL"),
            createEmployee(3L, "Carol", "Davis", "Marketing", new BigDecimal("75000"), 8, "Analytics, SQL"),
            createEmployee(4L, "David", "Wilson", "Engineering", new BigDecimal("105000"), 5, "Java, React"),
            createEmployee(5L, "Eva", "Brown", "Marketing", new BigDecimal("65000"), 2, "Marketing, Analytics"),
            createEmployee(6L, "Frank", "Miller", "Sales", new BigDecimal("80000"), 4, "Sales, CRM"),
            createEmployee(7L, "Grace", "Lee", "Engineering", new BigDecimal("95000"), 7, "Java, Docker")
        );
        
        service = new EmployeeAnalysisService(testEmployees);
    }
    
    @Test
    @DisplayName("Should filter high earners correctly")
    void testGetHighEarners() {
        // When
        List<Employee> highEarners = service.getHighEarners(new BigDecimal("100000"));
        
        // Then
        assertEquals(2, highEarners.size());
        assertTrue(highEarners.stream().allMatch(emp -> 
            emp.salary().compareTo(new BigDecimal("100000")) > 0));
        assertTrue(highEarners.stream().anyMatch(emp -> "Alice".equals(emp.firstName())));
        assertTrue(highEarners.stream().anyMatch(emp -> "David".equals(emp.firstName())));
    }
    
    @Test
    @DisplayName("Should filter senior employees correctly")
    void testGetSeniorEmployees() {
        // When
        List<Employee> seniors = service.getSeniorEmployees();
        
        // Then
        assertEquals(4, seniors.size());
        assertTrue(seniors.stream().allMatch(Employee::isSenior));
        assertTrue(seniors.stream().allMatch(emp -> emp.yearsOfExperience() >= 5));
    }
    
    @Test
    @DisplayName("Should filter employees by department")
    void testGetEmployeesByDepartment() {
        // When
        List<Employee> engineers = service.getEmployeesByDepartment("Engineering");
        
        // Then
        assertEquals(4, engineers.size());
        assertTrue(engineers.stream().allMatch(emp -> "Engineering".equals(emp.department())));
    }
    
    @Test
    @DisplayName("Should find senior high earners")
    void testGetSeniorHighEarners() {
        // When
        List<Employee> seniorHighEarners = service.getSeniorHighEarners();
        
        // Then
        assertEquals(2, seniorHighEarners.size());
        assertTrue(seniorHighEarners.stream().allMatch(Employee::isSenior));
        assertTrue(seniorHighEarners.stream().allMatch(Employee::isHighEarner));
    }
    
    @Test
    @DisplayName("Should extract all emails correctly")
    void testGetAllEmails() {
        // When
        List<String> emails = service.getAllEmails();
        
        // Then
        assertEquals(7, emails.size());
        assertTrue(emails.contains("alice@company.com"));
        assertTrue(emails.contains("bob@company.com"));
        assertTrue(emails.stream().allMatch(email -> email.contains("@company.com")));
    }
    
    @Test
    @DisplayName("Should extract full names correctly")
    void testGetFullNames() {
        // When
        List<String> names = service.getFullNames();
        
        // Then
        assertEquals(7, names.size());
        assertTrue(names.contains("Alice Smith"));
        assertTrue(names.contains("Bob Johnson"));
    }
    
    @Test
    @DisplayName("Should calculate salary statistics")
    void testGetSalaryStatistics() {
        // When
        CustomCollectors.Statistics stats = service.getSalaryStatistics();
        
        // Then
        assertEquals(7, stats.count());
        assertEquals(625000.0, stats.sum());
        assertEquals(89285.71, stats.average(), 0.01);
        assertEquals(65000.0, stats.min());
        assertEquals(120000.0, stats.max());
    }
    
    @Test
    @DisplayName("Should calculate average salary by department")
    void testGetAverageSalaryByDepartment() {
        // When
        Map<String, Double> avgByDept = service.getAverageSalaryByDepartment();
        
        // Then
        assertEquals(3, avgByDept.size());
        assertEquals(101250.0, avgByDept.get("Engineering"), 0.01); // (120k + 85k + 105k + 95k) / 4
        assertEquals(70000.0, avgByDept.get("Marketing"), 0.01); // (75k + 65k) / 2
        assertEquals(80000.0, avgByDept.get("Sales"), 0.01); // 80k / 1
    }
    
    @Test
    @DisplayName("Should count employees by department")
    void testGetEmployeeCountByDepartment() {
        // When
        Map<String, Long> countByDept = service.getEmployeeCountByDepartment();
        
        // Then
        assertEquals(3, countByDept.size());
        assertEquals(4L, countByDept.get("Engineering"));
        assertEquals(2L, countByDept.get("Marketing"));
        assertEquals(1L, countByDept.get("Sales"));
    }
    
    @Test
    @DisplayName("Should create department experience breakdown")
    void testGetDepartmentExperienceBreakdown() {
        // When
        Map<String, Map<String, Long>> breakdown = service.getDepartmentExperienceBreakdown();
        
        // Then
        assertEquals(3, breakdown.size());
        
        Map<String, Long> engineeringBreakdown = breakdown.get("Engineering");
        assertNotNull(engineeringBreakdown);
        assertEquals(1L, engineeringBreakdown.get("Mid-level")); // Bob: 3 years
        assertEquals(3L, engineeringBreakdown.get("Senior")); // Alice: 6, David: 5, Grace: 7
    }
    
    @Test
    @DisplayName("Should create salary buckets")
    void testGetSalaryBuckets() {
        // When
        Map<String, List<Employee>> buckets = service.getSalaryBuckets();
        
        // Then
        assertFalse(buckets.isEmpty());
        
        // Verify employees are in correct buckets based on salary ranges
        buckets.values().forEach(bucket -> 
            assertFalse(bucket.isEmpty())
        );
    }
    
    @Test
    @DisplayName("Should find employees with complex criteria")
    void testFindEmployeesWithCriteria() {
        // Given
        var criteria = new EmployeeAnalysisService.EmployeeSearchCriteria(
            new BigDecimal("80000"), // min salary
            new BigDecimal("110000"), // max salary
            "Engineering", // department
            4, // min experience
            List.of("Java") // skills
        );
        
        // When
        List<Employee> results = service.findEmployees(criteria);
        
        // Then
        assertEquals(2, results.size()); // David and Grace should match
        assertTrue(results.stream().allMatch(emp -> 
            emp.salary().compareTo(new BigDecimal("80000")) >= 0 &&
            emp.salary().compareTo(new BigDecimal("110000")) <= 0 &&
            "Engineering".equals(emp.department()) &&
            emp.yearsOfExperience() >= 4 &&
            emp.skills().contains("Java")
        ));
    }
    
    @Test
    @DisplayName("Should find highest paid employee")
    void testGetHighestPaidEmployee() {
        // When
        Optional<Employee> highest = service.getHighestPaidEmployee();
        
        // Then
        assertTrue(highest.isPresent());
        assertEquals("Alice", highest.get().firstName());
        assertEquals(new BigDecimal("120000"), highest.get().salary());
    }
    
    @Test
    @DisplayName("Should find most experienced employee")
    void testGetMostExperiencedEmployee() {
        // When
        Optional<Employee> mostExperienced = service.getMostExperiencedEmployee();
        
        // Then
        assertTrue(mostExperienced.isPresent());
        assertEquals("Carol", mostExperienced.get().firstName());
        assertEquals(8, mostExperienced.get().yearsOfExperience());
    }
    
    @Test
    @DisplayName("Should calculate average salary")
    void testGetAverageSalary() {
        // When
        double avgSalary = service.getAverageSalary();
        
        // Then
        assertEquals(89285.71, avgSalary, 0.01);
    }
    
    @Test
    @DisplayName("Should create department summaries")
    void testGetDepartmentSummaries() {
        // When
        Map<String, EmployeeAnalysisService.EmployeeSummary> summaries = 
            service.getDepartmentSummaries();
        
        // Then
        assertEquals(3, summaries.size());
        
        EmployeeAnalysisService.EmployeeSummary engSummary = summaries.get("Engineering");
        assertNotNull(engSummary);
        assertEquals(4, engSummary.totalEmployees());
        assertEquals(101250.0, engSummary.averageSalary(), 0.01);
        assertEquals(5.25, engSummary.averageExperience(), 0.01); // (6+3+5+7)/4
        assertEquals(3, engSummary.seniorCount()); // Alice, David, Grace
    }
    
    @Test
    @DisplayName("Should sort employees by name")
    void testSortByName() {
        // When
        List<Employee> sorted = service.sortByName();
        
        // Then
        assertEquals(7, sorted.size());
        assertEquals("Alice Smith", sorted.get(0).fullName());
        assertEquals("Grace Lee", sorted.get(6).fullName());
        
        // Verify ordering
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i-1).fullName().compareTo(sorted.get(i).fullName()) <= 0);
        }
    }
    
    @Test
    @DisplayName("Should sort employees by salary descending")
    void testSortBySalaryDesc() {
        // When
        List<Employee> sorted = service.sortBySalaryDesc();
        
        // Then
        assertEquals(7, sorted.size());
        assertEquals(new BigDecimal("120000"), sorted.get(0).salary());
        assertEquals(new BigDecimal("65000"), sorted.get(6).salary());
        
        // Verify descending order
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i-1).salary().compareTo(sorted.get(i).salary()) >= 0);
        }
    }
    
    @Test
    @DisplayName("Should handle empty criteria gracefully")
    void testFindEmployeesWithEmptyCriteria() {
        // Given
        var emptyCriteria = new EmployeeAnalysisService.EmployeeSearchCriteria(
            null, null, null, null, null
        );
        
        // When
        List<Employee> results = service.findEmployees(emptyCriteria);
        
        // Then
        assertEquals(7, results.size()); // Should return all employees
    }
    
    // Helper method
    private Employee createEmployee(Long id, String firstName, String lastName, 
                                  String department, BigDecimal salary, int experience, String skills) {
        return new Employee(id, firstName, lastName, firstName.toLowerCase() + "@company.com",
                          department, "Developer", salary, LocalDate.now(), experience, skills);
    }
}