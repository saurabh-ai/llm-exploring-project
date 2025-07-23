package com.javamastery.performance.memory.service;

import com.javamastery.performance.memory.model.MemorySnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MemoryMonitoringService
 */
@SpringBootTest
class MemoryMonitoringServiceTest {
    
    private MemoryMonitoringService memoryMonitoringService;
    
    @BeforeEach
    void setUp() {
        memoryMonitoringService = new MemoryMonitoringService();
    }
    
    @Test
    void testCreateSnapshot() {
        MemorySnapshot snapshot = memoryMonitoringService.createSnapshot();
        
        assertNotNull(snapshot);
        assertNotNull(snapshot.getTimestamp());
        assertTrue(snapshot.getHeapUsed() >= 0);
        assertTrue(snapshot.getHeapCommitted() >= 0);
        assertTrue(snapshot.getHeapMax() >= 0);
        assertTrue(snapshot.getHeapUsagePercentage() >= 0);
        assertTrue(snapshot.getThreadCount() > 0);
    }
    
    @Test
    void testGetRecentSnapshots() {
        // Create some snapshots
        memoryMonitoringService.createSnapshot();
        memoryMonitoringService.createSnapshot();
        memoryMonitoringService.createSnapshot();
        
        List<MemorySnapshot> snapshots = memoryMonitoringService.getRecentSnapshots(2);
        
        assertNotNull(snapshots);
        assertTrue(snapshots.size() <= 2);
    }
    
    @Test
    void testClearSnapshots() {
        // Create some snapshots
        memoryMonitoringService.createSnapshot();
        memoryMonitoringService.createSnapshot();
        
        memoryMonitoringService.clearSnapshots();
        
        List<MemorySnapshot> snapshots = memoryMonitoringService.getAllSnapshots();
        assertTrue(snapshots.isEmpty());
    }
    
    @Test
    void testMemoryLeakDetection() {
        // This test verifies that leak detection doesn't throw exceptions
        // Actual leak detection requires specific memory conditions
        boolean leakDetected = memoryMonitoringService.detectMemoryLeak();
        
        // Should not throw exception and return a boolean
        assertNotNull(leakDetected);
    }
    
    @Test
    void testForceGarbageCollection() {
        // Test that force GC doesn't throw exceptions
        assertDoesNotThrow(() -> memoryMonitoringService.forceGarbageCollection());
    }
}