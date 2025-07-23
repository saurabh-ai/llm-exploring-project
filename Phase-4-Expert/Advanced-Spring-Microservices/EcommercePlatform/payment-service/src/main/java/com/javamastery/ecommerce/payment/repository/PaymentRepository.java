package com.javamastery.ecommerce.payment.repository;

import com.javamastery.ecommerce.payment.entity.Payment;
import com.javamastery.ecommerce.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt < CURRENT_TIMESTAMP - INTERVAL :minutes MINUTE")
    List<Payment> findPendingPaymentsOlderThan(@Param("status") PaymentStatus status, @Param("minutes") int minutes);
    
    boolean existsByOrderId(Long orderId);
}