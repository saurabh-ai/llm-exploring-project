package com.javamastery.performance.memory.controller;

import com.javamastery.performance.memory.model.MemorySnapshot;
import com.javamastery.performance.memory.service.MemoryMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for memory analysis and monitoring operations
 */
@RestController
@RequestMapping("/api/memory")
@Tag(name = "Memory Analysis", description = "JVM Memory Usage Analysis and Monitoring")
public class MemoryAnalysisController {
    
    private final MemoryMonitoringService memoryMonitoringService;
    
    @Autowired
    public MemoryAnalysisController(MemoryMonitoringService memoryMonitoringService) {
        this.memoryMonitoringService = memoryMonitoringService;
    }
    
    @GetMapping("/current")
    @Operation(summary = "Get current memory snapshot", 
               description = "Returns real-time JVM memory usage information")
    public ResponseEntity<MemorySnapshot> getCurrentMemorySnapshot() {
        MemorySnapshot snapshot = memoryMonitoringService.createSnapshot();
        return ResponseEntity.ok(snapshot);
    }
    
    @GetMapping("/snapshots")
    @Operation(summary = "Get memory snapshots", 
               description = "Returns historical memory usage snapshots")
    public ResponseEntity<List<MemorySnapshot>> getMemorySnapshots(
            @Parameter(description = "Number of recent snapshots to return")
            @RequestParam(value = "count", defaultValue = "50") int count) {
        
        List<MemorySnapshot> snapshots = memoryMonitoringService.getRecentSnapshots(count);
        return ResponseEntity.ok(snapshots);
    }
    
    @GetMapping("/snapshots/all")
    @Operation(summary = "Get all memory snapshots", 
               description = "Returns all stored memory snapshots")
    public ResponseEntity<List<MemorySnapshot>> getAllMemorySnapshots() {
        List<MemorySnapshot> snapshots = memoryMonitoringService.getAllSnapshots();
        return ResponseEntity.ok(snapshots);
    }
    
    @DeleteMapping("/snapshots")
    @Operation(summary = "Clear memory snapshots", 
               description = "Clears all stored memory snapshots")
    public ResponseEntity<Map<String, String>> clearMemorySnapshots() {
        memoryMonitoringService.clearSnapshots();
        return ResponseEntity.ok(Map.of("message", "All memory snapshots cleared successfully"));
    }
    
    @PostMapping("/gc")
    @Operation(summary = "Force garbage collection", 
               description = "Triggers garbage collection for testing purposes")
    public ResponseEntity<Map<String, String>> forceGarbageCollection() {
        memoryMonitoringService.forceGarbageCollection();
        return ResponseEntity.ok(Map.of("message", "Garbage collection triggered"));
    }
    
    @GetMapping("/leak-detection")
    @Operation(summary = "Check for memory leaks", 
               description = "Analyzes recent memory usage patterns to detect potential memory leaks")
    public ResponseEntity<Map<String, Object>> checkMemoryLeak() {
        boolean leakDetected = memoryMonitoringService.detectMemoryLeak();
        return ResponseEntity.ok(Map.of(
            "memoryLeakDetected", leakDetected,
            "message", leakDetected ? 
                "Potential memory leak detected based on heap usage trends" : 
                "No memory leak detected"
        ));
    }
    
    @GetMapping("/health")
    @Operation(summary = "Memory health check", 
               description = "Returns memory health status and recommendations")
    public ResponseEntity<Map<String, Object>> getMemoryHealth() {
        MemorySnapshot current = memoryMonitoringService.createSnapshot();
        
        String status;
        String recommendation;
        
        if (current.getHeapUsagePercentage() > 90) {
            status = "CRITICAL";
            recommendation = "Immediate action required: heap usage critical. Consider increasing heap size or optimizing memory usage.";
        } else if (current.getHeapUsagePercentage() > 75) {
            status = "WARNING";
            recommendation = "Monitor closely: heap usage high. Consider garbage collection tuning or memory optimization.";
        } else if (current.getHeapUsagePercentage() > 50) {
            status = "CAUTION";
            recommendation = "Normal usage but monitor trends. Consider memory profiling if usage increases.";
        } else {
            status = "HEALTHY";
            recommendation = "Memory usage is optimal.";
        }
        
        return ResponseEntity.ok(Map.of(
            "status", status,
            "heapUsagePercentage", current.getHeapUsagePercentage(),
            "recommendation", recommendation,
            "threadCount", current.getThreadCount(),
            "gcCollectionCount", current.getGcCollectionCount()
        ));
    }
}