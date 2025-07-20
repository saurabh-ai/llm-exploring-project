package com.javamastery.filedownloader.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileChunk class.
 */
class FileChunkTest {
    
    @TempDir
    Path tempDir;
    
    private String testUrl;
    private Path testPath;
    
    @BeforeEach
    void setUp() {
        testUrl = "http://example.com/test.zip";
        testPath = tempDir.resolve("test.zip");
    }
    
    @Test
    void testFileChunkCreation() {
        FileChunk chunk = FileChunk.builder()
            .url(testUrl)
            .localPath(testPath)
            .range(0, 1023)
            .chunkNumber(1)
            .build();
        
        assertEquals(testUrl, chunk.getUrl());
        assertEquals(testPath, chunk.getLocalPath());
        assertEquals(0, chunk.getStartByte());
        assertEquals(1023, chunk.getEndByte());
        assertEquals(1, chunk.getChunkNumber());
        assertEquals(1024, chunk.getSize());
        assertNotNull(chunk.getCreatedAt());
    }
    
    @Test
    void testFileChunkBuilder() {
        FileChunk chunk = FileChunk.builder()
            .url(testUrl)
            .localPath(testPath)
            .range(1024, 2047)
            .chunkNumber(2)
            .build();
        
        assertEquals(1024, chunk.getSize());
        assertTrue(chunk.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    void testInvalidParameters() {
        FileChunk.Builder builder = FileChunk.builder();
        
        // Test null URL
        assertThrows(NullPointerException.class, () ->
            builder.url(null).localPath(testPath).range(0, 100).chunkNumber(1).build()
        );
        
        // Test null path
        assertThrows(NullPointerException.class, () ->
            builder.url(testUrl).localPath(null).range(0, 100).chunkNumber(1).build()
        );
        
        // Test negative start byte
        assertThrows(IllegalArgumentException.class, () ->
            builder.url(testUrl).localPath(testPath).range(-1, 100).chunkNumber(1).build()
        );
        
        // Test invalid range
        assertThrows(IllegalArgumentException.class, () ->
            builder.url(testUrl).localPath(testPath).range(100, 50).chunkNumber(1).build()
        );
    }
    
    @Test
    void testEquality() {
        FileChunk chunk1 = FileChunk.builder()
            .url(testUrl)
            .localPath(testPath)
            .range(0, 1023)
            .chunkNumber(1)
            .build();
        
        FileChunk chunk2 = FileChunk.builder()
            .url(testUrl)
            .localPath(testPath)
            .range(0, 1023)
            .chunkNumber(1)
            .build();
        
        assertEquals(chunk1, chunk2);
        assertEquals(chunk1.hashCode(), chunk2.hashCode());
    }
    
    @Test
    void testToString() {
        FileChunk chunk = FileChunk.builder()
            .url(testUrl)
            .localPath(testPath)
            .range(1024, 2047)
            .chunkNumber(2)
            .build();
        
        String str = chunk.toString();
        assertTrue(str.contains("chunk=2"));
        assertTrue(str.contains("range=1024-2047"));
        assertTrue(str.contains("size=1024"));
        assertTrue(str.contains(testUrl));
    }
}