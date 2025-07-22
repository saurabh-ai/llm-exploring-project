package com.javamastery.distributed.common.dto.notification;

import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;
    private String type; // EMAIL, SMS, PUSH
    private String recipient;
    private String subject;
    private String message;
    private String status; // PENDING, SENT, FAILED
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String errorMessage;
    
    // Default constructor
    public NotificationDto() {}
    
    // Constructor
    public NotificationDto(String type, String recipient, String subject, String message) {
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.status = "PENDING";
        this.scheduledAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}