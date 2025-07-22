package com.javamastery.distributed.notification.controller;

import com.javamastery.distributed.notification.service.NotificationService;
import com.javamastery.distributed.common.dto.notification.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationDto notificationDto) {
        try {
            NotificationDto result = notificationService.sendNotification(notificationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send notification: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        try {
            List<NotificationDto> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notifications"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        try {
            Optional<NotificationDto> notification = notificationService.getNotificationById(id);
            if (notification.isPresent()) {
                return ResponseEntity.ok(notification.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Notification not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notification"));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getNotificationsByStatus(@PathVariable String status) {
        try {
            List<NotificationDto> notifications = notificationService.getNotificationsByStatus(status);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notifications by status"));
        }
    }

    @PostMapping("/process-pending")
    public ResponseEntity<?> processPendingNotifications() {
        try {
            notificationService.processPendingNotifications();
            return ResponseEntity.ok(Map.of("message", "Processing pending notifications"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process pending notifications"));
        }
    }
}