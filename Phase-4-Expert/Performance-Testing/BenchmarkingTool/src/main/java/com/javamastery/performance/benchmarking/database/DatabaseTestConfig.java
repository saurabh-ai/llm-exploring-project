package com.javamastery.performance.benchmarking.database;

/**
 * Configuration for database performance tests
 */
public class DatabaseTestConfig {
    
    private String testName;
    private int insertCount = 1000;
    private int selectCount = 500;
    private int updateCount = 300;
    private int deleteCount = 100;
    private int concurrentConnections = 10;
    private int connectionTestCount = 100;
    private int concurrentOperations = 200;
    
    public DatabaseTestConfig() {}
    
    public DatabaseTestConfig(String testName) {
        this.testName = testName;
    }

    // Getters and Setters
    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }

    public int getConcurrentConnections() {
        return concurrentConnections;
    }

    public void setConcurrentConnections(int concurrentConnections) {
        this.concurrentConnections = concurrentConnections;
    }

    public int getConnectionTestCount() {
        return connectionTestCount;
    }

    public void setConnectionTestCount(int connectionTestCount) {
        this.connectionTestCount = connectionTestCount;
    }

    public int getConcurrentOperations() {
        return concurrentOperations;
    }

    public void setConcurrentOperations(int concurrentOperations) {
        this.concurrentOperations = concurrentOperations;
    }
}