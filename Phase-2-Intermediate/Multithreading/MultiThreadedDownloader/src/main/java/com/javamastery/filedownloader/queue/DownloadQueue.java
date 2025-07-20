package com.javamastery.filedownloader.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe download queue using BlockingQueue for producer-consumer pattern.
 * Supports priority-based ordering and dynamic queue operations.
 */
public class DownloadQueue {
    private final BlockingQueue<PriorityDownloadItem> queue;
    private final AtomicInteger size;
    private final int capacity;
    
    public DownloadQueue() {
        this(Integer.MAX_VALUE);
    }
    
    public DownloadQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new PriorityBlockingQueue<>(capacity);
        this.size = new AtomicInteger(0);
    }
    
    /**
     * Adds a download item to the queue. Blocks if queue is full.
     */
    public void add(PriorityDownloadItem item) throws InterruptedException {
        if (size.get() >= capacity) {
            throw new IllegalStateException("Queue is full");
        }
        
        queue.put(item);
        size.incrementAndGet();
    }
    
    /**
     * Attempts to add a download item to the queue with a timeout.
     */
    public boolean offer(PriorityDownloadItem item, long timeout, TimeUnit unit) throws InterruptedException {
        if (size.get() >= capacity) {
            return false;
        }
        
        boolean added = queue.offer(item, timeout, unit);
        if (added) {
            size.incrementAndGet();
        }
        return added;
    }
    
    /**
     * Retrieves and removes the highest priority item. Blocks if queue is empty.
     */
    public PriorityDownloadItem take() throws InterruptedException {
        PriorityDownloadItem item = queue.take();
        size.decrementAndGet();
        return item;
    }
    
    /**
     * Retrieves and removes the highest priority item with a timeout.
     */
    public PriorityDownloadItem poll(long timeout, TimeUnit unit) throws InterruptedException {
        PriorityDownloadItem item = queue.poll(timeout, unit);
        if (item != null) {
            size.decrementAndGet();
        }
        return item;
    }
    
    /**
     * Retrieves but does not remove the highest priority item.
     */
    public PriorityDownloadItem peek() {
        return queue.peek();
    }
    
    /**
     * Removes a specific download item from the queue.
     */
    public boolean remove(PriorityDownloadItem item) {
        boolean removed = queue.remove(item);
        if (removed) {
            size.decrementAndGet();
        }
        return removed;
    }
    
    /**
     * Removes a download item by ID.
     */
    public boolean removeById(String id) {
        PriorityDownloadItem toRemove = queue.stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElse(null);
            
        if (toRemove != null) {
            return remove(toRemove);
        }
        return false;
    }
    
    /**
     * Checks if the queue contains a download with the given ID.
     */
    public boolean containsId(String id) {
        return queue.stream().anyMatch(item -> item.getId().equals(id));
    }
    
    /**
     * Returns the current size of the queue.
     */
    public int size() {
        return size.get();
    }
    
    /**
     * Returns the maximum capacity of the queue.
     */
    public int capacity() {
        return capacity;
    }
    
    /**
     * Checks if the queue is empty.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    /**
     * Checks if the queue is full.
     */
    public boolean isFull() {
        return size.get() >= capacity;
    }
    
    /**
     * Clears all items from the queue.
     */
    public void clear() {
        queue.clear();
        size.set(0);
    }
    
    /**
     * Returns an array containing all elements in the queue.
     */
    public PriorityDownloadItem[] toArray() {
        return queue.toArray(new PriorityDownloadItem[0]);
    }
    
    @Override
    public String toString() {
        return String.format("DownloadQueue{size=%d, capacity=%d, isEmpty=%s}", 
                           size(), capacity(), isEmpty());
    }
}