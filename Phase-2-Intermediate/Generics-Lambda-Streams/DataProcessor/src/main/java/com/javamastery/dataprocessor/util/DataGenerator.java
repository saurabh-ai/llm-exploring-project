package com.javamastery.dataprocessor.util;

import com.javamastery.dataprocessor.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Data generator using functional programming and streams
 * Demonstrates supplier functions, random data generation, and stream creation
 */
public class DataGenerator {
    
    private static final Random RANDOM = ThreadLocalRandom.current();
    
    private static final String[] FIRST_NAMES = {
        "Alice", "Bob", "Carol", "David", "Emma", "Frank", "Grace", "Henry",
        "Ivy", "Jack", "Karen", "Leo", "Maria", "Nathan", "Olivia", "Paul",
        "Quinn", "Rachel", "Steve", "Tina", "Uma", "Victor", "Wendy", "Xavier",
        "Yara", "Zoe", "Alexander", "Bella", "Christopher", "Diana"
    };
    
    private static final String[] LAST_NAMES = {
        "Anderson", "Brown", "Clark", "Davis", "Evans", "Foster", "Garcia",
        "Hall", "Irving", "Johnson", "King", "Lopez", "Miller", "Nelson",
        "O'Connor", "Parker", "Quinn", "Rodriguez", "Smith", "Taylor",
        "Underwood", "Valdez", "Williams", "Young", "Zhang"
    };
    
    private static final String[] DEPARTMENTS = {
        "Engineering", "Marketing", "Sales", "Human Resources", "Finance",
        "Operations", "Customer Support", "Product Management", "Design", "Legal"
    };
    
    private static final String[] POSITIONS = {
        "Junior Developer", "Senior Developer", "Team Lead", "Manager",
        "Director", "Analyst", "Specialist", "Coordinator", "Associate",
        "Executive", "Consultant", "Administrator"
    };
    
    private static final String[] SKILLS = {
        "Java", "Python", "JavaScript", "SQL", "React", "Angular", "Node.js",
        "Spring Boot", "Docker", "Kubernetes", "AWS", "Machine Learning",
        "Data Analysis", "Project Management", "Leadership", "Communication"
    };
    
    private static final String[] TRANSACTION_DESCRIPTIONS = {
        "Online Purchase", "ATM Withdrawal", "Direct Deposit", "Wire Transfer",
        "Bill Payment", "Refund", "Cash Deposit", "Investment", "Loan Payment",
        "Subscription Fee", "Grocery Store", "Gas Station", "Restaurant", "Hotel"
    };
    
    private static final String[] COMPONENTS = {
        "UserService", "AuthenticationModule", "DatabaseConnector", "PaymentProcessor",
        "EmailService", "NotificationSystem", "ReportGenerator", "FileUploader",
        "SearchEngine", "CacheManager", "SecurityModule", "BackupService"
    };
    
    // Employee generation using streams
    public List<Employee> generateEmployees(int count) {
        return IntStream.range(0, count)
                .mapToObj(this::createRandomEmployee)
                .toList();
    }
    
    // Stream-based employee creation
    public Stream<Employee> employeeStream(int count) {
        return Stream.generate(this::createRandomEmployee)
                .limit(count);
    }
    
    private Employee createRandomEmployee(int id) {
        return new Employee(
                (long) id,
                randomElement(FIRST_NAMES),
                randomElement(LAST_NAMES),
                generateEmail(),
                randomElement(DEPARTMENTS),
                randomElement(POSITIONS),
                generateSalary(),
                generateHireDate(),
                RANDOM.nextInt(20), // 0-19 years experience
                generateSkills()
        );
    }
    
    private Employee createRandomEmployee() {
        return createRandomEmployee(RANDOM.nextInt(100000));
    }
    
    // Transaction generation with functional approach
    public List<Transaction> generateTransactions(int count) {
        return Stream.generate(this::createRandomTransaction)
                .limit(count)
                .toList();
    }
    
    public Stream<Transaction> transactionStream() {
        return Stream.generate(this::createRandomTransaction);
    }
    
    private Transaction createRandomTransaction() {
        return new Transaction(
                "TXN" + RANDOM.nextLong(1000000, 9999999),
                "ACC" + RANDOM.nextLong(100000, 999999),
                "CUST" + RANDOM.nextLong(10000, 99999),
                randomEnum(Transaction.TransactionType.class),
                generateTransactionAmount(),
                "USD",
                generateRandomDateTime(),
                randomElement(TRANSACTION_DESCRIPTIONS),
                randomEnum(Transaction.TransactionStatus.class),
                generateMerchantCategory()
        );
    }
    
    // Log entry generation
    public List<LogEntry> generateLogEntries(int count) {
        return IntStream.range(0, count)
                .mapToObj(this::createRandomLogEntry)
                .toList();
    }
    
