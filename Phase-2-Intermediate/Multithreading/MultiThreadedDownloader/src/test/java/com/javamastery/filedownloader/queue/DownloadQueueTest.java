package com.javamastery.filedownloader.queue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DownloadQueue class.
 */
class DownloadQueueTest {
    
    @TempDir
    Path tempDir;
    
    private DownloadQueue queue;
    
    @BeforeEach
    void setUp() {
        queue = new DownloadQueue(10); // Capacity of 10
    }
    
    @Test
    void testBasicOperations() throws InterruptedException {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        
        PriorityDownloadItem item = new PriorityDownloadItem(
            "test-1", 
            "http://example.com/file1.zip", 
            tempDir.resolve("file1.zip"),
            PriorityDownloadItem.Priority.NORMAL
        );
        
        queue.add(item);
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());
        
        PriorityDownloadItem retrieved = queue.take();
        assertEquals(item, retrieved);
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }
    
    @Test
    void testPriorityOrdering() throws InterruptedException {
        PriorityDownloadItem lowPriority = new PriorityDownloadItem(
            "low", "http://example.com/low.zip", tempDir.resolve("low.zip"),
            PriorityDownloadItem.Priority.LOW
        );
        
        PriorityDownloadItem highPriority = new PriorityDownloadItem(
            "high", "http://example.com/high.zip", tempDir.resolve("high.zip"),
            PriorityDownloadItem.Priority.HIGH
        );
        
        PriorityDownloadItem urgentPriority = new PriorityDownloadItem(
            "urgent", "http://example.com/urgent.zip", tempDir.resolve("urgent.zip"),
            PriorityDownloadItem.Priority.URGENT
        );
        
        // Add in reverse order of priority
        queue.add(lowPriority);
        queue.add(highPriority);
        queue.add(urgentPriority);
        
        // Should come out in priority order
        assertEquals(urgentPriority, queue.take());
        assertEquals(highPriority, queue.take());
        assertEquals(lowPriority, queue.take());
    }
    
    @Test
    void testOfferWithTimeout() throws InterruptedException {
        DownloadQueue smallQueue = new DownloadQueue(1);
        
        PriorityDownloadItem item1 = new PriorityDownloadItem(
            "item1", "http://example.com/item1.zip", tempDir.resolve("item1.zip")
        );
        
        PriorityDownloadItem item2 = new PriorityDownloadItem(
            "item2", "http://example.com/item2.zip", tempDir.resolve("item2.zip")
        );
        
        assertTrue(smallQueue.offer(item1, 100, TimeUnit.MILLISECONDS));
        assertFalse(smallQueue.offer(item2, 100, TimeUnit.MILLISECONDS)); // Should timeout
    }
    
    @Test
    void testPollWithTimeout() throws InterruptedException {
        assertNull(queue.poll(100, TimeUnit.MILLISECONDS)); // Should timeout
        
        PriorityDownloadItem item = new PriorityDownloadItem(
            "test", "http://example.com/test.zip", tempDir.resolve("test.zip")
        );
        
        queue.add(item);
        assertEquals(item, queue.poll(100, TimeUnit.MILLISECONDS));
    }
    
    @Test
    void testRemoveById() throws InterruptedException {
        PriorityDownloadItem item1 = new PriorityDownloadItem(
            "item1", "http://example.com/item1.zip", tempDir.resolve("item1.zip")
        );
        
        PriorityDownloadItem item2 = new PriorityDownloadItem(
            "item2", "http://example.com/item2.zip", tempDir.resolve("item2.zip")
        );
        
        queue.add(item1);
        queue.add(item2);
        
        assertTrue(queue.containsId("item1"));
        assertTrue(queue.removeById("item1"));
        assertFalse(queue.containsId("item1"));
        assertEquals(1, queue.size());
        
        assertFalse(queue.removeById("nonexistent"));
    }
    
    @Test
    void testCapacityLimits() throws InterruptedException {
        DownloadQueue smallQueue = new DownloadQueue(2);
        
        PriorityDownloadItem item1 = new PriorityDownloadItem(
            "item1", "http://example.com/item1.zip", tempDir.resolve("item1.zip")
        );
        
        PriorityDownloadItem item2 = new PriorityDownloadItem(
            "item2", "http://example.com/item2.zip", tempDir.resolve("item2.zip")
        );
        
        PriorityDownloadItem item3 = new PriorityDownloadItem(
            "item3", "http://example.com/item3.zip", tempDir.resolve("item3.zip")
        );
        
        smallQueue.add(item1);
        smallQueue.add(item2);
        assertEquals(2, smallQueue.capacity());
        assertTrue(smallQueue.isFull());
        
        // Adding another should throw exception
        assertThrows(IllegalStateException.class, () -> smallQueue.add(item3));
    }
    
    @Test
    void testClear() throws InterruptedException {
        queue.add(new PriorityDownloadItem(
            "item1", "http://example.com/item1.zip", tempDir.resolve("item1.zip")
        ));
        
        queue.add(new PriorityDownloadItem(
            "item2", "http://example.com/item2.zip", tempDir.resolve("item2.zip")
        ));
        
        assertEquals(2, queue.size());
        
        queue.clear();
        
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }
    
    @Test
    void testToArray() throws InterruptedException {
        PriorityDownloadItem item1 = new PriorityDownloadItem(
            "item1", "http://example.com/item1.zip", tempDir.resolve("item1.zip"),
            PriorityDownloadItem.Priority.HIGH
        );
        
        PriorityDownloadItem item2 = new PriorityDownloadItem(
            "item2", "http://example.com/item2.zip", tempDir.resolve("item2.zip"),
            PriorityDownloadItem.Priority.LOW
        );
        
        queue.add(item1);
        queue.add(item2);
        
        PriorityDownloadItem[] items = queue.toArray();
        assertEquals(2, items.length);
        // Should be in priority order
        assertEquals(item1, items[0]); // High priority first
        assertEquals(item2, items[1]);
    }
}