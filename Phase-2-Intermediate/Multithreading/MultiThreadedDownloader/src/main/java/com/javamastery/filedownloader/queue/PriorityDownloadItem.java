package com.javamastery.filedownloader.queue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a priority-based download item in the download queue.
 * Implements Comparable for priority-based ordering.
 */
public class PriorityDownloadItem implements Comparable<PriorityDownloadItem> {
    
    public enum Priority {
        LOW(1),
        NORMAL(2),
        HIGH(3),
        URGENT(4);
        
        private final int value;
        
        Priority(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    private final String id;
    private final String url;
    private final Path destinationPath;
    private final Priority priority;
    private final LocalDateTime createdAt;
    private final AtomicInteger retryCount;
    
    public PriorityDownloadItem(String id, String url, Path destinationPath, Priority priority) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        this.destinationPath = Objects.requireNonNull(destinationPath, "Destination path cannot be null");
        this.priority = Objects.requireNonNull(priority, "Priority cannot be null");
        this.createdAt = LocalDateTime.now();
        this.retryCount = new AtomicInteger(0);
    }
    
    public PriorityDownloadItem(String id, String url, Path destinationPath) {
        this(id, url, destinationPath, Priority.NORMAL);
    }
    
    // Getters
    public String getId() { return id; }
    public String getUrl() { return url; }
    public Path getDestinationPath() { return destinationPath; }
    public Priority getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getRetryCount() { return retryCount.get(); }
    
    public void incrementRetryCount() {
        retryCount.incrementAndGet();
    }
    
    public void resetRetryCount() {
        retryCount.set(0);
    }
    
    @Override
    public int compareTo(PriorityDownloadItem other) {
        // Higher priority comes first
        int priorityCompare = Integer.compare(other.priority.getValue(), this.priority.getValue());
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        
        // If same priority, earlier created items come first
        return this.createdAt.compareTo(other.createdAt);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PriorityDownloadItem that = (PriorityDownloadItem) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("PriorityDownloadItem{id='%s', url='%s', destination='%s', priority=%s, retries=%d}",
                           id, url, destinationPath, priority, getRetryCount());
    }
}