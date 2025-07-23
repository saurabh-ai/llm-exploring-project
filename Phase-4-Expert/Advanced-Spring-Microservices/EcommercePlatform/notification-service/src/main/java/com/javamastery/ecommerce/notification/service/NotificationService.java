package com.javamastery.ecommerce.notification.service;

import com.javamastery.ecommerce.notification.dto.NotificationRequest;
import com.javamastery.ecommerce.notification.entity.Notification;
import com.javamastery.ecommerce.notification.entity.NotificationChannel;
import com.javamastery.ecommerce.notification.entity.NotificationStatus;
import com.javamastery.ecommerce.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private TemplateService templateService;
    
    @Value("${notification.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Async
    public void sendNotification(NotificationRequest request) {
        logger.info("Sending notification of type {} to {} via {}", 
                   request.getType(), request.getRecipient(), request.getChannel());
        
        // Create notification record
        Notification notification = createNotificationRecord(request);
        
        // Process content and subject
        processNotificationContent(notification, request);
        
        // Save notification
        notification = notificationRepository.save(notification);
        
        // Send notification
        sendNotificationByChannel(notification);
    }
    
    private Notification createNotificationRecord(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setChannel(request.getChannel());
        notification.setRecipient(request.getRecipient());
        notification.setReferenceId(request.getReferenceId());
        notification.setReferenceType(request.getReferenceType());
        notification.setScheduledAt(request.getScheduledAt());
        notification.setMaxRetries(maxRetryAttempts);
        
        return notification;
    }
    
    private void processNotificationContent(Notification notification, NotificationRequest request) {
        String subject = request.getSubject();
        String content = request.getContent();
        
        // Use template if content is not provided
        if (content == null || content.isEmpty()) {
            try {
                subject = templateService.processSubjectTemplate(notification.getType(), request.getTemplateData());
                content = templateService.processContentTemplate(notification.getType(), request.getTemplateData());
            } catch (Exception e) {
                logger.warn("Failed to process template for notification type {}: {}", 
                          notification.getType(), e.getMessage());
                subject = getDefaultSubject(notification.getType());
                content = getDefaultContent(notification.getType(), request);
            }
        }
        
        notification.setSubject(subject);
        notification.setContent(content);
    }
    
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private void sendNotificationByChannel(Notification notification) {
        try {
            notification.setStatus(NotificationStatus.SENDING);
            notificationRepository.save(notification);
            
            boolean success = false;
            switch (notification.getChannel()) {
                case EMAIL:
                    success = emailService.sendEmail(notification.getRecipient(), 
                                                   notification.getSubject(), 
                                                   notification.getContent());
                    break;
                case SMS:
                    success = smsService.sendSms(notification.getRecipient(), 
                                               notification.getContent());
                    break;
                default:
                    logger.warn("Unsupported notification channel: {}", notification.getChannel());
                    success = false;
            }
            
            if (success) {
                notification.setStatus(NotificationStatus.SENT);
                logger.info("Notification {} sent successfully", notification.getId());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send notification");
                logger.error("Failed to send notification {}", notification.getId());
            }
            
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            logger.error("Error sending notification {}: {}", notification.getId(), e.getMessage());
            throw e; // Re-throw for retry mechanism
        } finally {
            notificationRepository.save(notification);
        }
    }
    
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }
    
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry();
        
        for (Notification notification : failedNotifications) {
            if (notification.getRetryCount() < notification.getMaxRetries()) {
                logger.info("Retrying notification {}, attempt {}", 
                          notification.getId(), notification.getRetryCount() + 1);
                sendNotificationByChannel(notification);
            }
        }
    }
    
    private String getDefaultSubject(com.javamastery.ecommerce.notification.entity.NotificationType type) {
        return switch (type) {
            case ORDER_CONFIRMATION -> "Order Confirmation";
            case PAYMENT_CONFIRMATION -> "Payment Confirmation";
            case PAYMENT_FAILED -> "Payment Failed";
            case PAYMENT_REFUNDED -> "Payment Refunded";
            default -> "Notification from E-commerce Platform";
        };
    }
    
    private String getDefaultContent(com.javamastery.ecommerce.notification.entity.NotificationType type, 
                                   NotificationRequest request) {
        return switch (type) {
            case ORDER_CONFIRMATION -> "Your order has been confirmed. Reference: " + request.getReferenceId();
            case PAYMENT_CONFIRMATION -> "Your payment has been processed successfully. Reference: " + request.getReferenceId();
            case PAYMENT_FAILED -> "Your payment could not be processed. Please try again. Reference: " + request.getReferenceId();
            case PAYMENT_REFUNDED -> "Your payment has been refunded. Reference: " + request.getReferenceId();
            default -> "You have a new notification from E-commerce Platform.";
        };
    }
}