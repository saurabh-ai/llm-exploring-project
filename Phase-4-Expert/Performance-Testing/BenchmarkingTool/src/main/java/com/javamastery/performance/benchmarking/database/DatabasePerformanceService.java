package com.javamastery.performance.benchmarking.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Service for database performance testing and benchmarking
 */
@Service
public class DatabasePerformanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceService.class);
    
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    
    @Value("${performance.database.test.table:performance_test}")
    private String testTable;
    
    @Autowired
    public DatabasePerformanceService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }
    
    /**
     * Run comprehensive database performance test
     */
    public CompletableFuture<DatabasePerformanceResult> runDatabasePerformanceTest(DatabaseTestConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Starting database performance test: {}", config.getTestName());
            
            DatabasePerformanceResult result = new DatabasePerformanceResult();
            result.setTestName(config.getTestName());
            result.setStartTime(LocalDateTime.now());
            
            try {
                // Prepare test environment
                prepareTestData(config);
                
                // Run different types of database tests
                Map<String, Long> testResults = new HashMap<>();
                
                testResults.put("connection_pool", testConnectionPoolPerformance(config));
                testResults.put("insert_performance", testInsertPerformance(config));
                testResults.put("select_performance", testSelectPerformance(config));
                testResults.put("update_performance", testUpdatePerformance(config));
                testResults.put("delete_performance", testDeletePerformance(config));
                testResults.put("concurrent_operations", testConcurrentOperations(config));
                
                result.setTestResults(testResults);
                result.setSuccess(true);
                
                // Calculate statistics
                calculateStatistics(result, testResults);
                
            } catch (Exception e) {
                logger.error("Database performance test failed: {}", e.getMessage(), e);
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            } finally {
                result.setEndTime(LocalDateTime.now());
                cleanupTestData(config);
            }
            
            return result;
        });
    }
    
    private long testConnectionPoolPerformance(DatabaseTestConfig config) {
        logger.info("Testing connection pool performance...");
        
        long startTime = System.currentTimeMillis();
        
        ExecutorService executor = Executors.newFixedThreadPool(config.getConcurrentConnections());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < config.getConnectionTestCount(); i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    // Simple connection test
                    try (PreparedStatement stmt = connection.prepareStatement("SELECT 1")) {
                        stmt.executeQuery();
                    }
                } catch (Exception e) {
                    logger.error("Connection test failed: {}", e.getMessage());
                }
            }, executor));
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Connection pool test completed in {} ms", duration);
        return duration;
    }
    
    private long testInsertPerformance(DatabaseTestConfig config) {
        logger.info("Testing insert performance...");
        
        long startTime = System.currentTimeMillis();
        
        String insertSql = "INSERT INTO " + testTable + " (test_data, created_at) VALUES (?, ?)";
        
        for (int i = 0; i < config.getInsertCount(); i++) {
            jdbcTemplate.update(insertSql, "Test data " + i, LocalDateTime.now());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Insert performance test completed: {} records in {} ms", config.getInsertCount(), duration);
        return duration;
    }
    
    private long testSelectPerformance(DatabaseTestConfig config) {
        logger.info("Testing select performance...");
        
        long startTime = System.currentTimeMillis();
        
        String selectSql = "SELECT * FROM " + testTable + " LIMIT ?";
        
        for (int i = 0; i < config.getSelectCount(); i++) {
            jdbcTemplate.queryForList(selectSql, Math.min(100, config.getInsertCount()));
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Select performance test completed: {} queries in {} ms", config.getSelectCount(), duration);
        return duration;
    }
    
    private long testUpdatePerformance(DatabaseTestConfig config) {
        logger.info("Testing update performance...");
        
        long startTime = System.currentTimeMillis();
        
        String updateSql = "UPDATE " + testTable + " SET test_data = ? WHERE id = ?";
        
        for (int i = 1; i <= config.getUpdateCount(); i++) {
            jdbcTemplate.update(updateSql, "Updated data " + i, i);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Update performance test completed: {} updates in {} ms", config.getUpdateCount(), duration);
        return duration;
    }
    
    private long testDeletePerformance(DatabaseTestConfig config) {
        logger.info("Testing delete performance...");
        
        long startTime = System.currentTimeMillis();
        
        String deleteSql = "DELETE FROM " + testTable + " WHERE id > ?";
        int deleteThreshold = config.getInsertCount() - config.getDeleteCount();
        
        jdbcTemplate.update(deleteSql, deleteThreshold);
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Delete performance test completed in {} ms", duration);
        return duration;
    }
    
    private long testConcurrentOperations(DatabaseTestConfig config) {
        logger.info("Testing concurrent database operations...");
        
        long startTime = System.currentTimeMillis();
        
        ExecutorService executor = Executors.newFixedThreadPool(config.getConcurrentConnections());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        // Mix of operations
        for (int i = 0; i < config.getConcurrentOperations(); i++) {
            final int operationId = i;
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    if (operationId % 4 == 0) {
                        // Insert
                        jdbcTemplate.update("INSERT INTO " + testTable + " (test_data, created_at) VALUES (?, ?)",
                                          "Concurrent insert " + operationId, LocalDateTime.now());
                    } else if (operationId % 4 == 1) {
                        // Select
                        jdbcTemplate.queryForList("SELECT * FROM " + testTable + " LIMIT 10");
                    } else if (operationId % 4 == 2) {
                        // Update
                        jdbcTemplate.update("UPDATE " + testTable + " SET test_data = ? WHERE id = 1",
                                          "Concurrent update " + operationId);
                    } else {
                        // Count
                        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + testTable, Integer.class);
                    }
                } catch (Exception e) {
                    logger.error("Concurrent operation {} failed: {}", operationId, e.getMessage());
                }
            }, executor));
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Concurrent operations test completed: {} operations in {} ms", 
                   config.getConcurrentOperations(), duration);
        return duration;
    }
    
    private void prepareTestData(DatabaseTestConfig config) {
        try {
            // Create test table if it doesn't exist
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS %s (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    test_data VARCHAR(255),
                    created_at TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """.formatted(testTable);
            
            jdbcTemplate.execute(createTableSql);
            
            // Clear existing test data
            jdbcTemplate.update("DELETE FROM " + testTable + " WHERE test_data LIKE 'Test data%'");
            
            logger.info("Test environment prepared for table: {}", testTable);
            
        } catch (Exception e) {
            logger.error("Failed to prepare test data: {}", e.getMessage());
            throw new RuntimeException("Test environment preparation failed", e);
        }
    }
    
    private void cleanupTestData(DatabaseTestConfig config) {
        try {
            // Clean up test data
            jdbcTemplate.update("DELETE FROM " + testTable + " WHERE test_data LIKE 'Test data%' OR test_data LIKE 'Concurrent%' OR test_data LIKE 'Updated%'");
            logger.info("Test data cleanup completed");
        } catch (Exception e) {
            logger.warn("Failed to cleanup test data: {}", e.getMessage());
        }
    }
    
    private void calculateStatistics(DatabasePerformanceResult result, Map<String, Long> testResults) {
        long totalTime = testResults.values().stream().mapToLong(Long::longValue).sum();
        result.setTotalExecutionTime(totalTime);
        
        double avgTime = testResults.values().stream().mapToLong(Long::longValue).average().orElse(0.0);
        result.setAverageOperationTime(avgTime);
        
        long maxTime = testResults.values().stream().mapToLong(Long::longValue).max().orElse(0L);
        result.setMaxOperationTime(maxTime);
        
        long minTime = testResults.values().stream().mapToLong(Long::longValue).min().orElse(0L);
        result.setMinOperationTime(minTime);
        
        logger.info("Database performance statistics calculated: total={}ms, avg={}ms, max={}ms, min={}ms",
                   totalTime, avgTime, maxTime, minTime);
    }
}