package com.javamastery.performance.benchmarking.reporting;

import com.javamastery.performance.benchmarking.database.DatabasePerformanceResult;
import com.javamastery.performance.benchmarking.dto.BenchmarkResult;
import com.javamastery.performance.benchmarking.microservice.MicroserviceTestResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating comprehensive performance test reports
 */
@Service
public class ReportingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);
    
    private final ObjectMapper objectMapper;
    private final List<PerformanceTestReport> testReports;
    
    public ReportingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.testReports = new ArrayList<>();
    }
    
    /**
     * Generate comprehensive HTML report for all test results
     */
    public String generateHtmlReport(List<BenchmarkResult> benchmarkResults,
                                   List<DatabasePerformanceResult> databaseResults,
                                   List<MicroserviceTestResult> microserviceResults) {
        
        logger.info("Generating comprehensive HTML performance report");
        
        StringBuilder html = new StringBuilder();
        html.append(generateHtmlHeader());
        html.append(generateExecutiveSummary(benchmarkResults, databaseResults, microserviceResults));
        html.append(generateBenchmarkSection(benchmarkResults));
        html.append(generateDatabaseSection(databaseResults));
        html.append(generateMicroserviceSection(microserviceResults));
        html.append(generateChartsSection(benchmarkResults, databaseResults, microserviceResults));
        html.append(generateRecommendationsSection(benchmarkResults, databaseResults, microserviceResults));
        html.append(generateHtmlFooter());
        
        return html.toString();
    }
    
    /**
     * Save report to file
     */
    public String saveReportToFile(String htmlContent, String reportName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("performance_report_%s_%s.html", 
                                           reportName.replaceAll("\\s+", "_"), timestamp);
            
            File reportsDir = new File("target/reports");
            reportsDir.mkdirs();
            
            File reportFile = new File(reportsDir, filename);
            
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(htmlContent);
            }
            
            logger.info("Performance report saved to: {}", reportFile.getAbsolutePath());
            return reportFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.error("Failed to save report: {}", e.getMessage());
            throw new RuntimeException("Report generation failed", e);
        }
    }
    
    /**
     * Generate JSON summary for API responses
     */
    public Map<String, Object> generateJsonSummary(List<BenchmarkResult> benchmarkResults,
                                                  List<DatabasePerformanceResult> databaseResults,
                                                  List<MicroserviceTestResult> microserviceResults) {
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("generatedAt", LocalDateTime.now());
        summary.put("totalTests", benchmarkResults.size() + databaseResults.size() + microserviceResults.size());
        
        // Benchmark summary
        if (!benchmarkResults.isEmpty()) {
            Map<String, Object> benchmarkSummary = new HashMap<>();
            benchmarkSummary.put("totalBenchmarks", benchmarkResults.size());
            benchmarkSummary.put("averageResponseTime", 
                benchmarkResults.stream().mapToDouble(BenchmarkResult::getAverageResponseTime).average().orElse(0.0));
            benchmarkSummary.put("averageThroughput", 
                benchmarkResults.stream().mapToDouble(BenchmarkResult::getThroughput).average().orElse(0.0));
            summary.put("benchmarkSummary", benchmarkSummary);
        }
        
        // Database summary
        if (!databaseResults.isEmpty()) {
            Map<String, Object> databaseSummary = new HashMap<>();
            databaseSummary.put("totalDatabaseTests", databaseResults.size());
            databaseSummary.put("averageExecutionTime", 
                databaseResults.stream().mapToLong(DatabasePerformanceResult::getTotalExecutionTime).average().orElse(0.0));
            summary.put("databaseSummary", databaseSummary);
        }
        
        // Microservice summary
        if (!microserviceResults.isEmpty()) {
            Map<String, Object> microserviceSummary = new HashMap<>();
            microserviceSummary.put("totalMicroserviceTests", microserviceResults.size());
            microserviceSummary.put("averageSuccessRate", 
                microserviceResults.stream().mapToDouble(MicroserviceTestResult::getOverallSuccessRate).average().orElse(0.0));
            summary.put("microserviceSummary", microserviceSummary);
        }
        
        return summary;
    }
    
    private String generateHtmlHeader() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Performance Test Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1, h2, h3 { color: #2c3e50; }
                    .summary { background: #3498db; color: white; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
                    .test-section { margin-bottom: 30px; }
                    .test-result { background: #ecf0f1; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .success { border-left: 5px solid #27ae60; }
                    .failure { border-left: 5px solid #e74c3c; }
                    .chart-container { margin: 20px 0; text-align: center; }
                    table { width: 100%; border-collapse: collapse; margin: 15px 0; }
                    th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
                    th { background-color: #34495e; color: white; }
                    .metric { display: inline-block; margin: 10px; padding: 15px; background: #f8f9fa; border-radius: 5px; min-width: 150px; text-align: center; }
                    .metric-value { font-size: 24px; font-weight: bold; color: #2c3e50; }
                    .metric-label { font-size: 14px; color: #7f8c8d; }
                    .recommendations { background: #fffacd; padding: 20px; border-radius: 5px; border-left: 5px solid #ffd700; }
                </style>
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            </head>
            <body>
                <div class="container">
                    <h1>Performance Test Report</h1>
                    <p>Generated on: %s</p>
            """.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    private String generateExecutiveSummary(List<BenchmarkResult> benchmarkResults,
                                          List<DatabasePerformanceResult> databaseResults,
                                          List<MicroserviceTestResult> microserviceResults) {
        
        int totalTests = benchmarkResults.size() + databaseResults.size() + microserviceResults.size();
        
        StringBuilder summary = new StringBuilder();
        summary.append("<div class=\"summary\">");
        summary.append("<h2>Executive Summary</h2>");
        summary.append("<div class=\"metric\">");
        summary.append("<div class=\"metric-value\">").append(totalTests).append("</div>");
        summary.append("<div class=\"metric-label\">Total Tests</div>");
        summary.append("</div>");
        
        if (!benchmarkResults.isEmpty()) {
            double avgResponseTime = benchmarkResults.stream()
                .mapToDouble(BenchmarkResult::getAverageResponseTime).average().orElse(0.0);
            summary.append("<div class=\"metric\">");
            summary.append("<div class=\"metric-value\">").append(String.format("%.2f ms", avgResponseTime)).append("</div>");
            summary.append("<div class=\"metric-label\">Avg Response Time</div>");
            summary.append("</div>");
        }
        
        if (!microserviceResults.isEmpty()) {
            double avgSuccessRate = microserviceResults.stream()
                .mapToDouble(MicroserviceTestResult::getOverallSuccessRate).average().orElse(0.0);
            summary.append("<div class=\"metric\">");
            summary.append("<div class=\"metric-value\">").append(String.format("%.1f%%", avgSuccessRate)).append("</div>");
            summary.append("<div class=\"metric-label\">Avg Success Rate</div>");
            summary.append("</div>");
        }
        
        summary.append("</div>");
        return summary.toString();
    }
    
    private String generateBenchmarkSection(List<BenchmarkResult> results) {
        if (results.isEmpty()) {
            return "";
        }
        
        StringBuilder section = new StringBuilder();
        section.append("<div class=\"test-section\">");
        section.append("<h2>Load Testing Results</h2>");
        
        for (BenchmarkResult result : results) {
            String cssClass = result.getErrorRate() < 0.05 ? "success" : "failure";
            section.append("<div class=\"test-result ").append(cssClass).append("\">");
            section.append("<h3>").append(result.getTestName()).append("</h3>");
            section.append("<p><strong>Total Requests:</strong> ").append(result.getTotalRequests()).append("</p>");
            section.append("<p><strong>Success Rate:</strong> ").append(String.format("%.2f%%", (1 - result.getErrorRate()) * 100)).append("</p>");
            section.append("<p><strong>Average Response Time:</strong> ").append(String.format("%.2f ms", result.getAverageResponseTime())).append("</p>");
            section.append("<p><strong>Throughput:</strong> ").append(String.format("%.2f req/s", result.getThroughput())).append("</p>");
            section.append("</div>");
        }
        
        section.append("</div>");
        return section.toString();
    }
    
    private String generateDatabaseSection(List<DatabasePerformanceResult> results) {
        if (results.isEmpty()) {
            return "";
        }
        
        StringBuilder section = new StringBuilder();
        section.append("<div class=\"test-section\">");
        section.append("<h2>Database Performance Results</h2>");
        
        for (DatabasePerformanceResult result : results) {
            String cssClass = result.isSuccess() ? "success" : "failure";
            section.append("<div class=\"test-result ").append(cssClass).append("\">");
            section.append("<h3>").append(result.getTestName()).append("</h3>");
            section.append("<p><strong>Total Execution Time:</strong> ").append(result.getTotalExecutionTime()).append(" ms</p>");
            section.append("<p><strong>Average Operation Time:</strong> ").append(String.format("%.2f ms", result.getAverageOperationTime())).append("</p>");
            
            if (result.getTestResults() != null) {
                section.append("<table>");
                section.append("<tr><th>Operation</th><th>Time (ms)</th></tr>");
                result.getTestResults().forEach((operation, time) -> {
                    section.append("<tr><td>").append(operation).append("</td><td>").append(time).append("</td></tr>");
                });
                section.append("</table>");
            }
            section.append("</div>");
        }
        
        section.append("</div>");
        return section.toString();
    }
    
    private String generateMicroserviceSection(List<MicroserviceTestResult> results) {
        if (results.isEmpty()) {
            return "";
        }
        
        StringBuilder section = new StringBuilder();
        section.append("<div class=\"test-section\">");
        section.append("<h2>Microservice Stress Test Results</h2>");
        
        for (MicroserviceTestResult result : results) {
            String cssClass = result.getOverallSuccessRate() > 95.0 ? "success" : "failure";
            section.append("<div class=\"test-result ").append(cssClass).append("\">");
            section.append("<h3>").append(result.getTestName()).append("</h3>");
            section.append("<p><strong>Total Requests:</strong> ").append(result.getTotalRequests()).append("</p>");
            section.append("<p><strong>Overall Success Rate:</strong> ").append(String.format("%.2f%%", result.getOverallSuccessRate())).append("</p>");
            section.append("<p><strong>Average Response Time:</strong> ").append(String.format("%.2f ms", result.getAverageResponseTime())).append("</p>");
            section.append("</div>");
        }
        
        section.append("</div>");
        return section.toString();
    }
    
    private String generateChartsSection(List<BenchmarkResult> benchmarkResults,
                                       List<DatabasePerformanceResult> databaseResults,
                                       List<MicroserviceTestResult> microserviceResults) {
        
        StringBuilder charts = new StringBuilder();
        charts.append("<div class=\"test-section\">");
        charts.append("<h2>Performance Charts</h2>");
        
        if (!benchmarkResults.isEmpty()) {
            charts.append("<div class=\"chart-container\">");
            charts.append("<h3>Response Time Comparison</h3>");
            charts.append("<canvas id=\"responseTimeChart\" width=\"400\" height=\"200\"></canvas>");
            charts.append("</div>");
        }
        
        charts.append("</div>");
        
        // Add chart JavaScript
        charts.append("<script>");
        if (!benchmarkResults.isEmpty()) {
            charts.append(generateResponseTimeChartScript(benchmarkResults));
        }
        charts.append("</script>");
        
        return charts.toString();
    }
    
    private String generateResponseTimeChartScript(List<BenchmarkResult> results) {
        String labels = results.stream()
            .map(r -> "'" + r.getTestName() + "'")
            .collect(Collectors.joining(","));
            
        String data = results.stream()
            .map(r -> String.valueOf(r.getAverageResponseTime()))
            .collect(Collectors.joining(","));
        
        return """
            const ctx = document.getElementById('responseTimeChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: [%s],
                    datasets: [{
                        label: 'Average Response Time (ms)',
                        data: [%s],
                        backgroundColor: 'rgba(52, 152, 219, 0.8)',
                        borderColor: 'rgba(52, 152, 219, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
            """.formatted(labels, data);
    }
    
    private String generateRecommendationsSection(List<BenchmarkResult> benchmarkResults,
                                                List<DatabasePerformanceResult> databaseResults,
                                                List<MicroserviceTestResult> microserviceResults) {
        
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("<div class=\"recommendations\">");
        recommendations.append("<h2>Performance Recommendations</h2>");
        recommendations.append("<ul>");
        
        // Analyze benchmark results
        for (BenchmarkResult result : benchmarkResults) {
            if (result.getAverageResponseTime() > 1000) {
                recommendations.append("<li><strong>").append(result.getTestName())
                    .append(":</strong> High response time detected. Consider optimizing application performance or scaling resources.</li>");
            }
            if (result.getErrorRate() > 0.05) {
                recommendations.append("<li><strong>").append(result.getTestName())
                    .append(":</strong> Error rate exceeds 5%. Investigate error causes and improve error handling.</li>");
            }
        }
        
        // Analyze microservice results
        for (MicroserviceTestResult result : microserviceResults) {
            if (result.getOverallSuccessRate() < 95.0) {
                recommendations.append("<li><strong>").append(result.getTestName())
                    .append(":</strong> Success rate below 95%. Implement circuit breaker patterns and improve resilience.</li>");
            }
        }
        
        // General recommendations
        recommendations.append("<li>Consider implementing caching strategies for frequently accessed data.</li>");
        recommendations.append("<li>Monitor and optimize database queries for better performance.</li>");
        recommendations.append("<li>Implement auto-scaling based on performance metrics.</li>");
        
        recommendations.append("</ul>");
        recommendations.append("</div>");
        
        return recommendations.toString();
    }
    
    private String generateHtmlFooter() {
        return """
                </div>
            </body>
            </html>
            """;
    }
}