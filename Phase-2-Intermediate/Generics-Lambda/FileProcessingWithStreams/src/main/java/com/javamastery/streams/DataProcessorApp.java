package com.javamastery.streams;

import com.javamastery.streams.core.DataGenerator;
import com.javamastery.streams.processor.*;
import com.javamastery.streams.model.*;
import com.javamastery.streams.util.FileUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application demonstrating comprehensive data processing with Java Streams API
 * Interactive console application showcasing various stream operations and functional programming
 */
public class DataProcessorApp {
    
    private static final Scanner scanner = new Scanner(System.in);
    private PersonDataProcessor personProcessor;
    private TransactionProcessor transactionProcessor;
    private LogAnalysisProcessor logProcessor;
    
    public static void main(String[] args) {
        new DataProcessorApp().run();
    }
    
    public void run() {
        System.out.println("🌊 Data Processing with Streams - Interactive Demo");
        System.out.println("================================================");
        
        // Initialize processors with sample data
        initializeProcessors();
        
        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1 -> demonstratePersonAnalysis();
                case 2 -> demonstrateTransactionAnalysis();
                case 3 -> demonstrateLogAnalysis();
                case 4 -> demonstrateParallelProcessing();
                case 5 -> demonstrateCustomCollectors();
                case 6 -> demonstrateFileProcessing();
                case 0 -> {
                    System.out.println("Thanks for exploring Java Streams! 👋");
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice! Please try again.");
            }
        }
    }
    
    private void initializeProcessors() {
        System.out.println("🔄 Generating sample data...");
        
        // Generate sample data using our data generator
        DataGenerator generator = new DataGenerator();
        List<Person> people = generator.generatePeople(1000);
        List<Transaction> transactions = generator.generateTransactions(2000);
        List<LogEntry> logEntries = generator.generateLogEntries(1500);
        
        // Initialize processors
        personProcessor = new PersonDataProcessor(people);
        transactionProcessor = new TransactionProcessor(transactions);
        logProcessor = new LogAnalysisProcessor(logEntries);
        
        System.out.println("✅ Data generated successfully!");
        System.out.println("   - People: " + people.size());
        System.out.println("   - Transactions: " + transactions.size());
        System.out.println("   - Log Entries: " + logEntries.size());
        System.out.println();
    }
    
    private void showMainMenu() {
        System.out.println("\n📋 Main Menu:");
        System.out.println("1. 👥 Person Data Analysis");
        System.out.println("2. 💰 Transaction Analysis");
        System.out.println("3. 📄 Log Analysis");
        System.out.println("4. ⚡ Parallel Processing Demo");
        System.out.println("5. 🔧 Custom Collectors Demo");
        System.out.println("6. 📁 File Processing Demo");
        System.out.println("0. 🚪 Exit");
    }
    
    private void demonstratePersonAnalysis() {
        System.out.println("\n👥 Person Data Analysis");
        System.out.println("======================");
        
        // Show various stream operations on person data
        System.out.println("📊 Salary Statistics:");
        var stats = personProcessor.getSalaryStatistics();
        System.out.println("   " + stats);
        
        System.out.println("\n🏆 Top 5 Earners:");
        personProcessor.getTopEarners(5).forEach(person -> 
            System.out.println("   " + person.fullName() + " - $" + String.format("%.2f", person.salary())));
        
        System.out.println("\n🏢 Average Salary by Department:");
        personProcessor.getAverageSalaryByDepartment().forEach((dept, avg) ->
            System.out.println("   " + dept + ": $" + String.format("%.2f", avg)));
        
        System.out.println("\n📈 Summary Report:");
        System.out.println(personProcessor.generateSummaryReport());
        
        pauseForUser();
    }
    
    private void demonstrateTransactionAnalysis() {
        System.out.println("\n💰 Transaction Analysis");
        System.out.println("=======================");
        
        System.out.println("💳 Transaction Statistics:");
        var stats = transactionProcessor.getTransactionStatistics();
        System.out.println("   " + stats);
        
        System.out.println("\n📈 Total by Transaction Type:");
        transactionProcessor.getTotalByType().forEach((type, total) ->
            System.out.println("   " + type + ": $" + String.format("%.2f", total)));
        
        System.out.println("\n🚨 Suspicious Patterns (accounts with 3+ large transactions):");
        var suspicious = transactionProcessor.findSuspiciousPatterns();
        if (suspicious.isEmpty()) {
            System.out.println("   No suspicious patterns detected.");
        } else {
            suspicious.forEach((account, count) ->
                System.out.println("   Account " + account + ": " + count + " large transactions"));
        }
        
        System.out.println("\n🕐 Transaction Distribution by Hour:");
        transactionProcessor.getTransactionsByHour().entrySet().stream()
            .limit(5) // Show first 5 hours
            .forEach(entry -> 
                System.out.println("   " + String.format("%02d:00", entry.getKey()) + " - " + entry.getValue() + " transactions"));
        
        pauseForUser();
    }
    
    private void demonstrateLogAnalysis() {
        System.out.println("\n📄 Log Analysis");
        System.out.println("===============");
        
        System.out.println("🚨 Error Distribution:");
        logProcessor.getErrorDistribution().forEach((level, count) ->
            System.out.println("   " + level + ": " + count));
        
        System.out.println("\n⚡ Response Time Statistics:");
        var responseStats = logProcessor.getResponseTimeStatistics();
        System.out.println("   " + responseStats);
        
        System.out.println("\n🐌 Top Components with Errors:");
        logProcessor.getTopErrorComponents(5).forEach(entry ->
            System.out.println("   " + entry.getKey() + ": " + entry.getValue() + " errors"));
        
        System.out.println("\n🏥 System Health Report:");
        var healthReport = logProcessor.generateSystemHealthReport();
        System.out.println("   " + healthReport);
        
        pauseForUser();
    }
    
    private void demonstrateParallelProcessing() {
        System.out.println("\n⚡ Parallel Processing Demo");
        System.out.println("==========================");
        
        System.out.println("🔄 Running expensive operations...");
        
        // Sequential processing
        long startTime = System.currentTimeMillis();
        var sequentialResult = personProcessor.expensiveOperation();
        long sequentialTime = System.currentTimeMillis() - startTime;
        
        System.out.println("📊 Results:");
        System.out.println("   Sequential processing: " + sequentialTime + "ms");
        System.out.println("   Records processed: " + sequentialResult.size());
        
        System.out.println("   Parallel streams automatically utilize multiple CPU cores");
        System.out.println("   for improved performance on large datasets!");
        
        pauseForUser();
    }
    
    private void demonstrateCustomCollectors() {
        System.out.println("\n🔧 Custom Collectors Demo");
        System.out.println("=========================");
        
        System.out.println("💰 Transaction Amount Buckets:");
        var buckets = transactionProcessor.groupByAmountRanges();
        buckets.forEach((range, transactions) ->
            System.out.println("   $" + range + ": " + transactions.size() + " transactions"));
        
        System.out.println("\n📊 Person Age Demographics:");
        var demographics = personProcessor.getDepartmentDemographics();
        demographics.entrySet().stream().limit(3).forEach(entry -> {
            System.out.println("   " + entry.getKey() + ":");
            entry.getValue().forEach((ageGroup, count) ->
                System.out.println("      " + ageGroup + ": " + count));
        });
        
        pauseForUser();
    }
    
    private void demonstrateFileProcessing() {
        System.out.println("\n📁 File Processing Demo");
        System.out.println("=======================");
        
        try {
            // Create temporary directory for demo
            var tempDir = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"), "streams-demo");
            FileUtils.ensureDirectoryExists(tempDir);
            
            // Create sample files
            var sampleFile1 = tempDir.resolve("sample1.txt");
            var sampleFile2 = tempDir.resolve("sample2.txt");
            
            List<String> lines1 = List.of(
                "Java Streams API is powerful",
                "Functional programming in Java",
                "Lambda expressions simplify code",
                "Collectors provide flexible data aggregation"
            );
            
            List<String> lines2 = List.of(
                "Parallel streams improve performance",
                "Stream operations are lazy evaluated",
                "Filter, map, and reduce are fundamental operations"
            );
            
            FileUtils.writeLines(sampleFile1, lines1);
            FileUtils.writeLines(sampleFile2, lines2);
            
            // Demonstrate file operations
            System.out.println("📄 File Statistics:");
            System.out.println("   File 1 - Lines: " + FileUtils.countLines(sampleFile1) + 
                             ", Words: " + FileUtils.countWords(sampleFile1));
            System.out.println("   File 2 - Lines: " + FileUtils.countLines(sampleFile2) + 
                             ", Words: " + FileUtils.countWords(sampleFile2));
            
            System.out.println("📁 Directory size: " + FileUtils.getDirectorySize(tempDir) + " bytes");
            
            // Clean up
            java.nio.file.Files.deleteIfExists(sampleFile1);
            java.nio.file.Files.deleteIfExists(sampleFile2);
            java.nio.file.Files.deleteIfExists(tempDir);
            
        } catch (Exception e) {
            System.out.println("❌ Error in file processing demo: " + e.getMessage());
        }
        
        pauseForUser();
    }
    
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next(); // consume invalid input
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return result;
    }
    
    private void pauseForUser() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}