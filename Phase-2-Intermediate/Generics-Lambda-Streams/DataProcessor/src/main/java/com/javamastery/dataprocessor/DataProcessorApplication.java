package com.javamastery.dataprocessor;

import com.javamastery.dataprocessor.model.*;
import com.javamastery.dataprocessor.service.*;
import com.javamastery.dataprocessor.util.DataGenerator;
import com.javamastery.dataprocessor.collector.CustomCollectors;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main application demonstrating comprehensive data processing with Java Streams API
 * Interactive console showcasing lambda expressions, functional programming, and advanced stream operations
 */
public class DataProcessorApplication {
    
    private static final Scanner scanner = new Scanner(System.in);
    private EmployeeAnalysisService employeeService;
    private List<Transaction> transactions;
    private List<LogEntry> logEntries;
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Data Processing with Streams Application...");
        new DataProcessorApplication().run();
    }
    
    public void run() {
        displayWelcomeMessage();
        initializeData();
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice (0-7): ");
            
            switch (choice) {
                case 1 -> demonstrateBasicStreamOperations();
                case 2 -> demonstrateAdvancedFiltering();
                case 3 -> demonstrateGroupingAndAggregation();
                case 4 -> demonstrateCustomCollectors();
                case 5 -> demonstrateParallelProcessing();
                case 6 -> demonstrateFunctionalProgramming();
                case 7 -> demonstrateComplexDataProcessing();
                case 0 -> {
                    System.out.println("\n🎉 Thank you for exploring Data Processing with Streams!");
                    System.out.println("Remember: Functional programming makes code more elegant and maintainable! 🌟");
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice! Please select 0-7.");
            }
        }
    }
    
    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🌊 DATA PROCESSING WITH STREAMS - COMPREHENSIVE DEMO 🌊");
        System.out.println("=".repeat(80));
        System.out.println("│ Demonstrating: Lambda Expressions, Streams API, Functional Programming    │");
        System.out.println("│ Features: Filtering, Mapping, Reducing, Collecting, Parallel Processing  │");
        System.out.println("│ Advanced: Custom Collectors, Generic Programming, Method References      │");
        System.out.println("=".repeat(80));
    }
    
    private void initializeData() {
        System.out.println("\n🔄 Generating comprehensive test data...");
        
        DataGenerator generator = new DataGenerator();
        
        // Generate data using streams
        List<Employee> employees = generator.generateEmployees(1500);
        transactions = generator.generateTransactions(3000);
        logEntries = generator.generateLogEntries(2000);
        
        employeeService = new EmployeeAnalysisService(employees);
        
        System.out.println("✅ Data generation complete!");
        System.out.println("   📊 Employees: " + employees.size());
        System.out.println("   💳 Transactions: " + transactions.size());
        System.out.println("   📝 Log Entries: " + logEntries.size());
        System.out.println("   🎯 Ready for advanced stream processing demonstrations!");
    }
    
    private void displayMainMenu() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("📋 STREAM PROCESSING DEMONSTRATIONS");
        System.out.println("─".repeat(60));
        System.out.println("1. 🔍 Basic Stream Operations (filter, map, collect)");
        System.out.println("2. 🎯 Advanced Filtering & Complex Predicates");
        System.out.println("3. 📊 Grouping & Aggregation Operations");
        System.out.println("4. 🔧 Custom Collectors & Advanced Aggregation");
        System.out.println("5. ⚡ Parallel Processing & Performance");
        System.out.println("6. 🧩 Functional Programming Patterns");
        System.out.println("7. 🚀 Complex Data Processing Scenarios");
        System.out.println("0. 🚪 Exit Application");
        System.out.println("─".repeat(60));
    }
    
    private void demonstrateBasicStreamOperations() {
        System.out.println("\n🔍 BASIC STREAM OPERATIONS");
        System.out.println("=".repeat(50));
        
        // Filter operations
        System.out.println("📈 High Earners (Salary > $100,000):");
        List<Employee> highEarners = employeeService.getHighEarners(new BigDecimal("100000"));
        highEarners.stream()
                .limit(5)
                .forEach(emp -> System.out.printf("   • %s - $%.2f%n", 
                    emp.fullName(), emp.salary()));
        System.out.println("   Total: " + highEarners.size() + " employees");
        
        // Map operations
        System.out.println("\n📧 Email Addresses (first 8):");
        employeeService.getAllEmails().stream()
                .limit(8)
                .forEach(email -> System.out.println("   • " + email));
        
        // Collect operations
        System.out.println("\n🏢 Departments:");
        Set<String> departments = employeeService.getEmployeeCountByDepartment().keySet();
        departments.forEach(dept -> System.out.println("   • " + dept));
        
        pauseForUser();
    }
    
    private void demonstrateAdvancedFiltering() {
        System.out.println("\n🎯 ADVANCED FILTERING & COMPLEX PREDICATES");
        System.out.println("=".repeat(50));
        
        // Complex filtering
        System.out.println("🎖️ Senior High Earners:");
        List<Employee> seniorHighEarners = employeeService.getSeniorHighEarners();
        seniorHighEarners.stream()
                .limit(5)
                .forEach(emp -> System.out.printf("   • %s (%d years exp) - $%.2f%n",
                    emp.fullName(), emp.yearsOfExperience(), emp.salary()));
        
        // Dynamic filtering with criteria
        System.out.println("\n🔎 Search Results (Engineering, Min Salary $80k, Java Skills):");
        var criteria = new EmployeeAnalysisService.EmployeeSearchCriteria(
                new BigDecimal("80000"), null, "Engineering", null, List.of("Java"));
        
        List<Employee> searchResults = employeeService.findEmployees(criteria);
        searchResults.stream()
                .limit(3)
                .forEach(emp -> System.out.printf("   • %s - %s - $%.2f%n",
                    emp.fullName(), emp.position(), emp.salary()));
        System.out.println("   Found: " + searchResults.size() + " matching employees");
        
        pauseForUser();
    }
    
    private void demonstrateGroupingAndAggregation() {
        System.out.println("\n📊 GROUPING & AGGREGATION OPERATIONS");
        System.out.println("=".repeat(50));
        
        // Basic grouping
        System.out.println("👥 Employee Count by Department:");
        employeeService.getEmployeeCountByDepartment().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("   • %-20s: %3d employees%n", 
                    entry.getKey(), entry.getValue()));
        
        // Average calculations
        System.out.println("\n💰 Average Salary by Department:");
        employeeService.getAverageSalaryByDepartment().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("   • %-20s: $%8.2f%n", 
                    entry.getKey(), entry.getValue()));
        
        // Complex nested grouping
        System.out.println("\n📈 Department Experience Breakdown (top 3 departments):");
        employeeService.getDepartmentExperienceBreakdown().entrySet().stream()
                .limit(3)
                .forEach(dept -> {
                    System.out.println("   🏢 " + dept.getKey() + ":");
                    dept.getValue().forEach((level, count) ->
                        System.out.printf("      └─ %-10s: %d employees%n", level, count));
                });
        
        pauseForUser();
    }
    
    private void demonstrateCustomCollectors() {
        System.out.println("\n🔧 CUSTOM COLLECTORS & ADVANCED AGGREGATION");
        System.out.println("=".repeat(50));
        
        // Statistics collector
        System.out.println("📊 Salary Statistics:");
        CustomCollectors.Statistics salaryStats = employeeService.getSalaryStatistics();
        System.out.println("   " + salaryStats);
        
        // Salary buckets using custom collector
        System.out.println("\n💼 Salary Distribution Buckets:");
        Map<String, List<Employee>> salaryBuckets = employeeService.getSalaryBuckets();
        salaryBuckets.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("   • $%-15s: %3d employees%n",
                    entry.getKey(), entry.getValue().size()));
        
        // Transaction analysis using custom collectors
        System.out.println("\n💳 Transaction Amount Buckets:");
        Map<String, List<Transaction>> txnBuckets = transactions.stream()
                .collect(CustomCollectors.toBuckets(
                    txn -> txn.amount().doubleValue(),
                    100.0, 500.0, 1000.0, 5000.0
                ));
        
        txnBuckets.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("   • $%-15s: %4d transactions%n",
                    entry.getKey(), entry.getValue().size()));
        
        pauseForUser();
    }
    
    private void demonstrateParallelProcessing() {
        System.out.println("\n⚡ PARALLEL PROCESSING & PERFORMANCE");
        System.out.println("=".repeat(50));
        
        System.out.println("🔄 Comparing Sequential vs Parallel Processing...");
        
        // Sequential processing
        long startTime = System.currentTimeMillis();
        Map<String, BigDecimal> sequentialResults = employeeService.getEmployeeCountByDepartment()
                .entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new BigDecimal(entry.getValue() * 1000) // Simulate expensive operation
                ));
        long sequentialTime = System.currentTimeMillis() - startTime;
        
        // Parallel processing
        startTime = System.currentTimeMillis();
        Map<String, BigDecimal> parallelResults = employeeService.getTotalSalaryByDepartmentParallel();
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("⏱️  Performance Results:");
        System.out.println("   • Sequential processing: " + sequentialTime + "ms");
        System.out.println("   • Parallel processing:   " + parallelTime + "ms");
        System.out.println("   • Departments processed: " + parallelResults.size());
        
        System.out.println("\n💼 Total Salary by Department (Parallel):");
        parallelResults.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.printf("   • %-20s: $%,12.2f%n",
                    entry.getKey(), entry.getValue()));
        
        pauseForUser();
    }
    
    private void demonstrateFunctionalProgramming() {
        System.out.println("\n🧩 FUNCTIONAL PROGRAMMING PATTERNS");
        System.out.println("=".repeat(50));
        
        // Method references
        System.out.println("📝 Method References & Function Composition:");
        System.out.println("   • Employees sorted by name (first 5):");
        employeeService.sortByName().stream()
                .limit(5)
                .map(Employee::fullName)
                .forEach(name -> System.out.println("      └─ " + name));
        
        // Lambda expressions with complex logic
        System.out.println("\n🎯 Lambda Expressions - Complex Filtering:");
        long engineeringHighEarners = employeeService.getEmployeesByDepartment("Engineering")
                .stream()
                .filter(emp -> emp.salary().compareTo(new BigDecimal("90000")) > 0)
                .filter(emp -> emp.yearsOfExperience() >= 3)
                .count();
        System.out.println("   • Engineering high earners (>$90k, 3+ years): " + engineeringHighEarners);
        
        // Function composition example
        System.out.println("\n🔗 Function Composition - Data Transformation Chain:");
        List<String> transformedData = employeeService.getSeniorEmployees().stream()
                .map(emp -> emp.fullName().toUpperCase())
                .map(name -> "Senior: " + name)
                .sorted()
                .limit(3)
                .collect(Collectors.toList());
        
        transformedData.forEach(data -> System.out.println("   • " + data));
        
        pauseForUser();
    }
    
    private void demonstrateComplexDataProcessing() {
        System.out.println("\n🚀 COMPLEX DATA PROCESSING SCENARIOS");
        System.out.println("=".repeat(50));
        
        // Multi-source data correlation
        System.out.println("🔄 Cross-Dataset Analysis:");
        
        // Transaction pattern analysis
        Map<String, Long> transactionPatterns = transactions.stream()
                .filter(Transaction::isCompleted)
                .collect(Collectors.groupingBy(
                    txn -> txn.getHourOfDay() + ":00-" + (txn.getHourOfDay() + 1) + ":00",
                    Collectors.counting()
                ));
        
        System.out.println("   📊 Transaction Volume by Hour (top 5):");
        transactionPatterns.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.printf("      • %-12s: %4d transactions%n",
                    entry.getKey(), entry.getValue()));
        
        // Log analysis with streams
        System.out.println("\n📝 Log Entry Analysis:");
        Map<LogEntry.LogLevel, Long> logDistribution = logEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::level, Collectors.counting()));
        
        System.out.println("   🚨 Log Level Distribution:");
        logDistribution.entrySet().stream()
                .sorted(Map.Entry.<LogEntry.LogLevel, Long>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("      • %-8s: %4d entries%n",
                    entry.getKey(), entry.getValue()));
        
        // Department performance summary
        System.out.println("\n🏢 Department Performance Summary:");
        Map<String, EmployeeAnalysisService.EmployeeSummary> summaries = 
                employeeService.getDepartmentSummaries();
        
        summaries.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(3)
                .forEach(entry -> {
                    var summary = entry.getValue();
                    System.out.printf("   • %s:%n", entry.getKey());
                    System.out.printf("      └─ Employees: %d | Avg Salary: $%.2f | Senior: %d%n",
                        summary.totalEmployees(), summary.averageSalary(), summary.seniorCount());
                });
        
        pauseForUser();
    }
    
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("⚠️ Please enter a valid number: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return result;
    }
    
    private void pauseForUser() {
        System.out.println("\n⏸️ Press Enter to continue...");
        scanner.nextLine();
    }
}