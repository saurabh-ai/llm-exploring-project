package com.javamastery.ecommerce.notification.dto;

import com.javamastery.ecommerce.notification.entity.NotificationChannel;
import com.javamastery.ecommerce.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Map;

public class NotificationRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    @NotNull(message = "Channel is required")
    private NotificationChannel channel;
    
    @NotNull(message = "Recipient is required")
    private String recipient;
    
    private String subject;
    private String content;
    private String referenceId;
    private String referenceType;
    private LocalDateTime scheduledAt;
    private Map<String, Object> templateData;
    
    public NotificationRequest() {}
    
    public NotificationRequest(Long userId, NotificationType type, NotificationChannel channel, String recipient) {
        this.userId = userId;
        this.type = type;
        this.channel = channel;
        this.recipient = recipient;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationChannel getChannel() {
        return channel;
    }
    
    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getReferenceType() {
        return referenceType;
    }
    
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }
    
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public Map<String, Object> getTemplateData() {
        return templateData;
    }
    
    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }
}