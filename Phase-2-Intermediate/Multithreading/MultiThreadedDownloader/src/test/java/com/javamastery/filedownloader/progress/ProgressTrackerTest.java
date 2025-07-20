package com.javamastery.filedownloader.progress;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProgressTracker class.
 */
class ProgressTrackerTest {
    
    private ProgressTracker progressTracker;
    
    @BeforeEach
    void setUp() {
        progressTracker = new ProgressTracker();
    }
    
    @Test
    void testRegisterDownload() {
        String downloadId = "test-download-1";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 1024);
        
        DownloadProgress progress = progressTracker.getProgress(downloadId);
        assertNotNull(progress);
        assertEquals(downloadId, progress.getDownloadId());
        assertEquals("test.zip", progress.getFileName());
        assertEquals("http://example.com/test.zip", progress.getUrl());
        assertEquals(1024, progress.getTotalBytes());
        assertEquals(0, progress.getDownloadedBytes());
        assertEquals(DownloadProgress.Status.PENDING, progress.getStatus());
    }
    
    @Test
    void testUpdateProgress() {
        String downloadId = "test-download-1";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 1024);
        
        progressTracker.updateProgress(downloadId, 512);
        
        DownloadProgress progress = progressTracker.getProgress(downloadId);
        assertEquals(512, progress.getDownloadedBytes());
        assertEquals(50.0, progress.getProgressPercent(), 0.01);
    }
    
    @Test
    void testAddProgress() {
        String downloadId = "test-download-1";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 1024);
        
        progressTracker.addProgress(downloadId, 256);
        progressTracker.addProgress(downloadId, 256);
        
        DownloadProgress progress = progressTracker.getProgress(downloadId);
        assertEquals(512, progress.getDownloadedBytes());
        assertEquals(1024, progressTracker.getTotalBytes());
        assertEquals(512, progressTracker.getDownloadedBytes());
    }
    
    @Test
    void testUpdateStatus() {
        String downloadId = "test-download-1";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 1024);
        
        progressTracker.updateStatus(downloadId, DownloadProgress.Status.DOWNLOADING);
        
        DownloadProgress progress = progressTracker.getProgress(downloadId);
        assertEquals(DownloadProgress.Status.DOWNLOADING, progress.getStatus());
        assertTrue(progress.isActive());
        
        progressTracker.updateStatus(downloadId, DownloadProgress.Status.COMPLETED);
        assertEquals(DownloadProgress.Status.COMPLETED, progress.getStatus());
        assertFalse(progress.isActive());
        assertEquals(1, progressTracker.getCompletedDownloads());
    }
    
    @Test
    void testSetError() {
        String downloadId = "test-download-1";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 1024);
        
        progressTracker.setError(downloadId, "Network error");
        
        DownloadProgress progress = progressTracker.getProgress(downloadId);
        assertEquals(DownloadProgress.Status.FAILED, progress.getStatus());
        assertEquals("Network error", progress.getErrorMessage());
        assertTrue(progress.isFailed());
        assertEquals(1, progressTracker.getFailedDownloads());
    }
    
    @Test
    void testOverallProgress() {
        progressTracker.registerDownload("download1", "file1.zip", "http://example.com/file1.zip", 1000);
        progressTracker.registerDownload("download2", "file2.zip", "http://example.com/file2.zip", 2000);
        
        progressTracker.addProgress("download1", 500);  // 50% of first
        progressTracker.addProgress("download2", 1000); // 50% of second
        
        assertEquals(3000, progressTracker.getTotalBytes());
        assertEquals(1500, progressTracker.getDownloadedBytes());
        assertEquals(50.0, progressTracker.getOverallProgressPercent(), 0.01);
        assertEquals(2, progressTracker.getTotalDownloads());
    }
    
    @Test
    void testRemoveProgress() {
        String downloadId = "test-download-1";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 1024);
        progressTracker.addProgress(downloadId, 512);
        
        assertEquals(1024, progressTracker.getTotalBytes());
        assertEquals(512, progressTracker.getDownloadedBytes());
        
        progressTracker.removeProgress(downloadId);
        
        assertNull(progressTracker.getProgress(downloadId));
        assertEquals(0, progressTracker.getTotalBytes());
        assertEquals(0, progressTracker.getDownloadedBytes());
        assertEquals(0, progressTracker.getTotalDownloads());
    }
    
    @Test
    void testConcurrentAccess() throws InterruptedException {
        String downloadId = "concurrent-test";
        progressTracker.registerDownload(downloadId, "test.zip", "http://example.com/test.zip", 10000);
        
        // Simulate concurrent progress updates
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    progressTracker.addProgress(downloadId, 1);
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        DownloadProgress progress = progressTracker.getProgress(downloadId);
        assertEquals(1000, progress.getDownloadedBytes());
    }
}