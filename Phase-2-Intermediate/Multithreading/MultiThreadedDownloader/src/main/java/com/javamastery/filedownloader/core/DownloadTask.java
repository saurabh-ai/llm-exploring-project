package com.javamastery.filedownloader.core;

import com.javamastery.filedownloader.config.DownloadConfig;
import com.javamastery.filedownloader.exception.DownloadException;
import com.javamastery.filedownloader.progress.DownloadProgress;
import com.javamastery.filedownloader.progress.ProgressTracker;
import com.javamastery.filedownloader.queue.PriorityDownloadItem;
import com.javamastery.filedownloader.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents an individual download task implementing Callable.
 * Handles the actual download process with progress tracking and error handling.
 */
public class DownloadTask implements Callable<DownloadResult> {
    
    private final PriorityDownloadItem downloadItem;
    private final DownloadConfig config;
    private final ProgressTracker progressTracker;
    private final AtomicBoolean cancelled;
    private final AtomicLong startTime;
    
    public DownloadTask(PriorityDownloadItem downloadItem, DownloadConfig config, ProgressTracker progressTracker) {
        this.downloadItem = downloadItem;
        this.config = config;
        this.progressTracker = progressTracker;
        this.cancelled = new AtomicBoolean(false);
        this.startTime = new AtomicLong(0);
    }
    
    @Override
    public DownloadResult call() throws Exception {
        String downloadId = downloadItem.getId();
        startTime.set(System.currentTimeMillis());
        
        try {
            // Update status to downloading
            progressTracker.updateStatus(downloadId, DownloadProgress.Status.DOWNLOADING);
            
            // Perform the download
            return performDownload();
            
        } catch (Exception e) {
            progressTracker.setError(downloadId, e.getMessage());
            throw new DownloadException("Download failed for " + downloadItem.getUrl(), e);
        }
    }
    
    private DownloadResult performDownload() throws IOException, DownloadException {
        String url = downloadItem.getUrl();
        Path destinationPath = downloadItem.getDestinationPath();
        String downloadId = downloadItem.getId();
        
        // Create parent directories if they don't exist
        FileUtils.createParentDirectories(destinationPath);
        
        // Create temporary file for download
        Path tempFile = FileUtils.createTempFile(destinationPath);
        
        HttpURLConnection connection = null;
        try {
            // Open connection
            connection = openConnection(url);
            
            // Get file size and register progress
            long totalBytes = connection.getContentLengthLong();
            if (totalBytes == -1) {
                totalBytes = 0; // Unknown size
            }
            
            String fileName = destinationPath.getFileName().toString();
            progressTracker.registerDownload(downloadId, fileName, url, totalBytes);
            
            // Check for resume support
            long startByte = 0;
            if (config.isResumeSupported() && FileUtils.exists(tempFile)) {
                startByte = FileUtils.getFileSize(tempFile);
                if (startByte > 0 && startByte < totalBytes) {
                    // Resume download
                    connection.disconnect();
                    connection = openConnectionWithRange(url, startByte);
                    progressTracker.updateProgress(downloadId, startByte);
                }
            }
            
            // Download the file
            downloadFile(connection, tempFile, startByte > 0);
            
            // Move temp file to final destination
            FileUtils.moveFile(tempFile, destinationPath);
            
            // Update status to completed
            progressTracker.updateStatus(downloadId, DownloadProgress.Status.COMPLETED);
            
            return DownloadResult.success(downloadId, destinationPath);
            
        } catch (IOException e) {
            // Clean up temp file on error
            try {
                FileUtils.deleteIfExists(tempFile);
            } catch (IOException cleanupException) {
                // Log cleanup error but don't override original exception
                e.addSuppressed(cleanupException);
            }
            throw e;
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private HttpURLConnection openConnection(String url) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        
        // Configure connection
        connection.setConnectTimeout(config.getNetworkConfig().getConnectionTimeout());
        connection.setReadTimeout(config.getNetworkConfig().getReadTimeout());
        connection.setRequestProperty("User-Agent", "MultiThreadedDownloader/1.0");
        
        return connection;
    }
    
    private HttpURLConnection openConnectionWithRange(String url, long startByte) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestProperty("Range", "bytes=" + startByte + "-");
        return connection;
    }
    
    private void downloadFile(HttpURLConnection connection, Path tempFile, boolean append) throws IOException {
        String downloadId = downloadItem.getId();
        long chunkSize = config.getNetworkConfig().getChunkSize();
        long bandwidthLimit = config.getNetworkConfig().getBandwidthLimit();
        
        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream out = new FileOutputStream(tempFile.toFile(), append)) {
            
            byte[] buffer = new byte[(int) Math.min(chunkSize, 8192)];
            int bytesRead;
            long totalRead = 0;
            long lastSpeedUpdate = System.currentTimeMillis();
            long speedTrackingBytes = 0;
            
            while ((bytesRead = in.read(buffer)) != -1 && !cancelled.get()) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                speedTrackingBytes += bytesRead;
                
                // Update progress
                progressTracker.addProgress(downloadId, bytesRead);
                
                // Update speed every second
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSpeedUpdate >= 1000) {
                    long speed = speedTrackingBytes * 1000 / (currentTime - lastSpeedUpdate);
                    progressTracker.updateSpeed(downloadId, speed);
                    speedTrackingBytes = 0;
                    lastSpeedUpdate = currentTime;
                }
                
                // Apply bandwidth limiting if configured
                if (bandwidthLimit > 0) {
                    applyBandwidthThrottling(totalRead, bandwidthLimit);
                }
            }
            
            // Ensure all data is written
            out.flush();
            out.getFD().sync();
        }
        
        // Check if download was cancelled
        if (cancelled.get()) {
            throw new IOException("Download was cancelled");
        }
    }
    
    private void applyBandwidthThrottling(long totalBytesRead, long bandwidthLimit) {
        long elapsedTime = System.currentTimeMillis() - startTime.get();
        if (elapsedTime > 0) {
            long expectedTime = (totalBytesRead * 1000) / bandwidthLimit;
            long sleepTime = expectedTime - elapsedTime;
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(Math.min(sleepTime, 1000)); // Max 1 second sleep
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public void cancel() {
        cancelled.set(true);
        progressTracker.updateStatus(downloadItem.getId(), DownloadProgress.Status.CANCELLED);
    }
    
    public boolean isCancelled() {
        return cancelled.get();
    }
    
    public PriorityDownloadItem getDownloadItem() {
        return downloadItem;
    }
}