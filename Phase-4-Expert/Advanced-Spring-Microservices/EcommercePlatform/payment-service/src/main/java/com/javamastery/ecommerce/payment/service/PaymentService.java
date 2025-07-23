package com.javamastery.ecommerce.payment.service;

import com.javamastery.ecommerce.payment.client.OrderServiceClient;
import com.javamastery.ecommerce.payment.dto.PaymentRequest;
import com.javamastery.ecommerce.payment.dto.PaymentResponse;
import com.javamastery.ecommerce.payment.entity.Payment;
import com.javamastery.ecommerce.payment.entity.PaymentStatus;
import com.javamastery.ecommerce.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Value("${payment.processing.delay-ms:1000}")
    private long processingDelay;
    
    @Value("${payment.processing.failure-rate:0.1}")
    private double failureRate;
    
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment for order {} with amount {}", request.getOrderId(), request.getAmount());
        
        // Validate payment request
        validatePaymentRequest(request);
        
        // Create payment record
        Payment payment = new Payment(request.getOrderId(), request.getUserId(), 
                                    request.getAmount(), request.getMethod());
        payment.setTransactionId(generateTransactionId());
        payment.setStatus(PaymentStatus.PROCESSING);
        
        payment = paymentRepository.save(payment);
        
        // Simulate payment processing
        PaymentStatus finalStatus = simulatePaymentProcessing(payment);
        payment.setStatus(finalStatus);
        payment = paymentRepository.save(payment);
        
        // Update order status
        updateOrderPaymentStatus(payment.getOrderId(), finalStatus);
        
        // Publish payment event
        publishPaymentEvent(payment);
        
        return mapToResponse(payment);
    }
    
    private void validatePaymentRequest(PaymentRequest request) {
        // Check if payment already exists for this order
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalStateException("Payment already exists for order: " + request.getOrderId());
        }
        
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
    
    private PaymentStatus simulatePaymentProcessing(Payment payment) {
        try {
            logger.info("Simulating payment processing for transaction {}", payment.getTransactionId());
            Thread.sleep(processingDelay);
            
            // Simulate random failures
            if (ThreadLocalRandom.current().nextDouble() < failureRate) {
                logger.warn("Payment processing failed for transaction {}", payment.getTransactionId());
                payment.setPaymentGatewayResponse("Payment declined by gateway");
                return PaymentStatus.FAILED;
            }
            
            logger.info("Payment processed successfully for transaction {}", payment.getTransactionId());
            payment.setPaymentGatewayResponse("Payment approved");
            return PaymentStatus.COMPLETED;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Payment processing interrupted for transaction {}", payment.getTransactionId());
            return PaymentStatus.FAILED;
        }
    }
    
    @CircuitBreaker(name = "order-service", fallbackMethod = "fallbackUpdateOrderStatus")
    private void updateOrderPaymentStatus(Long orderId, PaymentStatus status) {
        String orderStatus = status == PaymentStatus.COMPLETED ? "PAID" : "PAYMENT_FAILED";
        orderServiceClient.updatePaymentStatus(orderId, orderStatus);
        logger.info("Updated order {} payment status to {}", orderId, orderStatus);
    }
    
    private void fallbackUpdateOrderStatus(Long orderId, PaymentStatus status, Exception ex) {
        logger.error("Failed to update order {} status due to: {}", orderId, ex.getMessage());
        // Could implement retry logic or store for later processing
    }
    
    private void publishPaymentEvent(Payment payment) {
        PaymentEvent event = new PaymentEvent(payment.getId(), payment.getOrderId(), 
                                            payment.getUserId(), payment.getStatus());
        eventPublisher.publishEvent(event);
        logger.info("Published payment event for payment {}", payment.getId());
    }
    
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        
        // Update order status
        updateOrderPaymentStatus(payment.getOrderId(), PaymentStatus.REFUNDED);
        
        // Publish refund event
        publishPaymentEvent(payment);
        
        logger.info("Refunded payment {}", paymentId);
        return mapToResponse(payment);
    }
    
    public Optional<PaymentResponse> getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::mapToResponse);
    }
    
    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getMethod(),
            payment.getTransactionId(),
            payment.getCreatedAt(),
            payment.getProcessedAt()
        );
    }
    
    // Inner class for payment events
    public static class PaymentEvent {
        private final Long paymentId;
        private final Long orderId;
        private final Long userId;
        private final PaymentStatus status;
        
        public PaymentEvent(Long paymentId, Long orderId, Long userId, PaymentStatus status) {
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.userId = userId;
            this.status = status;
        }
        
        // Getters
        public Long getPaymentId() { return paymentId; }
        public Long getOrderId() { return orderId; }
        public Long getUserId() { return userId; }
        public PaymentStatus getStatus() { return status; }
    }
}