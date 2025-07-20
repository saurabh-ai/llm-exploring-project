package com.javamastery.filedownloader.core;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Result of a download operation.
 */
public class DownloadResult {
    private final String downloadId;
    private final Path filePath;
    private final boolean successful;
    private final Exception error;
    private final LocalDateTime completedAt;
    
    public DownloadResult(String downloadId, Path filePath, boolean successful, Exception error) {
        this.downloadId = downloadId;
        this.filePath = filePath;
        this.successful = successful;
        this.error = error;
        this.completedAt = LocalDateTime.now();
    }
    
    public static DownloadResult success(String downloadId, Path filePath) {
        return new DownloadResult(downloadId, filePath, true, null);
    }
    
    public static DownloadResult failure(String downloadId, Exception error) {
        return new DownloadResult(downloadId, null, false, error);
    }
    
    public String getDownloadId() { return downloadId; }
    public Path getFilePath() { return filePath; }
    public boolean isSuccessful() { return successful; }
    public Exception getError() { return error; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    
    @Override
    public String toString() {
        return String.format("DownloadResult{id='%s', path='%s', successful=%s, completedAt=%s}",
                           downloadId, filePath, successful, completedAt);
    }
}