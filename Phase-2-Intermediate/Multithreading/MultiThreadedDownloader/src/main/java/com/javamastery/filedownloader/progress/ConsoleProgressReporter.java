package com.javamastery.filedownloader.progress;

import com.javamastery.filedownloader.util.FileUtils;
import java.util.Collection;

/**
 * Console-based progress reporter for displaying download progress.
 */
public class ConsoleProgressReporter implements ProgressReporter {
    
    private final boolean showIndividualProgress;
    private final boolean showOverallProgress;
    
    public ConsoleProgressReporter() {
        this(true, true);
    }
    
    public ConsoleProgressReporter(boolean showIndividualProgress, boolean showOverallProgress) {
        this.showIndividualProgress = showIndividualProgress;
        this.showOverallProgress = showOverallProgress;
    }
    
    @Override
    public void onProgressUpdate(ProgressTracker tracker) {
        if (showOverallProgress) {
            displayOverallProgress(tracker);
        }
        
        if (showIndividualProgress) {
            displayIndividualProgress(tracker);
        }
        
        System.out.println(); // Add spacing
    }
    
    private void displayOverallProgress(ProgressTracker tracker) {
        double overallPercent = tracker.getOverallProgressPercent();
        int totalDownloads = tracker.getTotalDownloads();
        int completed = tracker.getCompletedDownloads();
        int failed = tracker.getFailedDownloads();
        int active = tracker.getActiveDownloads();
        
        String overallBar = createProgressBar(overallPercent, 50);
        long overallSpeed = tracker.getOverallSpeed();
        String speedStr = overallSpeed > 0 ? FileUtils.formatFileSize(overallSpeed) + "/s" : "N/A";
        
        System.out.printf("Overall Progress: %s %.2f%% | Total: %d | Active: %d | Completed: %d | Failed: %d | Speed: %s%n",
                         overallBar, overallPercent, totalDownloads, active, completed, failed, speedStr);
    }
    
    private void displayIndividualProgress(ProgressTracker tracker) {
        Collection<DownloadProgress> activeProgress = tracker.getActiveProgress();
        
        if (activeProgress.isEmpty()) {
            return;
        }
        
        System.out.println("Active Downloads:");
        for (DownloadProgress progress : activeProgress) {
            displayDownloadProgress(progress);
        }
    }
    
    private void displayDownloadProgress(DownloadProgress progress) {
        double percent = progress.getProgressPercent();
        String fileName = progress.getFileName();
        long speed = progress.getDownloadSpeed();
        long downloaded = progress.getDownloadedBytes();
        long total = progress.getTotalBytes();
        
        String progressBar = createProgressBar(percent, 30);
        String speedStr = speed > 0 ? FileUtils.formatFileSize(speed) + "/s" : "N/A";
        String sizeStr = String.format("%s/%s", 
                                     FileUtils.formatFileSize(downloaded),
                                     total > 0 ? FileUtils.formatFileSize(total) : "Unknown");
        
        String eta = "";
        if (progress.getEstimatedTimeRemaining() > 0) {
            eta = String.format(" | ETA: %s", formatTime(progress.getEstimatedTimeRemaining()));
        }
        
        System.out.printf("  %-30s %s %.2f%% | %s | %s%s%n",
                         truncateFileName(fileName, 30), progressBar, percent, sizeStr, speedStr, eta);
    }
    
    private String createProgressBar(double percent, int length) {
        int filled = (int) (percent / 100.0 * length);
        StringBuilder bar = new StringBuilder("[");
        
        for (int i = 0; i < length; i++) {
            if (i < filled) {
                bar.append("█");
            } else if (i == filled && percent % (100.0 / length) > 0) {
                bar.append("▓");
            } else {
                bar.append("░");
            }
        }
        
        bar.append("]");
        return bar.toString();
    }
    
    private String truncateFileName(String fileName, int maxLength) {
        if (fileName.length() <= maxLength) {
            return fileName;
        }
        return fileName.substring(0, maxLength - 3) + "...";
    }
    
    private String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return String.format("%dm %ds", seconds / 60, seconds % 60);
        } else {
            return String.format("%dh %dm", seconds / 3600, (seconds % 3600) / 60);
        }
    }
}