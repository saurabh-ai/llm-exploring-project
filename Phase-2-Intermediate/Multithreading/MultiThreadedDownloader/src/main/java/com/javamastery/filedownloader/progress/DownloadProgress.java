package com.javamastery.filedownloader.progress;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe immutable progress data model for individual downloads.
 * Uses atomic references for thread-safe updates.
 */
public class DownloadProgress {
    
    public enum Status {
        PENDING,
        DOWNLOADING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    private final String downloadId;
    private final String fileName;
    private final String url;
    private final AtomicLong totalBytes;
    private final AtomicLong downloadedBytes;
    private final AtomicLong downloadSpeed; // bytes per second
    private final AtomicReference<Status> status;
    private final AtomicReference<LocalDateTime> startTime;
    private final AtomicReference<LocalDateTime> endTime;
    private final AtomicReference<String> errorMessage;
    
    public DownloadProgress(String downloadId, String fileName, String url, long totalBytes) {
        this.downloadId = downloadId;
        this.fileName = fileName;
        this.url = url;
        this.totalBytes = new AtomicLong(totalBytes);
        this.downloadedBytes = new AtomicLong(0);
        this.downloadSpeed = new AtomicLong(0);
        this.status = new AtomicReference<>(Status.PENDING);
        this.startTime = new AtomicReference<>();
        this.endTime = new AtomicReference<>();
        this.errorMessage = new AtomicReference<>();
    }
    
    // Getters
    public String getDownloadId() { return downloadId; }
    public String getFileName() { return fileName; }
    public String getUrl() { return url; }
    public long getTotalBytes() { return totalBytes.get(); }
    public long getDownloadedBytes() { return downloadedBytes.get(); }
    public long getDownloadSpeed() { return downloadSpeed.get(); }
    public Status getStatus() { return status.get(); }
    public LocalDateTime getStartTime() { return startTime.get(); }
    public LocalDateTime getEndTime() { return endTime.get(); }
    public String getErrorMessage() { return errorMessage.get(); }
    
    // Progress calculation
    public double getProgressPercent() {
        long total = getTotalBytes();
        if (total <= 0) return 0.0;
        return (getDownloadedBytes() * 100.0) / total;
    }
    
    public long getRemainingBytes() {
        return Math.max(0, getTotalBytes() - getDownloadedBytes());
    }
    
    public long getEstimatedTimeRemaining() {
        long speed = getDownloadSpeed();
        if (speed <= 0) return -1; // Unknown
        return getRemainingBytes() / speed;
    }
    
    public boolean isCompleted() {
        return status.get() == Status.COMPLETED;
    }
    
    public boolean isFailed() {
        return status.get() == Status.FAILED;
    }
    
    public boolean isActive() {
        Status currentStatus = status.get();
        return currentStatus == Status.DOWNLOADING || currentStatus == Status.PENDING;
    }
    
    // Thread-safe setters
    public void updateProgress(long downloadedBytes) {
        this.downloadedBytes.set(Math.max(0, downloadedBytes));
    }
    
    public void addProgress(long additionalBytes) {
        this.downloadedBytes.addAndGet(additionalBytes);
    }
    
    public void updateSpeed(long bytesPerSecond) {
        this.downloadSpeed.set(Math.max(0, bytesPerSecond));
    }
    
    public void updateTotalBytes(long totalBytes) {
        this.totalBytes.set(Math.max(0, totalBytes));
    }
    
    public void setStatus(Status newStatus) {
        Status oldStatus = this.status.getAndSet(newStatus);
        
        // Set timestamps based on status transitions
        if (newStatus == Status.DOWNLOADING && startTime.get() == null) {
            startTime.set(LocalDateTime.now());
        } else if ((newStatus == Status.COMPLETED || newStatus == Status.FAILED || 
                   newStatus == Status.CANCELLED) && endTime.get() == null) {
            endTime.set(LocalDateTime.now());
        }
    }
    
    public void setError(String errorMessage) {
        this.errorMessage.set(errorMessage);
        setStatus(Status.FAILED);
    }
    
    @Override
    public String toString() {
        return String.format("DownloadProgress{id='%s', file='%s', progress=%.2f%%, status=%s, speed=%d B/s}",
                           downloadId, fileName, getProgressPercent(), getStatus(), getDownloadSpeed());
    }
}