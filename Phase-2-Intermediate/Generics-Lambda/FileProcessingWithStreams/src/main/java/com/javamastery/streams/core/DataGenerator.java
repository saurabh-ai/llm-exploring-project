package com.javamastery.streams.core;

import com.javamastery.streams.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Data generator for creating sample datasets
 * Demonstrates functional approaches to data generation using streams
 */
public class DataGenerator {
    
    private final Random random = ThreadLocalRandom.current();
    
    // Sample data arrays
    private static final String[] FIRST_NAMES = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
        "Thomas", "Sarah", "Christopher", "Karen", "Charles", "Nancy", "Daniel", "Lisa",
        "Matthew", "Betty", "Anthony", "Helen", "Mark", "Sandra", "Donald", "Donna"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
        "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White"
    };
    
    private static final String[] CITIES = {
        "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia",
        "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville",
        "San Francisco", "Columbus", "Fort Worth", "Indianapolis", "Charlotte", "Seattle"
    };
    
    private static final String[] COUNTRIES = {
        "United States", "Canada", "United Kingdom", "Germany", "France", "Australia",
        "Japan", "South Korea", "Netherlands", "Sweden", "Norway", "Switzerland"
    };
    
    private static final String[] DEPARTMENTS = {
        "Engineering", "Marketing", "Sales", "Human Resources", "Finance", 
        "Operations", "Customer Service", "Product Management", "Legal", "IT"
    };
    
    private static final String[] TRANSACTION_CATEGORIES = {
        "Food & Dining", "Shopping", "Transportation", "Entertainment", "Bills & Utilities",
        "Travel", "Healthcare", "Education", "Groceries", "Gas", "Insurance", "Investment"
    };
    
    private static final String[] COMPONENTS = {
        "DatabaseService", "UserController", "PaymentProcessor", "AuthenticationService",
        "EmailService", "ReportGenerator", "FileUploadHandler", "APIGateway",
        "CacheManager", "MessageQueue", "NotificationService", "SchedulerService"
    };
    
    private static final String[] THREAD_NAMES = {
        "main", "http-nio-8080", "scheduler-1", "async-task", "worker-thread",
        "db-connection-pool", "message-consumer", "file-processor", "background-job"
    };
    
    /**
     * Generate random people data using streams
     */
    public List<Person> generatePeople(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> generatePerson("P" + String.format("%04d", i + 1)))
            .toList();
    }
    
    /**
     * Generate random transaction data using streams
     */
    public List<Transaction> generateTransactions(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> generateTransaction("T" + String.format("%06d", i + 1)))
            .toList();
    }
    
    /**
     * Generate random log entries using streams
     */
    public List<LogEntry> generateLogEntries(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> generateLogEntry())
            .sorted(Comparator.comparing(LogEntry::date).thenComparing(LogEntry::time))
            .toList();
    }
    
    private Person generatePerson(String id) {
        String firstName = randomElement(FIRST_NAMES);
        String lastName = randomElement(LAST_NAMES);
        String email = generateEmail(firstName, lastName);
        int age = random.nextInt(22, 65); // Age between 22 and 64
        String city = randomElement(CITIES);
        String country = randomElement(COUNTRIES);
        double salary = generateSalary(age);
        String department = randomElement(DEPARTMENTS);
        LocalDate joinDate = generateJoinDate();
        
        return new Person(id, firstName, lastName, email, age, city, country, 
                         salary, department, joinDate);
    }
    
    private Transaction generateTransaction(String transactionId) {
        String accountId = "ACC" + String.format("%04d", random.nextInt(1, 501)); // 500 unique accounts
        Transaction.TransactionType type = randomElement(Transaction.TransactionType.values());
        double amount = generateTransactionAmount(type);
        String category = randomElement(TRANSACTION_CATEGORIES);
        String description = generateTransactionDescription(category, type);
        LocalDateTime timestamp = generateTransactionTimestamp();
        String currency = random.nextDouble() < 0.9 ? "USD" : randomElement(new String[]{"EUR", "GBP", "CAD", "AUD"});
        
        return new Transaction(transactionId, accountId, type, amount, category, 
                             description, timestamp, currency);
    }
    
    private LogEntry generateLogEntry() {
        LocalDate date = LocalDate.now().minusDays(random.nextInt(30)); // Last 30 days
        String time = String.format("%02d:%02d:%02d", 
            random.nextInt(24), random.nextInt(60), random.nextInt(60));
        LogEntry.LogLevel level = generateLogLevel();
        String component = randomElement(COMPONENTS);
        String message = generateLogMessage(level, component);
        String threadName = randomElement(THREAD_NAMES) + "-" + random.nextInt(10);
        long responseTime = generateResponseTime(level);
        
        return new LogEntry(date, time, level, component, message, threadName, responseTime);
    }
    
    private String generateEmail(String firstName, String lastName) {
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "company.com", "example.org"};
        String localPart = firstName.toLowerCase() + "." + lastName.toLowerCase();
        if (random.nextDouble() < 0.3) {
            localPart += random.nextInt(100); // Add number sometimes
        }
        return localPart + "@" + randomElement(domains);
    }
    
    private double generateSalary(int age) {
        // Base salary influenced by age (experience)
        double baseSalary = 40000 + (age - 22) * 2000;
        // Add some randomness
        double variation = baseSalary * (random.nextDouble() * 0.4 - 0.2); // ±20%
        return Math.round((baseSalary + variation) / 1000) * 1000; // Round to nearest 1000
    }
    
    private LocalDate generateJoinDate() {
        // Join date between 1-15 years ago
        int daysAgo = random.nextInt(365, 365 * 15);
        return LocalDate.now().minusDays(daysAgo);
    }
    
    private double generateTransactionAmount(Transaction.TransactionType type) {
        return switch (type) {
            case CREDIT -> random.nextDouble(100, 5000);
            case DEBIT -> random.nextDouble(10, 2000);
            case TRANSFER -> random.nextDouble(50, 10000);
            case FEE -> random.nextDouble(1, 50);
        };
    }
    
    private String generateTransactionDescription(String category, Transaction.TransactionType type) {
        Map<String, String[]> descriptions = Map.of(
            "Food & Dining", new String[]{"Restaurant payment", "Coffee shop", "Fast food", "Grocery store"},
            "Transportation", new String[]{"Gas station", "Public transport", "Taxi ride", "Parking fee"},
            "Shopping", new String[]{"Online purchase", "Department store", "Electronics", "Clothing store"},
            "Bills & Utilities", new String[]{"Electricity bill", "Internet payment", "Phone bill", "Water bill"}
        );
        
        String[] options = descriptions.getOrDefault(category, new String[]{category + " transaction"});
        return randomElement(options) + " - " + type.toString().toLowerCase();
    }
    
    private LocalDateTime generateTransactionTimestamp() {
        // Random time in last 60 days
        LocalDateTime now = LocalDateTime.now();
        long minutesAgo = random.nextLong(60 * 24 * 60); // 60 days in minutes
        return now.minusMinutes(minutesAgo);
    }
    
    private LogEntry.LogLevel generateLogLevel() {
        // Weight distribution: DEBUG(30%), INFO(40%), WARN(20%), ERROR(8%), FATAL(2%)
        double rand = random.nextDouble();
        if (rand < 0.30) return LogEntry.LogLevel.DEBUG;
        if (rand < 0.70) return LogEntry.LogLevel.INFO;
        if (rand < 0.90) return LogEntry.LogLevel.WARN;
        if (rand < 0.98) return LogEntry.LogLevel.ERROR;
        return LogEntry.LogLevel.FATAL;
    }
    
    private String generateLogMessage(LogEntry.LogLevel level, String component) {
        return switch (level) {
            case DEBUG -> "Debug information for " + component + " operation";
            case INFO -> component + " processed successfully";
            case WARN -> "Warning in " + component + ": potential issue detected";
            case ERROR -> "Error in " + component + ": operation failed";
            case FATAL -> "Fatal error in " + component + ": system shutdown required";
        };
    }
    
    private long generateResponseTime(LogEntry.LogLevel level) {
        // Error logs tend to have higher response times
        return switch (level) {
            case DEBUG, INFO -> random.nextLong(1, 500);
            case WARN -> random.nextLong(100, 1500);
            case ERROR -> random.nextLong(500, 5000);
            case FATAL -> random.nextLong(2000, 10000);
        };
    }
    
    @SuppressWarnings("unchecked")
    private <T> T randomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
}