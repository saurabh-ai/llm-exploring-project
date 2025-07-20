package com.javamastery.filedownloader.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe main configuration class that combines all configuration settings.
 */
public class DownloadConfig {
    private final AtomicReference<Path> downloadDirectory;
    private final AtomicReference<ThreadConfig> threadConfig;
    private final AtomicReference<NetworkConfig> networkConfig;
    private final AtomicBoolean resumeSupported;
    private final AtomicBoolean progressReporting;
    
    public DownloadConfig() {
        this.downloadDirectory = new AtomicReference<>(Paths.get("downloads"));
        this.threadConfig = new AtomicReference<>(new ThreadConfig());
        this.networkConfig = new AtomicReference<>(new NetworkConfig());
        this.resumeSupported = new AtomicBoolean(true);
        this.progressReporting = new AtomicBoolean(true);
    }
    
    public DownloadConfig(Path downloadDirectory, ThreadConfig threadConfig, NetworkConfig networkConfig) {
        this.downloadDirectory = new AtomicReference<>(downloadDirectory);
        this.threadConfig = new AtomicReference<>(threadConfig);
        this.networkConfig = new AtomicReference<>(networkConfig);
        this.resumeSupported = new AtomicBoolean(true);
        this.progressReporting = new AtomicBoolean(true);
    }
    
    // Thread-safe getters and setters
    public Path getDownloadDirectory() {
        return downloadDirectory.get();
    }
    
    public void setDownloadDirectory(Path downloadDirectory) {
        if (downloadDirectory == null) {
            throw new IllegalArgumentException("Download directory cannot be null");
        }
        this.downloadDirectory.set(downloadDirectory);
    }
    
    public ThreadConfig getThreadConfig() {
        return threadConfig.get();
    }
    
    public void setThreadConfig(ThreadConfig threadConfig) {
        if (threadConfig == null) {
            throw new IllegalArgumentException("Thread config cannot be null");
        }
        this.threadConfig.set(threadConfig);
    }
    
    public NetworkConfig getNetworkConfig() {
        return networkConfig.get();
    }
    
    public void setNetworkConfig(NetworkConfig networkConfig) {
        if (networkConfig == null) {
            throw new IllegalArgumentException("Network config cannot be null");
        }
        this.networkConfig.set(networkConfig);
    }
    
    public boolean isResumeSupported() {
        return resumeSupported.get();
    }
    
    public void setResumeSupported(boolean resumeSupported) {
        this.resumeSupported.set(resumeSupported);
    }
    
    public boolean isProgressReporting() {
        return progressReporting.get();
    }
    
    public void setProgressReporting(boolean progressReporting) {
        this.progressReporting.set(progressReporting);
    }
    
    // Builder pattern for easier configuration
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Path downloadDirectory = Paths.get("downloads");
        private ThreadConfig threadConfig = new ThreadConfig();
        private NetworkConfig networkConfig = new NetworkConfig();
        private boolean resumeSupported = true;
        private boolean progressReporting = true;
        
        public Builder downloadDirectory(Path downloadDirectory) {
            this.downloadDirectory = downloadDirectory;
            return this;
        }
        
        public Builder downloadDirectory(String downloadDirectory) {
            this.downloadDirectory = Paths.get(downloadDirectory);
            return this;
        }
        
        public Builder threadPoolSize(int threadPoolSize) {
            this.threadConfig.setCorePoolSize(threadPoolSize);
            return this;
        }
        
        public Builder maxConcurrentDownloads(int maxConcurrentDownloads) {
            this.threadConfig.setMaxConcurrentDownloads(maxConcurrentDownloads);
            return this;
        }
        
        public Builder connectionTimeout(int timeoutMillis) {
            this.networkConfig.setConnectionTimeout(timeoutMillis);
            return this;
        }
        
        public Builder chunkSize(long chunkSize) {
            this.networkConfig.setChunkSize(chunkSize);
            return this;
        }
        
        public Builder bandwidthLimit(long bytesPerSecond) {
            this.networkConfig.setBandwidthLimit(bytesPerSecond);
            return this;
        }
        
        public Builder resumeSupported(boolean resumeSupported) {
            this.resumeSupported = resumeSupported;
            return this;
        }
        
        public Builder progressReporting(boolean progressReporting) {
            this.progressReporting = progressReporting;
            return this;
        }
        
        public DownloadConfig build() {
            DownloadConfig config = new DownloadConfig(downloadDirectory, threadConfig, networkConfig);
            config.setResumeSupported(resumeSupported);
            config.setProgressReporting(progressReporting);
            return config;
        }
    }
    
    @Override
    public String toString() {
        return String.format("DownloadConfig{downloadDirectory=%s, threadConfig=%s, networkConfig=%s, resumeSupported=%s, progressReporting=%s}",
                           getDownloadDirectory(), getThreadConfig(), getNetworkConfig(), 
                           isResumeSupported(), isProgressReporting());
    }
}