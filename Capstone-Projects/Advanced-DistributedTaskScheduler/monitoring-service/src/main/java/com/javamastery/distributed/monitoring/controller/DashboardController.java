package com.javamastery.distributed.monitoring.controller;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.enums.JobStatus;
import com.javamastery.distributed.monitoring.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dashboard Controller for monitoring interface
 * Demonstrates Observer Pattern for monitoring job status changes
 */
@Controller
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Monitoring dashboard interface")
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    private final DashboardService dashboardService;
    
    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    /**
     * Main dashboard view
     */
    @GetMapping
    public String dashboard(Model model) {
        logger.debug("Loading dashboard view");
        
        try {
            // Get job statistics
            Map<String, Long> jobStats = dashboardService.getJobStatistics();
            model.addAttribute("jobStats", jobStats);
            
            // Get recent jobs
            List<JobDto> recentJobs = dashboardService.getRecentJobs(10);
            model.addAttribute("recentJobs", recentJobs);
            
            // Get system health
            boolean systemHealthy = dashboardService.isSystemHealthy();
            model.addAttribute("systemHealthy", systemHealthy);
            
        } catch (Exception e) {
            logger.error("Error loading dashboard data: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to load dashboard data");
        }
        
        return "dashboard";
    }
    
    /**
     * Jobs page
     */
    @GetMapping("/jobs")
    public String jobs(Model model, 
                      @RequestParam(required = false) JobStatus status) {
        logger.debug("Loading jobs view with status filter: {}", status);
        
        try {
            List<JobDto> jobs;
            if (status != null) {
                jobs = dashboardService.getJobsByStatus(status);
            } else {
                jobs = dashboardService.getAllJobs();
            }
            model.addAttribute("jobs", jobs);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("statuses", JobStatus.values());
            
        } catch (Exception e) {
            logger.error("Error loading jobs: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to load jobs data");
        }
        
        return "jobs";
    }
    
    // REST API endpoints for AJAX calls
    
    /**
     * Get job statistics for real-time updates
     */
    @GetMapping("/api/stats")
    @ResponseBody
    @Operation(summary = "Get job statistics", description = "Returns current job statistics")
    public ResponseEntity<Map<String, Long>> getJobStatistics() {
        logger.debug("API request for job statistics");
        
        Map<String, Long> stats = dashboardService.getJobStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get system health status
     */
    @GetMapping("/api/health")
    @ResponseBody
    @Operation(summary = "Get system health", description = "Returns system health status")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        logger.debug("API request for system health");
        
        Map<String, Object> health = dashboardService.getSystemHealth();
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get recent job executions
     */
    @GetMapping("/api/recent-jobs")
    @ResponseBody
    @Operation(summary = "Get recent jobs", description = "Returns list of recent jobs")
    public ResponseEntity<List<JobDto>> getRecentJobs(
            @RequestParam(defaultValue = "10") int limit) {
        logger.debug("API request for recent jobs with limit: {}", limit);
        
        List<JobDto> recentJobs = dashboardService.getRecentJobs(limit);
        return ResponseEntity.ok(recentJobs);
    }
}