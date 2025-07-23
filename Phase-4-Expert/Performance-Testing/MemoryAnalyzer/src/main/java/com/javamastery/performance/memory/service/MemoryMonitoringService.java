package com.javamastery.performance.memory.service;

import com.javamastery.performance.memory.model.MemorySnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service for monitoring and analyzing JVM memory usage
 */
@Service
public class MemoryMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemoryMonitoringService.class);
    
    private final MemoryMXBean memoryBean;
    private final List<GarbageCollectorMXBean> gcBeans;
    private final ThreadMXBean threadBean;
    private final List<MemoryPoolMXBean> memoryPoolBeans;
    
    private final ConcurrentLinkedQueue<MemorySnapshot> snapshots;
    private static final int MAX_SNAPSHOTS = 1000;
    
    public MemoryMonitoringService() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.memoryPoolBeans = ManagementFactory.getMemoryPoolMXBeans();
        this.snapshots = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Creates a real-time memory snapshot
     */
    public MemorySnapshot createSnapshot() {
        MemorySnapshot snapshot = new MemorySnapshot();
        
        // Heap memory information
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        snapshot.setHeapUsed(heapUsage.getUsed());
        snapshot.setHeapCommitted(heapUsage.getCommitted());
        snapshot.setHeapMax(heapUsage.getMax());
        snapshot.setHeapUsagePercentage(calculateUsagePercentage(heapUsage.getUsed(), heapUsage.getMax()));
        
        // Non-heap memory information
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        snapshot.setNonHeapUsed(nonHeapUsage.getUsed());
        snapshot.setNonHeapCommitted(nonHeapUsage.getCommitted());
        snapshot.setNonHeapMax(nonHeapUsage.getMax());
        
        // Garbage collection information
        long totalGcCount = 0;
        long totalGcTime = 0;
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            totalGcCount += gcBean.getCollectionCount();
            totalGcTime += gcBean.getCollectionTime();
        }
        snapshot.setGcCollectionCount(totalGcCount);
        snapshot.setGcCollectionTime(totalGcTime);
        
        // Memory pool information
        populateMemoryPoolInfo(snapshot);
        
        // Thread information
        snapshot.setThreadCount(threadBean.getThreadCount());
        snapshot.setPeakThreadCount(threadBean.getPeakThreadCount());
        
        // Store snapshot
        addSnapshot(snapshot);
        
        logger.debug("Memory snapshot created: Heap Usage {}%", snapshot.getHeapUsagePercentage());
        
        return snapshot;
    }
    
    /**
     * Scheduled method to automatically collect memory snapshots
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void collectMemorySnapshot() {
        createSnapshot();
    }
    
    /**
     * Get recent memory snapshots
     */
    public List<MemorySnapshot> getRecentSnapshots(int count) {
        return snapshots.stream()
                .skip(Math.max(0, snapshots.size() - count))
                .toList();
    }
    
    /**
     * Get all stored snapshots
     */
    public List<MemorySnapshot> getAllSnapshots() {
        return List.copyOf(snapshots);
    }
    
    /**
     * Clear all stored snapshots
     */
    public void clearSnapshots() {
        snapshots.clear();
        logger.info("All memory snapshots cleared");
    }
    
    /**
     * Detect potential memory leaks based on heap usage trends
     */
    public boolean detectMemoryLeak() {
        if (snapshots.size() < 10) {
            return false;
        }
        
        List<MemorySnapshot> recent = getRecentSnapshots(10);
        double avgHeapUsage = recent.stream()
                .mapToDouble(MemorySnapshot::getHeapUsagePercentage)
                .average()
                .orElse(0.0);
        
        // Simple leak detection: if average heap usage > 80% over last 10 snapshots
        boolean potentialLeak = avgHeapUsage > 80.0;
        
        if (potentialLeak) {
            logger.warn("Potential memory leak detected! Average heap usage: {}%", avgHeapUsage);
        }
        
        return potentialLeak;
    }
    
    /**
     * Force garbage collection (for testing purposes)
     */
    public void forceGarbageCollection() {
        logger.info("Forcing garbage collection...");
        System.gc();
        System.runFinalization();
    }
    
    private void populateMemoryPoolInfo(MemorySnapshot snapshot) {
        for (MemoryPoolMXBean poolBean : memoryPoolBeans) {
            String poolName = poolBean.getName().toLowerCase();
            MemoryUsage usage = poolBean.getUsage();
            
            if (usage != null) {
                if (poolName.contains("eden")) {
                    snapshot.setEdenSpaceUsed(usage.getUsed());
                    snapshot.setEdenSpaceMax(usage.getMax());
                } else if (poolName.contains("survivor")) {
                    snapshot.setSurvivorSpaceUsed(usage.getUsed());
                    snapshot.setSurvivorSpaceMax(usage.getMax());
                } else if (poolName.contains("old") || poolName.contains("tenured")) {
                    snapshot.setOldGenUsed(usage.getUsed());
                    snapshot.setOldGenMax(usage.getMax());
                } else if (poolName.contains("metaspace")) {
                    snapshot.setMetaspaceUsed(usage.getUsed());
                    snapshot.setMetaspaceMax(usage.getMax());
                }
            }
        }
    }
    
    private double calculateUsagePercentage(long used, long max) {
        if (max <= 0) return 0.0;
        return (double) used / max * 100.0;
    }
    
    private void addSnapshot(MemorySnapshot snapshot) {
        snapshots.offer(snapshot);
        
        // Maintain maximum size
        while (snapshots.size() > MAX_SNAPSHOTS) {
            snapshots.poll();
        }
    }
}