package com.javamastery.filedownloader.exception;

/**
 * Base exception for all download-related errors.
 * This provides a common base for handling all download exceptions.
 */
public class DownloadException extends Exception {
    
    public DownloadException(String message) {
        super(message);
    }
    
    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DownloadException(Throwable cause) {
        super(cause);
    }
}