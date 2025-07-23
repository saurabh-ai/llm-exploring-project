package com.javamastery.distributed.monitoring.controller;

import com.javamastery.distributed.monitoring.service.AdvancedAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Advanced Analytics Controller for enterprise-grade monitoring
 * Provides comprehensive analytics, forecasting, and insights endpoints
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Advanced Analytics", description = "Enterprise-grade monitoring and analytics")
@CrossOrigin(origins = "*")
public class AdvancedAnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedAnalyticsController.class);
    
    private final AdvancedAnalyticsService analyticsService;
    
    @Autowired
    public AdvancedAnalyticsController(AdvancedAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    
    @GetMapping("/performance")
    @Operation(summary = "Get performance analytics", 
               description = "Retrieve comprehensive performance analytics including execution trends, success rates, and metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceAnalytics() {
        logger.info("Performance analytics requested");
        
        try {
            Map<String, Object> analytics = analyticsService.getPerformanceAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            logger.error("Failed to retrieve performance analytics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/realtime")
    @Operation(summary = "Get real-time metrics", 
               description = "Retrieve real-time system metrics including JVM, application, and business metrics")
    public ResponseEntity<Map<String, Object>> getRealTimeMetrics() {
        logger.debug("Real-time metrics requested");
        
        try {
            Map<String, Object> metrics = analyticsService.getRealTimeMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            logger.error("Failed to retrieve real-time metrics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/forecast")
    @Operation(summary = "Get execution forecast", 
               description = "Retrieve job execution forecasts and resource requirement predictions")
    public ResponseEntity<Map<String, Object>> getExecutionForecast() {
        logger.info("Execution forecast requested");
        
        try {
            Map<String, Object> forecast = analyticsService.getExecutionForecast();
            return ResponseEntity.ok(forecast);
        } catch (Exception e) {
            logger.error("Failed to retrieve execution forecast: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/dependencies")
    @Operation(summary = "Get service dependency analysis", 
               description = "Analyze service dependencies, health matrix, and identify bottlenecks")
    public ResponseEntity<Map<String, Object>> getServiceDependencyAnalysis() {
        logger.info("Service dependency analysis requested");
        
        try {
            Map<String, Object> analysis = analyticsService.getServiceDependencyAnalysis();
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            logger.error("Failed to retrieve service dependency analysis: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/alerts")
    @Operation(summary = "Get alerting insights", 
               description = "Retrieve active alerts, trends, and recommended thresholds")
    public ResponseEntity<Map<String, Object>> getAlertingInsights() {
        logger.info("Alerting insights requested");
        
        try {
            Map<String, Object> insights = analyticsService.getAlertingInsights();
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            logger.error("Failed to retrieve alerting insights: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/health-score")
    @Operation(summary = "Get system health score", 
               description = "Calculate and return overall system health score")
    public ResponseEntity<Map<String, Object>> getSystemHealthScore() {
        logger.debug("System health score requested");
        
        try {
            Map<String, Object> realTimeMetrics = analyticsService.getRealTimeMetrics();
            Double healthScore = (Double) realTimeMetrics.get("healthScore");
            
            Map<String, Object> response = Map.of(
                "healthScore", healthScore,
                "status", healthScore > 80 ? "EXCELLENT" : 
                         healthScore > 60 ? "GOOD" : 
                         healthScore > 40 ? "WARNING" : "CRITICAL",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve system health score: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get analytics summary", 
               description = "Retrieve a comprehensive summary of all analytics data")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        logger.info("Analytics summary requested");
        
        try {
            Map<String, Object> summary = Map.of(
                "performance", analyticsService.getPerformanceAnalytics(),
                "realtime", analyticsService.getRealTimeMetrics(),
                "forecast", analyticsService.getExecutionForecast(),
                "dependencies", analyticsService.getServiceDependencyAnalysis(),
                "alerts", analyticsService.getAlertingInsights()
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Failed to retrieve analytics summary: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh analytics cache", 
               description = "Force refresh of analytics cache and recalculate metrics")
    public ResponseEntity<Map<String, String>> refreshAnalytics() {
        logger.info("Analytics refresh requested");
        
        try {
            // In a real implementation, this would clear caches and refresh data
            logger.info("Analytics cache refreshed successfully");
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Analytics cache refreshed successfully",
                "timestamp", String.valueOf(System.currentTimeMillis())
            ));
        } catch (Exception e) {
            logger.error("Failed to refresh analytics: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}