package com.javamastery.distributed.notification.service;

import com.javamastery.distributed.notification.entity.Notification;
import com.javamastery.distributed.notification.repository.NotificationRepository;
import com.javamastery.distributed.common.dto.notification.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationDto notificationDto;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationDto = new NotificationDto();
        notificationDto.setType("EMAIL");
        notificationDto.setRecipient("test@example.com");
        notificationDto.setSubject("Test Subject");
        notificationDto.setMessage("Test message");
        notificationDto.setScheduledAt(LocalDateTime.now().plusMinutes(10));

        notification = new Notification();
        notification.setId(1L);
        notification.setType("EMAIL");
        notification.setRecipient("test@example.com");
        notification.setSubject("Test Subject");
        notification.setMessage("Test message");
        notification.setStatus("PENDING");
        notification.setScheduledAt(LocalDateTime.now().plusMinutes(10));
    }

    @Test
    void testSendNotification_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        NotificationDto result = notificationService.sendNotification(notificationDto);

        // Assert
        assertNotNull(result);
        assertEquals("EMAIL", result.getType());
        assertEquals("test@example.com", result.getRecipient());
        assertEquals("Test Subject", result.getSubject());
        assertEquals("Test message", result.getMessage());

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetAllNotifications() {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findAll()).thenReturn(notifications);

        // Act
        List<NotificationDto> result = notificationService.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EMAIL", result.get(0).getType());
        assertEquals("test@example.com", result.get(0).getRecipient());

        verify(notificationRepository).findAll();
    }

    @Test
    void testGetNotificationsByStatus() {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByStatus("PENDING")).thenReturn(notifications);

        // Act
        List<NotificationDto> result = notificationService.getNotificationsByStatus("PENDING");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(notificationRepository).findByStatus("PENDING");
    }

    @Test
    void testGetNotificationById_Success() {
        // Arrange
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        // Act
        Optional<NotificationDto> result = notificationService.getNotificationById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("EMAIL", result.get().getType());
        assertEquals("test@example.com", result.get().getRecipient());

        verify(notificationRepository).findById(1L);
    }

    @Test
    void testGetNotificationById_NotFound() {
        // Arrange
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<NotificationDto> result = notificationService.getNotificationById(999L);

        // Assert
        assertFalse(result.isPresent());

        verify(notificationRepository).findById(999L);
    }

    @Test
    void testProcessPendingNotifications() {
        // Arrange
        List<Notification> pendingNotifications = Arrays.asList(notification);
        when(notificationRepository.findPendingNotificationsToSend(any(LocalDateTime.class)))
                .thenReturn(pendingNotifications);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.processPendingNotifications();

        // Assert
        verify(notificationRepository).findPendingNotificationsToSend(any(LocalDateTime.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(notificationRepository).save(any(Notification.class));
    }
}