    private LogEntry createRandomLogEntry(int id) {
        return new LogEntry(
                "LOG" + id,
                generateRandomDateTime(),
                randomEnum(LogEntry.LogLevel.class),
                randomElement(COMPONENTS),
                generateLogMessage(),
                "Thread-" + RANDOM.nextInt(20),
                RANDOM.nextLong(100, 10000), // Response time in ms
                "USER" + RANDOM.nextInt(1000),
                "SESSION" + RANDOM.nextLong(100000, 999999),
                generateRandomIP()
        );
    }
    
    // Functional suppliers for different data types
    public Supplier<String> nameSupplier() {
        return () -> randomElement(FIRST_NAMES) + " " + randomElement(LAST_NAMES);
    }
    
    public Supplier<BigDecimal> salarySupplier() {
        return this::generateSalary;
    }
    
    public Supplier<LocalDate> dateSupplier() {
        return this::generateHireDate;
    }
    
    // Utility methods using functional programming
    private String randomElement(String[] array) {
        return array[RANDOM.nextInt(array.length)];
    }
    
    private <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        return constants[RANDOM.nextInt(constants.length)];
    }
    
    private String generateEmail() {
        return (randomElement(FIRST_NAMES) + "." + randomElement(LAST_NAMES))
                .toLowerCase() + "@company.com";
    }
    
    private BigDecimal generateSalary() {
        // Generate salary between $35,000 and $200,000
        double salary = 35000 + RANDOM.nextDouble() * 165000;
        return BigDecimal.valueOf(Math.round(salary));
    }
    
    private LocalDate generateHireDate() {
        // Generate hire dates within last 10 years
        LocalDate now = LocalDate.now();
        return now.minusDays(RANDOM.nextInt(3650));
    }
    
    private String generateSkills() {
        Set<String> skillSet = new HashSet<>();
        int numSkills = RANDOM.nextInt(5) + 1; // 1-5 skills
        
        while (skillSet.size() < numSkills) {
            skillSet.add(randomElement(SKILLS));
        }
        
        return String.join(", ", skillSet);
    }
    
    private BigDecimal generateTransactionAmount() {
        // Generate amounts with different distributions
        double amount = switch (RANDOM.nextInt(4)) {
            case 0 -> RANDOM.nextDouble() * 100; // Small transactions
            case 1 -> 100 + RANDOM.nextDouble() * 900; // Medium transactions
            case 2 -> 1000 + RANDOM.nextDouble() * 9000; // Large transactions
            default -> 10000 + RANDOM.nextDouble() * 90000; // Very large transactions
        };
        
        return BigDecimal.valueOf(Math.round(amount * 100.0) / 100.0);
    }
    
    private LocalDateTime generateRandomDateTime() {
        // Generate datetime within last 30 days
        LocalDateTime now = LocalDateTime.now();
        return now.minusMinutes(RANDOM.nextInt(43200)); // 30 days in minutes
    }
    
    private String generateMerchantCategory() {
        String[] categories = {"Grocery", "Gas", "Restaurant", "Retail", "Online", 
                              "ATM", "Transfer", "Utility", "Entertainment", "Travel"};
        return randomElement(categories);
    }
    
    private String generateLogMessage() {
        String[] templates = {
            "User %s logged in successfully",
            "Database query executed in %d ms",
            "Payment processed for amount $%.2f",
            "Email sent to %s",
            "File uploaded: %s",
            "Cache miss for key: %s",
            "Security alert: suspicious activity detected",
            "Backup completed successfully",
            "API request processed",
            "System health check passed"
        };
        
        String template = randomElement(templates);
        
        return switch (template.charAt(template.indexOf('%') + 1)) {
            case 's' -> String.format(template, "user" + RANDOM.nextInt(1000));
            case 'd' -> String.format(template, RANDOM.nextInt(5000));
            case 'f' -> String.format(template, RANDOM.nextDouble() * 1000);
            default -> template.replaceAll("%.", "N/A");
        };
    }
    
    private String generateRandomIP() {
        return RANDOM.nextInt(256) + "." + 
               RANDOM.nextInt(256) + "." + 
               RANDOM.nextInt(256) + "." + 
               RANDOM.nextInt(256);
    }
    
    // Batch generation methods using parallel streams
    public List<Employee> generateEmployeesParallel(int count) {
        return IntStream.range(0, count)
                .parallel()
                .mapToObj(this::createRandomEmployee)
                .toList();
    }
    
    public List<Transaction> generateTransactionsParallel(int count) {
        return IntStream.range(0, count)
                .parallel()
                .mapToObj(i -> createRandomTransaction())
                .toList();
    }
}