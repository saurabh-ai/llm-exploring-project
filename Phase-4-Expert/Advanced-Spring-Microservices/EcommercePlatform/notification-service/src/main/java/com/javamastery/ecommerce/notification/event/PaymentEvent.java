package com.javamastery.ecommerce.notification.event;

public class PaymentEvent {
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
    
    public Long getPaymentId() {
        return paymentId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED
    }
}