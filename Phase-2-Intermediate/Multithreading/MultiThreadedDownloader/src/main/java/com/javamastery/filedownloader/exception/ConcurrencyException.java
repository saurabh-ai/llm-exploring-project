package com.javamastery.filedownloader.exception;

/**
 * Exception for concurrency-related errors in multi-threaded operations.
 */
public class ConcurrencyException extends DownloadException {
    
    public ConcurrencyException(String message) {
        super(message);
    }
    
    public ConcurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ConcurrencyException(Throwable cause) {
        super(cause);
    }
}