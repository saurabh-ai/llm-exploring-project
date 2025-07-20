package com.javamastery.filedownloader.core;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a file chunk for chunk-based downloading.
 * Immutable class representing a portion of a file to be downloaded.
 */
public final class FileChunk {
    private final String url;
    private final Path localPath;
    private final long startByte;
    private final long endByte;
    private final int chunkNumber;
    private final LocalDateTime createdAt;
    
    private FileChunk(Builder builder) {
        this.url = builder.url;
        this.localPath = builder.localPath;
        this.startByte = builder.startByte;
        this.endByte = builder.endByte;
        this.chunkNumber = builder.chunkNumber;
        this.createdAt = LocalDateTime.now();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public String getUrl() { return url; }
    public Path getLocalPath() { return localPath; }
    public long getStartByte() { return startByte; }
    public long getEndByte() { return endByte; }
    public int getChunkNumber() { return chunkNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getSize() { return endByte - startByte + 1; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FileChunk that = (FileChunk) obj;
        return startByte == that.startByte &&
               endByte == that.endByte &&
               chunkNumber == that.chunkNumber &&
               Objects.equals(url, that.url) &&
               Objects.equals(localPath, that.localPath);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(url, localPath, startByte, endByte, chunkNumber);
    }
    
    @Override
    public String toString() {
        return String.format("FileChunk{chunk=%d, range=%d-%d, size=%d, url='%s'}", 
                           chunkNumber, startByte, endByte, getSize(), url);
    }
    
    public static class Builder {
        private String url;
        private Path localPath;
        private long startByte;
        private long endByte;
        private int chunkNumber;
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder localPath(Path localPath) {
            this.localPath = localPath;
            return this;
        }
        
        public Builder range(long startByte, long endByte) {
            this.startByte = startByte;
            this.endByte = endByte;
            return this;
        }
        
        public Builder chunkNumber(int chunkNumber) {
            this.chunkNumber = chunkNumber;
            return this;
        }
        
        public FileChunk build() {
            Objects.requireNonNull(url, "URL cannot be null");
            Objects.requireNonNull(localPath, "Local path cannot be null");
            if (startByte < 0) throw new IllegalArgumentException("Start byte cannot be negative");
            if (endByte < startByte) throw new IllegalArgumentException("End byte must be >= start byte");
            if (chunkNumber < 0) throw new IllegalArgumentException("Chunk number cannot be negative");
            
            return new FileChunk(this);
        }
    }
}