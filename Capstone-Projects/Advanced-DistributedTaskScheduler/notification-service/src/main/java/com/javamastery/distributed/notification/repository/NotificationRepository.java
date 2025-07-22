package com.javamastery.distributed.notification.repository;

import com.javamastery.distributed.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByStatus(String status);
    
    List<Notification> findByType(String type);
    
    List<Notification> findByRecipient(String recipient);
    
    List<Notification> findByStatusAndRetryCountLessThan(String status, Integer maxRetries);
    
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt <= :now AND n.status = 'PENDING'")
    List<Notification> findPendingNotificationsToSend(LocalDateTime now);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = :status")
    Long countByStatus(String status);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :startDate AND n.createdAt <= :endDate")
    List<Notification> findNotificationsInDateRange(LocalDateTime startDate, LocalDateTime endDate);
}