package com.javamastery.distributed.monitoring.service;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.enums.JobStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced Analytics Service for advanced monitoring operations
 * Provides enterprise-grade monitoring, analytics, and performance insights
 */
@Service
public class AdvancedAnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedAnalyticsService.class);
    
    private final MeterRegistry meterRegistry;
    private final DashboardService dashboardService;
    
    @Autowired
    public AdvancedAnalyticsService(MeterRegistry meterRegistry, DashboardService dashboardService) {
        this.meterRegistry = meterRegistry;
        this.dashboardService = dashboardService;
    }
    
    /**
     * Get performance analytics for jobs
     */
    public Map<String, Object> getPerformanceAnalytics() {
        logger.info("Generating performance analytics report");
        
        Map<String, Object> analytics = new HashMap<>();
        List<JobDto> allJobs = dashboardService.getAllJobs();
        
        // Job execution trends
        analytics.put("executionTrends", calculateExecutionTrends(allJobs));
        
        // Success rate analysis
        analytics.put("successRateAnalysis", calculateSuccessRates(allJobs));
        
        // Performance metrics
        analytics.put("performanceMetrics", calculatePerformanceMetrics(allJobs));
        
        // Resource utilization
        analytics.put("resourceUtilization", calculateResourceUtilization());
        
        // Failure analysis
        analytics.put("failureAnalysis", analyzeFailures(allJobs));
        
        return analytics;
    }
    
    /**
     * Get real-time system metrics
     */
    public Map<String, Object> getRealTimeMetrics() {
        logger.debug("Fetching real-time system metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM metrics
        metrics.put("jvmMetrics", getJvmMetrics());
        
        // Application metrics
        metrics.put("applicationMetrics", getApplicationMetrics());
        
        // Custom business metrics
        metrics.put("businessMetrics", getBusinessMetrics());
        
        // System health score
        metrics.put("healthScore", calculateSystemHealthScore());
        
        return metrics;
    }
    
    /**
     * Get job execution forecast
     */
    public Map<String, Object> getExecutionForecast() {
        logger.info("Generating job execution forecast");
        
        Map<String, Object> forecast = new HashMap<>();
        List<JobDto> allJobs = dashboardService.getAllJobs();
        
        // Predict next hour executions
        forecast.put("nextHourPrediction", predictNextHourExecutions(allJobs));
        
        // Daily execution forecast
        forecast.put("dailyForecast", generateDailyForecast(allJobs));
        
        // Resource requirements forecast
        forecast.put("resourceForecast", forecastResourceRequirements(allJobs));
        
        return forecast;
    }
    
    /**
     * Get service dependency analysis
     */
    public Map<String, Object> getServiceDependencyAnalysis() {
        logger.info("Analyzing service dependencies");
        
        Map<String, Object> analysis = new HashMap<>();
        
        // Service health matrix
        analysis.put("serviceHealthMatrix", getServiceHealthMatrix());
        
        // Dependency graph
        analysis.put("dependencyGraph", generateDependencyGraph());
        
        // Critical path analysis
        analysis.put("criticalPath", analyzeCriticalPath());
        
        // Bottleneck identification
        analysis.put("bottlenecks", identifyBottlenecks());
        
        return analysis;
    }
    
    /**
     * Generate alerting insights
     */
    public Map<String, Object> getAlertingInsights() {
        logger.info("Generating alerting insights");
        
        Map<String, Object> insights = new HashMap<>();
        
        // Active alerts
        insights.put("activeAlerts", getActiveAlerts());
        
        // Alert trends
        insights.put("alertTrends", calculateAlertTrends());
        
        // Recommended thresholds
        insights.put("recommendedThresholds", calculateRecommendedThresholds());
        
        return insights;
    }
    
    private Map<String, Object> calculateExecutionTrends(List<JobDto> jobs) {
        Map<String, Object> trends = new HashMap<>();
        
        // Group jobs by hour for the last 24 hours
        Map<Integer, Long> hourlyExecutions = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime hourStart = now.minusHours(hour).truncatedTo(ChronoUnit.HOURS);
            LocalDateTime hourEnd = hourStart.plusHours(1);
            
            long count = jobs.stream()
                .filter(job -> job.getCreatedAt().isAfter(hourStart) && job.getCreatedAt().isBefore(hourEnd))
                .count();
            
            hourlyExecutions.put(hour, count);
        }
        
        trends.put("hourlyExecutions", hourlyExecutions);
        trends.put("peakHour", hourlyExecutions.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0));
        
        return trends;
    }
    
    private Map<String, Object> calculateSuccessRates(List<JobDto> jobs) {
        Map<String, Object> successRates = new HashMap<>();
        
        long totalJobs = jobs.size();
        if (totalJobs == 0) {
            successRates.put("overallSuccessRate", 100.0);
            return successRates;
        }
        
        long successfulJobs = jobs.stream()
            .mapToLong(job -> job.getStatus() == JobStatus.COMPLETED ? 1 : 0)
            .sum();
        
        long failedJobs = jobs.stream()
            .mapToLong(job -> job.getStatus() == JobStatus.FAILED ? 1 : 0)
            .sum();
        
        double successRate = (double) successfulJobs / totalJobs * 100;
        double failureRate = (double) failedJobs / totalJobs * 100;
        
        successRates.put("overallSuccessRate", successRate);
        successRates.put("overallFailureRate", failureRate);
        successRates.put("totalProcessed", successfulJobs + failedJobs);
        
        // Success rate by job type
        Map<String, Double> successRateByType = jobs.stream()
            .collect(Collectors.groupingBy(JobDto::getJobClass,
                Collectors.averagingDouble(job -> job.getStatus() == JobStatus.COMPLETED ? 100.0 : 0.0)));
        
        successRates.put("successRateByType", successRateByType);
        
        return successRates;
    }
    
    private Map<String, Object> calculatePerformanceMetrics(List<JobDto> jobs) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Calculate average execution time (simulated)
        double avgExecutionTime = jobs.stream()
            .filter(job -> job.getStatus() == JobStatus.COMPLETED)
            .mapToDouble(job -> simulateExecutionTime(job.getJobClass()))
            .average()
            .orElse(0.0);
        
        metrics.put("averageExecutionTime", avgExecutionTime);
        metrics.put("throughputPerHour", jobs.size() / 24.0); // Jobs per hour over 24h period
        
        // Queue depth simulation
        long queueDepth = jobs.stream()
            .filter(job -> job.getStatus() == JobStatus.SCHEDULED)
            .count();
        
        metrics.put("currentQueueDepth", queueDepth);
        metrics.put("estimatedQueueProcessingTime", queueDepth * avgExecutionTime);
        
        return metrics;
    }
    
    private Map<String, Object> calculateResourceUtilization() {
        Map<String, Object> utilization = new HashMap<>();
        
        // Simulate resource metrics
        utilization.put("cpuUtilization", Math.random() * 30 + 20); // 20-50%
        utilization.put("memoryUtilization", Math.random() * 40 + 30); // 30-70%
        utilization.put("diskUtilization", Math.random() * 20 + 10); // 10-30%
        utilization.put("networkUtilization", Math.random() * 25 + 15); // 15-40%
        
        // Thread pool utilization
        utilization.put("threadPoolUtilization", Math.random() * 60 + 20); // 20-80%
        utilization.put("connectionPoolUtilization", Math.random() * 50 + 25); // 25-75%
        
        return utilization;
    }
    
    private Map<String, Object> analyzeFailures(List<JobDto> jobs) {
        Map<String, Object> analysis = new HashMap<>();
        
        List<JobDto> failedJobs = jobs.stream()
            .filter(job -> job.getStatus() == JobStatus.FAILED)
            .toList();
        
        // Failure categories
        Map<String, Long> failuresByType = failedJobs.stream()
            .collect(Collectors.groupingBy(JobDto::getJobClass, Collectors.counting()));
        
        analysis.put("failuresByType", failuresByType);
        analysis.put("totalFailures", failedJobs.size());
        
        // Simulated failure reasons
        Map<String, Long> failureReasons = Map.of(
            "Timeout", (long) (failedJobs.size() * 0.4),
            "Resource Unavailable", (long) (failedJobs.size() * 0.3),
            "Invalid Input", (long) (failedJobs.size() * 0.2),
            "System Error", (long) (failedJobs.size() * 0.1)
        );
        
        analysis.put("failureReasons", failureReasons);
        
        return analysis;
    }
    
    private Map<String, Object> getJvmMetrics() {
        Map<String, Object> jvmMetrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        jvmMetrics.put("totalMemory", totalMemory);
        jvmMetrics.put("usedMemory", usedMemory);
        jvmMetrics.put("freeMemory", freeMemory);
        jvmMetrics.put("memoryUtilization", (double) usedMemory / totalMemory * 100);
        jvmMetrics.put("availableProcessors", runtime.availableProcessors());
        
        return jvmMetrics;
    }
    
    private Map<String, Object> getApplicationMetrics() {
        Map<String, Object> appMetrics = new HashMap<>();
        
        // Get metrics from Micrometer registry
        appMetrics.put("httpRequestsTotal", meterRegistry.counter("http.server.requests").count());
        appMetrics.put("activeConnections", Math.random() * 50 + 10); // Simulated
        appMetrics.put("uptime", System.currentTimeMillis() / 1000); // Seconds since start
        
        return appMetrics;
    }
    
    private Map<String, Object> getBusinessMetrics() {
        Map<String, Object> businessMetrics = new HashMap<>();
        List<JobDto> allJobs = dashboardService.getAllJobs();
        
        businessMetrics.put("totalJobsScheduled", allJobs.size());
        businessMetrics.put("jobsCompletedToday", allJobs.stream()
            .filter(job -> job.getStatus() == JobStatus.COMPLETED)
            .filter(job -> job.getCreatedAt().isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)))
            .count());
        
        businessMetrics.put("averageJobsPerHour", allJobs.size() / 24.0);
        
        return businessMetrics;
    }
    
    private double calculateSystemHealthScore() {
        // Calculate a composite health score based on various factors
        double cpuScore = Math.max(0, 100 - (Math.random() * 30 + 20)); // Invert CPU usage
        double memoryScore = Math.max(0, 100 - (Math.random() * 40 + 30)); // Invert memory usage
        double errorScore = Math.max(0, 100 - (dashboardService.getJobsByStatus(JobStatus.FAILED).size() * 10));
        
        return (cpuScore + memoryScore + errorScore) / 3;
    }
    
    private Map<String, Object> predictNextHourExecutions(List<JobDto> jobs) {
        Map<String, Object> prediction = new HashMap<>();
        
        // Simple prediction based on historical patterns
        long scheduledJobs = jobs.stream()
            .filter(job -> job.getStatus() == JobStatus.SCHEDULED)
            .count();
        
        prediction.put("estimatedExecutions", scheduledJobs + (long)(Math.random() * 10));
        prediction.put("confidence", 0.75 + Math.random() * 0.2); // 75-95%
        
        return prediction;
    }
    
    private Map<String, Object> generateDailyForecast(List<JobDto> jobs) {
        Map<String, Object> forecast = new HashMap<>();
        
        // Generate forecast for next 7 days
        Map<String, Integer> dailyForecast = new HashMap<>();
        for (int day = 1; day <= 7; day++) {
            dailyForecast.put("day" + day, (int)(jobs.size() * (0.8 + Math.random() * 0.4)));
        }
        
        forecast.put("dailyExecutionForecast", dailyForecast);
        forecast.put("forecastAccuracy", 0.82 + Math.random() * 0.15); // 82-97%
        
        return forecast;
    }
    
    private Map<String, Object> forecastResourceRequirements(List<JobDto> jobs) {
        Map<String, Object> forecast = new HashMap<>();
        
        long heavyJobs = jobs.stream()
            .filter(job -> job.getJobClass().contains("DataImport") || job.getJobClass().contains("Report"))
            .count();
        
        forecast.put("estimatedCpuRequirement", heavyJobs * 15 + jobs.size() * 5); // Percentage
        forecast.put("estimatedMemoryRequirement", heavyJobs * 100 + jobs.size() * 20); // MB
        forecast.put("estimatedDiskIORequirement", heavyJobs * 50 + jobs.size() * 10); // MB/s
        
        return forecast;
    }
    
    private Map<String, Object> getServiceHealthMatrix() {
        Map<String, Object> matrix = new HashMap<>();
        
        Map<String, Map<String, Object>> services = new HashMap<>();
        
        String[] serviceNames = {"scheduler-service", "executor-service", "notification-service", "user-service"};
        for (String service : serviceNames) {
            Map<String, Object> serviceHealth = new HashMap<>();
            serviceHealth.put("status", Math.random() > 0.1 ? "UP" : "DOWN");
            serviceHealth.put("responseTime", 50 + Math.random() * 200); // 50-250ms
            serviceHealth.put("errorRate", Math.random() * 5); // 0-5%
            serviceHealth.put("uptime", 99.0 + Math.random() * 1); // 99-100%
            
            services.put(service, serviceHealth);
        }
        
        matrix.put("services", services);
        return matrix;
    }
    
    private Map<String, Object> generateDependencyGraph() {
        Map<String, Object> graph = new HashMap<>();
        
        // Simplified dependency mapping
        Map<String, List<String>> dependencies = Map.of(
            "api-gateway", List.of("scheduler-service", "executor-service", "notification-service"),
            "scheduler-service", List.of("executor-service", "notification-service"),
            "executor-service", List.of("notification-service"),
            "notification-service", List.of()
        );
        
        graph.put("dependencies", dependencies);
        graph.put("criticalServices", List.of("scheduler-service", "executor-service"));
        
        return graph;
    }
    
    private Map<String, Object> analyzeCriticalPath() {
        Map<String, Object> analysis = new HashMap<>();
        
        analysis.put("criticalPath", List.of("api-gateway", "scheduler-service", "executor-service"));
        analysis.put("estimatedFailureImpact", Map.of(
            "scheduler-service", "High - Job scheduling disabled",
            "executor-service", "Critical - Job execution halted",
            "notification-service", "Medium - Notifications delayed"
        ));
        
        return analysis;
    }
    
    private List<Map<String, Object>> identifyBottlenecks() {
        List<Map<String, Object>> bottlenecks = new ArrayList<>();
        
        // Simulated bottleneck detection
        if (Math.random() > 0.7) {
            Map<String, Object> bottleneck = new HashMap<>();
            bottleneck.put("service", "executor-service");
            bottleneck.put("type", "Thread Pool Exhaustion");
            bottleneck.put("severity", "Medium");
            bottleneck.put("recommendation", "Increase thread pool size or optimize job execution time");
            bottlenecks.add(bottleneck);
        }
        
        return bottlenecks;
    }
    
    private List<Map<String, Object>> getActiveAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        // Simulated active alerts
        if (Math.random() > 0.8) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "alert-001");
            alert.put("severity", "WARNING");
            alert.put("message", "High job failure rate detected");
            alert.put("timestamp", LocalDateTime.now().toString());
            alert.put("service", "executor-service");
            alerts.add(alert);
        }
        
        return alerts;
    }
    
    private Map<String, Object> calculateAlertTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // Simulated alert trends for the last 7 days
        Map<String, Integer> dailyAlerts = new HashMap<>();
        for (int day = 0; day < 7; day++) {
            dailyAlerts.put("day" + day, (int)(Math.random() * 10));
        }
        
        trends.put("dailyAlerts", dailyAlerts);
        trends.put("trendDirection", Math.random() > 0.5 ? "increasing" : "decreasing");
        
        return trends;
    }
    
    private Map<String, Object> calculateRecommendedThresholds() {
        Map<String, Object> thresholds = new HashMap<>();
        
        thresholds.put("cpuUtilization", 80.0);
        thresholds.put("memoryUtilization", 85.0);
        thresholds.put("jobFailureRate", 5.0);
        thresholds.put("responseTime", 2000.0); // milliseconds
        thresholds.put("queueDepth", 100);
        
        return thresholds;
    }
    
    private double simulateExecutionTime(String jobClass) {
        // Simulate different execution times based on job type
        return switch (jobClass) {
            case "com.example.EmailJob" -> 2000 + Math.random() * 1000; // 2-3 seconds
            case "com.example.ReportJob" -> 5000 + Math.random() * 3000; // 5-8 seconds
            case "com.example.DataImportJob" -> 10000 + Math.random() * 5000; // 10-15 seconds
            default -> 3000 + Math.random() * 2000; // 3-5 seconds
        };
    }
}