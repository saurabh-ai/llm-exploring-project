package com.javamastery.distributed.notification.service;

import com.javamastery.distributed.notification.entity.Notification;
import com.javamastery.distributed.notification.repository.NotificationRepository;
import com.javamastery.distributed.common.dto.notification.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    public NotificationDto sendNotification(NotificationDto notificationDto) {
        // Create notification entity
        Notification notification = new Notification();
        notification.setType(notificationDto.getType());
        notification.setRecipient(notificationDto.getRecipient());
        notification.setSubject(notificationDto.getSubject());
        notification.setMessage(notificationDto.getMessage());
        notification.setScheduledAt(notificationDto.getScheduledAt() != null ? 
            notificationDto.getScheduledAt() : LocalDateTime.now());

        // Save notification
        Notification savedNotification = notificationRepository.save(notification);

        // Try to send immediately if scheduled for now or past
        if (savedNotification.getScheduledAt().isBefore(LocalDateTime.now().plusMinutes(1))) {
            processSingleNotification(savedNotification);
        }

        return convertToDto(savedNotification);
    }

    public void processPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository
            .findPendingNotificationsToSend(LocalDateTime.now());
        
        for (Notification notification : pendingNotifications) {
            processSingleNotification(notification);
        }
    }

    private void processSingleNotification(Notification notification) {
        try {
            switch (notification.getType().toUpperCase()) {
                case "EMAIL":
                    sendEmail(notification);
                    break;
                case "SMS":
                    sendSms(notification);
                    break;
                case "PUSH":
                    sendPushNotification(notification);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported notification type: " + notification.getType());
            }
            
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setErrorMessage(null);
            
        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            
            // Schedule retry if within retry limit
            if (notification.getRetryCount() < notification.getMaxRetries()) {
                notification.setStatus("PENDING");
                notification.setScheduledAt(LocalDateTime.now().plusMinutes(5 * notification.getRetryCount()));
            }
        }
        
        notificationRepository.save(notification);
    }

    private void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        message.setFrom("noreply@taskscheduler.com");
        
        mailSender.send(message);
    }

    private void sendSms(Notification notification) {
        // SMS implementation would go here
        // For now, just simulate SMS sending
        System.out.println("Sending SMS to " + notification.getRecipient() + ": " + notification.getMessage());
    }

    private void sendPushNotification(Notification notification) {
        // Push notification implementation would go here
        // For now, just simulate push notification
        System.out.println("Sending Push to " + notification.getRecipient() + ": " + notification.getMessage());
    }

    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<NotificationDto> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToDto);
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setRecipient(notification.getRecipient());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setStatus(notification.getStatus());
        dto.setScheduledAt(notification.getScheduledAt());
        dto.setSentAt(notification.getSentAt());
        dto.setErrorMessage(notification.getErrorMessage());
        return dto;
    }
}