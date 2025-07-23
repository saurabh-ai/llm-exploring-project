package com.javamastery.ecommerce.notification.controller;

import com.javamastery.ecommerce.notification.dto.NotificationRequest;
import com.javamastery.ecommerce.notification.entity.Notification;
import com.javamastery.ecommerce.notification.entity.NotificationStatus;
import com.javamastery.ecommerce.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "Email and SMS notification operations")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping
    @Operation(summary = "Send notification", description = "Send a notification via email or SMS")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationRequest request) {
        logger.info("Sending notification request: {} to {}", request.getType(), request.getRecipient());
        try {
            notificationService.sendNotification(request);
            return ResponseEntity.ok("Notification queued for sending");
        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send notification: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Retrieve all notifications for a specific user")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Retrieving notifications for user {}", userId);
        List<Notification> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status", description = "Retrieve notifications by status")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable NotificationStatus status) {
        logger.info("Retrieving notifications with status {}", status);
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed notifications", description = "Retry sending failed notifications")
    public ResponseEntity<String> retryFailedNotifications() {
        logger.info("Retrying failed notifications");
        try {
            notificationService.retryFailedNotifications();
            return ResponseEntity.ok("Failed notifications retry initiated");
        } catch (Exception e) {
            logger.error("Failed to retry notifications: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to retry notifications: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check notification service health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is running");
    }
}