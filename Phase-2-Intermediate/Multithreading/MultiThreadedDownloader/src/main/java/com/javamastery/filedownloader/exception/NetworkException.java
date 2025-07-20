package com.javamastery.filedownloader.exception;

/**
 * Exception for network-related errors during downloads.
 */
public class NetworkException extends DownloadException {
    
    public NetworkException(String message) {
        super(message);
    }
    
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NetworkException(Throwable cause) {
        super(cause);
    }
}