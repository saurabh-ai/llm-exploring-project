package com.javamastery.filedownloader.core;

import com.javamastery.filedownloader.config.DownloadConfig;
import com.javamastery.filedownloader.exception.ConcurrencyException;
import com.javamastery.filedownloader.exception.DownloadException;
import com.javamastery.filedownloader.progress.ProgressTracker;
import com.javamastery.filedownloader.queue.DownloadQueue;
import com.javamastery.filedownloader.queue.PriorityDownloadItem;
import com.javamastery.filedownloader.util.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main download orchestrator class managing the entire download process.
 * Handles ExecutorService management, task distribution, and coordination.
 */
public class DownloadEngine {
    
    private final DownloadConfig config;
    private final ProgressTracker progressTracker;
    private final DownloadQueue downloadQueue;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledService;
    private final AtomicBoolean running;
    private final AtomicBoolean shutdown;
    private final AtomicInteger activeDownloads;
    private final List<Future<DownloadResult>> activeTasks;
    
    private DownloadEngine(Builder builder) {
        this.config = builder.config;
        this.progressTracker = new ProgressTracker();
        this.downloadQueue = new DownloadQueue(config.getThreadConfig().getMaxConcurrentDownloads() * 2);
        
        // Create thread pool with custom settings
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            config.getThreadConfig().getCorePoolSize(),
            config.getThreadConfig().getMaximumPoolSize(),
            config.getThreadConfig().getKeepAliveTime(),
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new DownloadThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        this.executorService = executor;
        this.scheduledService = Executors.newScheduledThreadPool(2, new DownloadThreadFactory());
        this.running = new AtomicBoolean(false);
        this.shutdown = new AtomicBoolean(false);
        this.activeDownloads = new AtomicInteger(0);
        this.activeTasks = new ArrayList<>();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Adds a download to the queue with normal priority.
     */
    public String addDownload(String url, String destinationPath) throws InterruptedException {
        return addDownload(url, destinationPath, PriorityDownloadItem.Priority.NORMAL);
    }
    
    /**
     * Adds a download to the queue with specified priority.
     */
    public String addDownload(String url, String destinationPath, PriorityDownloadItem.Priority priority) throws InterruptedException {
        if (shutdown.get()) {
            throw new IllegalStateException("Download engine is shut down");
        }
        
        String downloadId = generateDownloadId();
        Path path = Paths.get(destinationPath);
        
        // If path is just a directory, extract filename from URL
        if (destinationPath.endsWith("/") || destinationPath.endsWith("\\")) {
            String fileName = FileUtils.extractFileNameFromUrl(url);
            path = Paths.get(destinationPath, fileName);
        }
        
        PriorityDownloadItem item = new PriorityDownloadItem(downloadId, url, path, priority);
        downloadQueue.add(item);
        
        return downloadId;
    }
    
    /**
     * Starts the download process and returns a CompletableFuture for async handling.
     */
    public CompletableFuture<Void> startDownloads() {
        if (running.get()) {
            throw new IllegalStateException("Download engine is already running");
        }
        
        if (shutdown.get()) {
            throw new IllegalStateException("Download engine is shut down");
        }
        
        running.set(true);
        
        return CompletableFuture.runAsync(this::processDownloadQueue, executorService);
    }
    
    /**
     * Stops all downloads gracefully.
     */
    public CompletableFuture<Void> stop() {
        running.set(false);
        
        return CompletableFuture.runAsync(() -> {
            // Cancel all active tasks
            synchronized (activeTasks) {
                for (Future<DownloadResult> task : activeTasks) {
                    task.cancel(true);
                }
                activeTasks.clear();
            }
            
            // Wait for active downloads to complete
            while (activeDownloads.get() > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
    
    /**
     * Shuts down the download engine completely.
     */
    public void shutdown() {
        shutdown.set(true);
        running.set(false);
        
        // Cancel all queued and active downloads
        downloadQueue.clear();
        
        synchronized (activeTasks) {
            for (Future<DownloadResult> task : activeTasks) {
                task.cancel(true);
            }
            activeTasks.clear();
        }
        
        // Shutdown executors
        scheduledService.shutdown();
        executorService.shutdown();
        
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduledService.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduledService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Removes a download from the queue by ID.
     */
    public boolean removeDownload(String downloadId) {
        return downloadQueue.removeById(downloadId);
    }
    
    /**
     * Gets the progress tracker for monitoring downloads.
     */
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }
    
    /**
     * Gets the download queue.
     */
    public DownloadQueue getDownloadQueue() {
        return downloadQueue;
    }
    
    /**
     * Gets the current configuration.
     */
    public DownloadConfig getConfig() {
        return config;
    }
    
    /**
     * Checks if the engine is currently running.
     */
    public boolean isRunning() {
        return running.get();
    }
    
    /**
     * Checks if the engine is shut down.
     */
    public boolean isShutdown() {
        return shutdown.get();
    }
    
    /**
     * Gets the number of active downloads.
     */
    public int getActiveDownloadCount() {
        return activeDownloads.get();
    }
    
    private void processDownloadQueue() {
        while (running.get() && !shutdown.get()) {
            try {
                // Check if we can start more downloads
                int maxConcurrent = config.getThreadConfig().getMaxConcurrentDownloads();
                if (activeDownloads.get() >= maxConcurrent) {
                    // Wait a bit before checking again
                    Thread.sleep(100);
                    continue;
                }
                
                // Get next download from queue
                PriorityDownloadItem item = downloadQueue.poll(1, TimeUnit.SECONDS);
                if (item == null) {
                    continue;
                }
                
                // Start the download
                startDownloadTask(item);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Log error but continue processing
                System.err.println("Error in download queue processing: " + e.getMessage());
            }
        }
    }
    
    private void startDownloadTask(PriorityDownloadItem item) {
        DownloadTask task = new DownloadTask(item, config, progressTracker);
        activeDownloads.incrementAndGet();
        
        Future<DownloadResult> future = executorService.submit(task);
        
        synchronized (activeTasks) {
            activeTasks.add(future);
        }
        
        // Handle task completion asynchronously
        CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (Exception e) {
                return DownloadResult.failure(item.getId(), e);
            }
        }, executorService).whenComplete((result, throwable) -> {
            // Remove from active tasks
            synchronized (activeTasks) {
                activeTasks.remove(future);
            }
            activeDownloads.decrementAndGet();
            
            // Handle retry logic if download failed
            if (!result.isSuccessful() && item.getRetryCount() < config.getNetworkConfig().getMaxRetries()) {
                scheduleRetry(item);
            }
        });
    }
    
    private void scheduleRetry(PriorityDownloadItem item) {
        item.incrementRetryCount();
        long delay = config.getNetworkConfig().getRetryDelay() * (1L << item.getRetryCount()); // Exponential backoff
        
        scheduledService.schedule(() -> {
            try {
                downloadQueue.add(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private String generateDownloadId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Custom thread factory for download threads.
     */
    private static class DownloadThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix = "DownloadThread-";
        
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
    
    /**
     * Builder class for creating DownloadEngine instances.
     */
    public static class Builder {
        private DownloadConfig config = new DownloadConfig();
        
        public Builder config(DownloadConfig config) {
            this.config = config;
            return this;
        }
        
        public Builder threadPoolSize(int threadPoolSize) {
            config.getThreadConfig().setCorePoolSize(threadPoolSize);
            return this;
        }
        
        public Builder maxConcurrentDownloads(int maxConcurrentDownloads) {
            config.getThreadConfig().setMaxConcurrentDownloads(maxConcurrentDownloads);
            return this;
        }
        
        public Builder connectionTimeout(int timeoutMillis) {
            config.getNetworkConfig().setConnectionTimeout(timeoutMillis);
            return this;
        }
        
        public Builder downloadDirectory(String directory) {
            config.setDownloadDirectory(Paths.get(directory));
            return this;
        }
        
        public DownloadEngine build() {
            return new DownloadEngine(this);
        }
    }
}