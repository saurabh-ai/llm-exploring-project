package com.javamastery.filedownloader.progress;

/**
 * Interface for progress reporting implementations.
 */
public interface ProgressReporter {
    
    /**
     * Called when progress is updated.
     */
    void onProgressUpdate(ProgressTracker tracker);
}