package com.javamastery.filedownloader.progress;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

/**
 * Thread-safe progress tracker for managing multiple download progress states.
 * Uses ConcurrentHashMap for thread-safe operations on multiple downloads.
 */
public class ProgressTracker {
    
    private final ConcurrentHashMap<String, DownloadProgress> progressMap;
    private final AtomicLong totalBytesAllDownloads;
    private final AtomicLong downloadedBytesAllDownloads;
    private final AtomicInteger completedDownloads;
    private final AtomicInteger failedDownloads;
    
    public ProgressTracker() {
        this.progressMap = new ConcurrentHashMap<>();
        this.totalBytesAllDownloads = new AtomicLong(0);
        this.downloadedBytesAllDownloads = new AtomicLong(0);
        this.completedDownloads = new AtomicInteger(0);
        this.failedDownloads = new AtomicInteger(0);
    }
    
    /**
     * Registers a new download for progress tracking.
     */
    public void registerDownload(String downloadId, String fileName, String url, long totalBytes) {
        DownloadProgress progress = new DownloadProgress(downloadId, fileName, url, totalBytes);
        DownloadProgress existing = progressMap.put(downloadId, progress);
        
        // If this is a new download, update totals
        if (existing == null) {
            totalBytesAllDownloads.addAndGet(totalBytes);
        } else {
            // Update totals by removing old and adding new
            totalBytesAllDownloads.addAndGet(totalBytes - existing.getTotalBytes());
            downloadedBytesAllDownloads.addAndGet(-existing.getDownloadedBytes());
            
            // Adjust counters if status changed
            if (existing.isCompleted()) {
                completedDownloads.decrementAndGet();
            } else if (existing.isFailed()) {
                failedDownloads.decrementAndGet();
            }
        }
    }
    
    /**
     * Updates progress for a specific download.
     */
    public void updateProgress(String downloadId, long downloadedBytes) {
        DownloadProgress progress = progressMap.get(downloadId);
        if (progress != null) {
            long oldDownloaded = progress.getDownloadedBytes();
            progress.updateProgress(downloadedBytes);
            
            // Update total downloaded bytes
            downloadedBytesAllDownloads.addAndGet(downloadedBytes - oldDownloaded);
        }
    }
    
    /**
     * Adds incremental progress to a download.
     */
    public void addProgress(String downloadId, long additionalBytes) {
        DownloadProgress progress = progressMap.get(downloadId);
        if (progress != null) {
            progress.addProgress(additionalBytes);
            downloadedBytesAllDownloads.addAndGet(additionalBytes);
        }
    }
    
    /**
     * Updates download speed for a specific download.
     */
    public void updateSpeed(String downloadId, long bytesPerSecond) {
        DownloadProgress progress = progressMap.get(downloadId);
        if (progress != null) {
            progress.updateSpeed(bytesPerSecond);
        }
    }
    
    /**
     * Updates the status of a download and adjusts counters.
     */
    public void updateStatus(String downloadId, DownloadProgress.Status status) {
        DownloadProgress progress = progressMap.get(downloadId);
        if (progress != null) {
            DownloadProgress.Status oldStatus = progress.getStatus();
            progress.setStatus(status);
            
            // Update counters based on status change
            updateStatusCounters(oldStatus, status);
        }
    }
    
    /**
     * Sets error status for a download.
     */
    public void setError(String downloadId, String errorMessage) {
        DownloadProgress progress = progressMap.get(downloadId);
        if (progress != null) {
            DownloadProgress.Status oldStatus = progress.getStatus();
            progress.setError(errorMessage);
            
            // Update counters
            updateStatusCounters(oldStatus, DownloadProgress.Status.FAILED);
        }
    }
    
    /**
     * Gets progress for a specific download.
     */
    public DownloadProgress getProgress(String downloadId) {
        return progressMap.get(downloadId);
    }
    
    /**
     * Gets all progress entries.
     */
    public Collection<DownloadProgress> getAllProgress() {
        return new ArrayList<>(progressMap.values());
    }
    
    /**
     * Gets progress for active downloads only.
     */
    public List<DownloadProgress> getActiveProgress() {
        return progressMap.values().stream()
            .filter(DownloadProgress::isActive)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Removes progress tracking for a download.
     */
    public void removeProgress(String downloadId) {
        DownloadProgress removed = progressMap.remove(downloadId);
        if (removed != null) {
            totalBytesAllDownloads.addAndGet(-removed.getTotalBytes());
            downloadedBytesAllDownloads.addAndGet(-removed.getDownloadedBytes());
            
            if (removed.isCompleted()) {
                completedDownloads.decrementAndGet();
            } else if (removed.isFailed()) {
                failedDownloads.decrementAndGet();
            }
        }
    }
    
    /**
     * Gets overall progress percentage across all downloads.
     */
    public double getOverallProgressPercent() {
        long total = totalBytesAllDownloads.get();
        if (total <= 0) return 0.0;
        return (downloadedBytesAllDownloads.get() * 100.0) / total;
    }
    
    /**
     * Gets total number of downloads being tracked.
     */
    public int getTotalDownloads() {
        return progressMap.size();
    }
    
    /**
     * Gets number of completed downloads.
     */
    public int getCompletedDownloads() {
        return completedDownloads.get();
    }
    
    /**
     * Gets number of failed downloads.
     */
    public int getFailedDownloads() {
        return failedDownloads.get();
    }
    
    /**
     * Gets number of active downloads.
     */
    public int getActiveDownloads() {
        return (int) progressMap.values().stream().filter(DownloadProgress::isActive).count();
    }
    
    /**
     * Gets total bytes across all downloads.
     */
    public long getTotalBytes() {
        return totalBytesAllDownloads.get();
    }
    
    /**
     * Gets total downloaded bytes across all downloads.
     */
    public long getDownloadedBytes() {
        return downloadedBytesAllDownloads.get();
    }
    
    /**
     * Gets overall download speed (sum of all active download speeds).
     */
    public long getOverallSpeed() {
        return progressMap.values().stream()
            .filter(DownloadProgress::isActive)
            .mapToLong(DownloadProgress::getDownloadSpeed)
            .sum();
    }
    
    /**
     * Clears all progress data.
     */
    public void clear() {
        progressMap.clear();
        totalBytesAllDownloads.set(0);
        downloadedBytesAllDownloads.set(0);
        completedDownloads.set(0);
        failedDownloads.set(0);
    }
    
    private void updateStatusCounters(DownloadProgress.Status oldStatus, DownloadProgress.Status newStatus) {
        // Decrement old status counter
        if (oldStatus == DownloadProgress.Status.COMPLETED) {
            completedDownloads.decrementAndGet();
        } else if (oldStatus == DownloadProgress.Status.FAILED) {
            failedDownloads.decrementAndGet();
        }
        
        // Increment new status counter
        if (newStatus == DownloadProgress.Status.COMPLETED) {
            completedDownloads.incrementAndGet();
        } else if (newStatus == DownloadProgress.Status.FAILED) {
            failedDownloads.incrementAndGet();
        }
    }
    
    @Override
    public String toString() {
        return String.format("ProgressTracker{total=%d, completed=%d, failed=%d, active=%d, overall=%.2f%%}",
                           getTotalDownloads(), getCompletedDownloads(), getFailedDownloads(), 
                           getActiveDownloads(), getOverallProgressPercent());
    }
}