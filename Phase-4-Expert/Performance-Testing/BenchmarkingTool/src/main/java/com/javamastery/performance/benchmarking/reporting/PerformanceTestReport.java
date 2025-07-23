package com.javamastery.performance.benchmarking.reporting;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Performance test report data structure
 */
public class PerformanceTestReport {
    
    private String reportId;
    private String reportName;
    private LocalDateTime generatedAt;
    private String reportType; // HTML, JSON, PDF
    private String filePath;
    private Map<String, Object> summary;
    private long reportSize;
    
    public PerformanceTestReport() {
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public long getReportSize() {
        return reportSize;
    }

    public void setReportSize(long reportSize) {
        this.reportSize = reportSize;
    }
}