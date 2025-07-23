package com.javamastery.performance.memory.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Represents memory usage metrics at a specific point in time
 */
public class MemorySnapshot {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    // Heap Memory
    private long heapUsed;
    private long heapCommitted;
    private long heapMax;
    private double heapUsagePercentage;
    
    // Non-Heap Memory
    private long nonHeapUsed;
    private long nonHeapCommitted;
    private long nonHeapMax;
    
    // Garbage Collection
    private long gcCollectionCount;
    private long gcCollectionTime;
    
    // Memory Pools
    private long edenSpaceUsed;
    private long edenSpaceMax;
    private long survivorSpaceUsed;
    private long survivorSpaceMax;
    private long oldGenUsed;
    private long oldGenMax;
    private long metaspaceUsed;
    private long metaspaceMax;
    
    // Thread Information
    private int threadCount;
    private int peakThreadCount;
    
    public MemorySnapshot() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getHeapCommitted() {
        return heapCommitted;
    }

    public void setHeapCommitted(long heapCommitted) {
        this.heapCommitted = heapCommitted;
    }

    public long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(long heapMax) {
        this.heapMax = heapMax;
    }

    public double getHeapUsagePercentage() {
        return heapUsagePercentage;
    }

    public void setHeapUsagePercentage(double heapUsagePercentage) {
        this.heapUsagePercentage = heapUsagePercentage;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public long getNonHeapCommitted() {
        return nonHeapCommitted;
    }

    public void setNonHeapCommitted(long nonHeapCommitted) {
        this.nonHeapCommitted = nonHeapCommitted;
    }

    public long getNonHeapMax() {
        return nonHeapMax;
    }

    public void setNonHeapMax(long nonHeapMax) {
        this.nonHeapMax = nonHeapMax;
    }

    public long getGcCollectionCount() {
        return gcCollectionCount;
    }

    public void setGcCollectionCount(long gcCollectionCount) {
        this.gcCollectionCount = gcCollectionCount;
    }

    public long getGcCollectionTime() {
        return gcCollectionTime;
    }

    public void setGcCollectionTime(long gcCollectionTime) {
        this.gcCollectionTime = gcCollectionTime;
    }

    public long getEdenSpaceUsed() {
        return edenSpaceUsed;
    }

    public void setEdenSpaceUsed(long edenSpaceUsed) {
        this.edenSpaceUsed = edenSpaceUsed;
    }

    public long getEdenSpaceMax() {
        return edenSpaceMax;
    }

    public void setEdenSpaceMax(long edenSpaceMax) {
        this.edenSpaceMax = edenSpaceMax;
    }

    public long getSurvivorSpaceUsed() {
        return survivorSpaceUsed;
    }

    public void setSurvivorSpaceUsed(long survivorSpaceUsed) {
        this.survivorSpaceUsed = survivorSpaceUsed;
    }

    public long getSurvivorSpaceMax() {
        return survivorSpaceMax;
    }

    public void setSurvivorSpaceMax(long survivorSpaceMax) {
        this.survivorSpaceMax = survivorSpaceMax;
    }

    public long getOldGenUsed() {
        return oldGenUsed;
    }

    public void setOldGenUsed(long oldGenUsed) {
        this.oldGenUsed = oldGenUsed;
    }

    public long getOldGenMax() {
        return oldGenMax;
    }

    public void setOldGenMax(long oldGenMax) {
        this.oldGenMax = oldGenMax;
    }

    public long getMetaspaceUsed() {
        return metaspaceUsed;
    }

    public void setMetaspaceUsed(long metaspaceUsed) {
        this.metaspaceUsed = metaspaceUsed;
    }

    public long getMetaspaceMax() {
        return metaspaceMax;
    }

    public void setMetaspaceMax(long metaspaceMax) {
        this.metaspaceMax = metaspaceMax;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }
}