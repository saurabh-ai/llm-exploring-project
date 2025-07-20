package com.javamastery.dataprocessor.service;

import com.javamastery.dataprocessor.model.Employee;
import com.javamastery.dataprocessor.model.DataContainer;
import com.javamastery.dataprocessor.collector.CustomCollectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class demonstrating comprehensive stream operations on employee data
 * Shows functional programming patterns, lambda expressions, and method references
 */
public class EmployeeAnalysisService {
    
    private final DataContainer<Employee> employees;
    
    public EmployeeAnalysisService(List<Employee> employees) {
        this.employees = new DataContainer<>(employees);
    }
    
    // Basic filtering operations
    public List<Employee> getHighEarners(BigDecimal threshold) {
        return employees.filter(emp -> emp.salary().compareTo(threshold) > 0);
    }
    
    public List<Employee> getSeniorEmployees() {
        return employees.filter(Employee::isSenior);
    }
    
    public List<Employee> getEmployeesByDepartment(String department) {
        return employees.filter(emp -> department.equals(emp.department()));
    }
    
    // Advanced filtering with compound conditions
    public List<Employee> getSeniorHighEarners() {
        return employees.getItems().stream()
                .filter(Employee::isSenior)
                .filter(Employee::isHighEarner)
                .collect(Collectors.toList());
    }
    
    // Transformation operations using map
    public List<String> getAllEmails() {
        return employees.map(Employee::email);
    }
    
    public List<String> getFullNames() {
        return employees.map(Employee::fullName);
    }
    
    public Map<String, String> getEmailToNameMapping() {
        return employees.getItems().stream()
                .collect(Collectors.toMap(
                    Employee::email,
                    Employee::fullName,
                    (existing, replacement) -> existing // Handle duplicates
                ));
    }
    
    // Aggregation operations
    public CustomCollectors.Statistics getSalaryStatistics() {
        return employees.getItems().stream()
                .collect(CustomCollectors.toStatistics(
                    emp -> emp.salary().doubleValue()));
    }
    
    public Map<String, Double> getAverageSalaryByDepartment() {
        return employees.getItems().stream()
                .collect(Collectors.groupingBy(
                    Employee::department,
                    Collectors.averagingDouble(emp -> emp.salary().doubleValue())
                ));
    }
    
    public Map<String, Long> getEmployeeCountByDepartment() {
        return employees.getItems().stream()
                .collect(Collectors.groupingBy(
                    Employee::department,
                    Collectors.counting()
                ));
    }
    
    // Complex grouping operations
    public Map<String, Map<String, Long>> getDepartmentExperienceBreakdown() {
        return employees.getItems().stream()
                .collect(Collectors.groupingBy(
                    Employee::department,
                    Collectors.groupingBy(
                        Employee::getExperienceLevel,
                        Collectors.counting()
                    )
                ));
    }
    
    // Custom collector usage
    public Map<String, List<Employee>> getSalaryBuckets() {
        return employees.getItems().stream()
                .collect(CustomCollectors.toBuckets(
                    emp -> emp.salary().doubleValue(),
                    50000, 75000, 100000, 150000
                ));
    }
    
    // Advanced stream operations
    public List<Employee> getTopEarnersByDepartment(int limit) {
        return employees.getItems().stream()
                .collect(Collectors.groupingBy(Employee::department))
                .values()
                .stream()
                .flatMap(deptEmployees -> deptEmployees.stream()
                    .sorted(Comparator.comparing(Employee::salary).reversed())
                    .limit(limit))
                .collect(Collectors.toList());
    }
    
    // Parallel processing example
    public Map<String, BigDecimal> getTotalSalaryByDepartmentParallel() {
        return employees.getItems().parallelStream()
                .collect(Collectors.groupingBy(
                    Employee::department,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Employee::salary,
                        BigDecimal::add
                    )
                ));
    }
    
    // Complex filtering with multiple conditions
    public List<Employee> findEmployees(EmployeeSearchCriteria criteria) {
        Predicate<Employee> predicate = emp -> true; // Start with always true
        
        if (criteria.minSalary() != null) {
            predicate = predicate.and(emp -> emp.salary().compareTo(criteria.minSalary()) >= 0);
        }
        
        if (criteria.maxSalary() != null) {
            predicate = predicate.and(emp -> emp.salary().compareTo(criteria.maxSalary()) <= 0);
        }
        
        if (criteria.department() != null) {
            predicate = predicate.and(emp -> criteria.department().equals(emp.department()));
        }
        
        if (criteria.minExperience() != null) {
            predicate = predicate.and(emp -> emp.yearsOfExperience() >= criteria.minExperience());
        }
        
        if (criteria.skills() != null && !criteria.skills().isEmpty()) {
            predicate = predicate.and(emp -> 
                criteria.skills().stream().anyMatch(skill -> 
                    emp.skills() != null && emp.skills().toLowerCase().contains(skill.toLowerCase())
                )
            );
        }
        
        return employees.filter(predicate);
    }
    
    // Statistical analysis methods
    public Optional<Employee> getHighestPaidEmployee() {
        return employees.getItems().stream()
                .max(Comparator.comparing(Employee::salary));
    }
    
    public Optional<Employee> getMostExperiencedEmployee() {
        return employees.getItems().stream()
                .max(Comparator.comparing(Employee::yearsOfExperience));
    }
    
    public double getAverageSalary() {
        return employees.getAverageDouble(emp -> emp.salary().doubleValue())
                .orElse(0.0);
    }
    
    // Complex data transformation
    public Map<String, EmployeeSummary> getDepartmentSummaries() {
        return employees.getItems().stream()
                .collect(Collectors.groupingBy(
                    Employee::department,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        this::createEmployeeSummary
                    )
                ));
    }
    
    private EmployeeSummary createEmployeeSummary(List<Employee> deptEmployees) {
        long count = deptEmployees.size();
        double avgSalary = deptEmployees.stream()
                .mapToDouble(emp -> emp.salary().doubleValue())
                .average()
                .orElse(0.0);
        double avgExperience = deptEmployees.stream()
                .mapToInt(Employee::yearsOfExperience)
                .average()
                .orElse(0.0);
        long seniorCount = deptEmployees.stream()
                .mapToLong(emp -> emp.isSenior() ? 1 : 0)
                .sum();
        
        return new EmployeeSummary(count, avgSalary, avgExperience, seniorCount);
    }
    
    // Method reference examples
    public List<Employee> sortByName() {
        return employees.getSorted(Comparator.comparing(Employee::fullName));
    }
    
    public List<Employee> sortBySalaryDesc() {
        return employees.getSorted(Comparator.comparing(Employee::salary).reversed());
    }
    
    // Data classes for complex operations
    public record EmployeeSearchCriteria(
        BigDecimal minSalary,
        BigDecimal maxSalary,
        String department,
        Integer minExperience,
        List<String> skills
    ) {}
    
    public record EmployeeSummary(
        long totalEmployees,
        double averageSalary,
        double averageExperience,
        long seniorCount
    ) {}
}