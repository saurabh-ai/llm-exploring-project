package com.javamastery.filedownloader.config;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe configuration class for network settings.
 */
public class NetworkConfig {
    private final AtomicInteger connectionTimeout; // milliseconds
    private final AtomicInteger readTimeout; // milliseconds
    private final AtomicInteger maxRetries;
    private final AtomicLong retryDelay; // milliseconds
    private final AtomicLong chunkSize; // bytes
    private final AtomicLong bandwidthLimit; // bytes per second, 0 = unlimited
    
    public NetworkConfig() {
        this.connectionTimeout = new AtomicInteger(30000); // 30 seconds
        this.readTimeout = new AtomicInteger(60000); // 60 seconds
        this.maxRetries = new AtomicInteger(3);
        this.retryDelay = new AtomicLong(1000); // 1 second
        this.chunkSize = new AtomicLong(1024 * 1024); // 1 MB
        this.bandwidthLimit = new AtomicLong(0); // Unlimited
    }
    
    public NetworkConfig(int connectionTimeout, int readTimeout, int maxRetries, 
                        long retryDelay, long chunkSize, long bandwidthLimit) {
        this.connectionTimeout = new AtomicInteger(connectionTimeout);
        this.readTimeout = new AtomicInteger(readTimeout);
        this.maxRetries = new AtomicInteger(maxRetries);
        this.retryDelay = new AtomicLong(retryDelay);
        this.chunkSize = new AtomicLong(chunkSize);
        this.bandwidthLimit = new AtomicLong(bandwidthLimit);
    }
    
    // Thread-safe getters and setters
    public int getConnectionTimeout() {
        return connectionTimeout.get();
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        if (connectionTimeout <= 0) {
            throw new IllegalArgumentException("Connection timeout must be positive");
        }
        this.connectionTimeout.set(connectionTimeout);
    }
    
    public int getReadTimeout() {
        return readTimeout.get();
    }
    
    public void setReadTimeout(int readTimeout) {
        if (readTimeout <= 0) {
            throw new IllegalArgumentException("Read timeout must be positive");
        }
        this.readTimeout.set(readTimeout);
    }
    
    public int getMaxRetries() {
        return maxRetries.get();
    }
    
    public void setMaxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        this.maxRetries.set(maxRetries);
    }
    
    public long getRetryDelay() {
        return retryDelay.get();
    }
    
    public void setRetryDelay(long retryDelay) {
        if (retryDelay < 0) {
            throw new IllegalArgumentException("Retry delay cannot be negative");
        }
        this.retryDelay.set(retryDelay);
    }
    
    public long getChunkSize() {
        return chunkSize.get();
    }
    
    public void setChunkSize(long chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        this.chunkSize.set(chunkSize);
    }
    
    public long getBandwidthLimit() {
        return bandwidthLimit.get();
    }
    
    public void setBandwidthLimit(long bandwidthLimit) {
        if (bandwidthLimit < 0) {
            throw new IllegalArgumentException("Bandwidth limit cannot be negative");
        }
        this.bandwidthLimit.set(bandwidthLimit);
    }
    
    public boolean isBandwidthLimited() {
        return getBandwidthLimit() > 0;
    }
    
    @Override
    public String toString() {
        return String.format("NetworkConfig{connectionTimeout=%d ms, readTimeout=%d ms, maxRetries=%d, retryDelay=%d ms, chunkSize=%d bytes, bandwidthLimit=%d B/s}",
                           getConnectionTimeout(), getReadTimeout(), getMaxRetries(), 
                           getRetryDelay(), getChunkSize(), getBandwidthLimit());
    }
}