package com.javamastery.filedownloader.config;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe configuration class for thread pool settings.
 */
public class ThreadConfig {
    private final AtomicInteger corePoolSize;
    private final AtomicInteger maximumPoolSize;
    private final AtomicLong keepAliveTime; // milliseconds
    private final AtomicInteger maxConcurrentDownloads;
    
    public ThreadConfig() {
        this.corePoolSize = new AtomicInteger(4);
        this.maximumPoolSize = new AtomicInteger(8);
        this.keepAliveTime = new AtomicLong(60000); // 1 minute
        this.maxConcurrentDownloads = new AtomicInteger(10);
    }
    
    public ThreadConfig(int corePoolSize, int maximumPoolSize, long keepAliveTime, int maxConcurrentDownloads) {
        this.corePoolSize = new AtomicInteger(corePoolSize);
        this.maximumPoolSize = new AtomicInteger(maximumPoolSize);
        this.keepAliveTime = new AtomicLong(keepAliveTime);
        this.maxConcurrentDownloads = new AtomicInteger(maxConcurrentDownloads);
    }
    
    // Thread-safe getters and setters
    public int getCorePoolSize() {
        return corePoolSize.get();
    }
    
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize <= 0) {
            throw new IllegalArgumentException("Core pool size must be positive");
        }
        this.corePoolSize.set(corePoolSize);
    }
    
    public int getMaximumPoolSize() {
        return maximumPoolSize.get();
    }
    
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0) {
            throw new IllegalArgumentException("Maximum pool size must be positive");
        }
        this.maximumPoolSize.set(maximumPoolSize);
    }
    
    public long getKeepAliveTime() {
        return keepAliveTime.get();
    }
    
    public void setKeepAliveTime(long keepAliveTime) {
        if (keepAliveTime < 0) {
            throw new IllegalArgumentException("Keep alive time cannot be negative");
        }
        this.keepAliveTime.set(keepAliveTime);
    }
    
    public int getMaxConcurrentDownloads() {
        return maxConcurrentDownloads.get();
    }
    
    public void setMaxConcurrentDownloads(int maxConcurrentDownloads) {
        if (maxConcurrentDownloads <= 0) {
            throw new IllegalArgumentException("Max concurrent downloads must be positive");
        }
        this.maxConcurrentDownloads.set(maxConcurrentDownloads);
    }
    
    @Override
    public String toString() {
        return String.format("ThreadConfig{corePoolSize=%d, maximumPoolSize=%d, keepAliveTime=%d ms, maxConcurrentDownloads=%d}",
                           getCorePoolSize(), getMaximumPoolSize(), getKeepAliveTime(), getMaxConcurrentDownloads());
    }
}