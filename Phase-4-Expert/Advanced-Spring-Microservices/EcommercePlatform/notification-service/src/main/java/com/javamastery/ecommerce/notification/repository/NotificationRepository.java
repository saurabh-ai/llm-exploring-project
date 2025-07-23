package com.javamastery.ecommerce.notification.repository;

import com.javamastery.ecommerce.notification.entity.Notification;
import com.javamastery.ecommerce.notification.entity.NotificationStatus;
import com.javamastery.ecommerce.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByStatus(NotificationStatus status);
    
    List<Notification> findByType(NotificationType type);
    
    List<Notification> findByReferenceIdAndReferenceType(String referenceId, String referenceType);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.createdAt >= :fromDate")
    List<Notification> findByStatusAndCreatedAtAfter(@Param("status") NotificationStatus status, 
                                                    @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetries")
    List<Notification> findFailedNotificationsForRetry();
    
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt IS NOT NULL AND n.scheduledAt <= :now AND n.status = 'SCHEDULED'")
    List<Notification> findScheduledNotificationsDue(@Param("now") LocalDateTime now);
    
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
    
    long countByStatusAndCreatedAtAfter(NotificationStatus status, LocalDateTime fromDate);
}