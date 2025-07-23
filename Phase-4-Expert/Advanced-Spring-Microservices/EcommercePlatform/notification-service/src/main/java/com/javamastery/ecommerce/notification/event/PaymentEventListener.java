package com.javamastery.ecommerce.notification.event;

import com.javamastery.ecommerce.notification.dto.NotificationRequest;
import com.javamastery.ecommerce.notification.entity.NotificationChannel;
import com.javamastery.ecommerce.notification.entity.NotificationType;
import com.javamastery.ecommerce.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @EventListener
    @Async
    public void handlePaymentEvent(PaymentEvent event) {
        logger.info("Received payment event for payment {} with status {}", 
                   event.getPaymentId(), event.getStatus());
        
        try {
            switch (event.getStatus()) {
                case COMPLETED:
                    sendPaymentConfirmationNotification(event);
                    break;
                case FAILED:
                    sendPaymentFailedNotification(event);
                    break;
                case REFUNDED:
                    sendPaymentRefundedNotification(event);
                    break;
                default:
                    logger.debug("No notification required for payment status: {}", event.getStatus());
            }
        } catch (Exception e) {
            logger.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }
    
    private void sendPaymentConfirmationNotification(PaymentEvent event) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(event.getUserId());
        request.setType(NotificationType.PAYMENT_CONFIRMATION);
        request.setChannel(NotificationChannel.EMAIL);
        request.setRecipient("user" + event.getUserId() + "@example.com"); // Would get from user service
        request.setReferenceId(event.getPaymentId().toString());
        request.setReferenceType("PAYMENT");
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("paymentId", event.getPaymentId());
        templateData.put("orderId", event.getOrderId());
        request.setTemplateData(templateData);
        
        notificationService.sendNotification(request);
    }
    
    private void sendPaymentFailedNotification(PaymentEvent event) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(event.getUserId());
        request.setType(NotificationType.PAYMENT_FAILED);
        request.setChannel(NotificationChannel.EMAIL);
        request.setRecipient("user" + event.getUserId() + "@example.com");
        request.setReferenceId(event.getPaymentId().toString());
        request.setReferenceType("PAYMENT");
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("paymentId", event.getPaymentId());
        templateData.put("orderId", event.getOrderId());
        request.setTemplateData(templateData);
        
        notificationService.sendNotification(request);
    }
    
    private void sendPaymentRefundedNotification(PaymentEvent event) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(event.getUserId());
        request.setType(NotificationType.PAYMENT_REFUNDED);
        request.setChannel(NotificationChannel.EMAIL);
        request.setRecipient("user" + event.getUserId() + "@example.com");
        request.setReferenceId(event.getPaymentId().toString());
        request.setReferenceType("PAYMENT");
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("paymentId", event.getPaymentId());
        templateData.put("orderId", event.getOrderId());
        request.setTemplateData(templateData);
        
        notificationService.sendNotification(request);
    }
}