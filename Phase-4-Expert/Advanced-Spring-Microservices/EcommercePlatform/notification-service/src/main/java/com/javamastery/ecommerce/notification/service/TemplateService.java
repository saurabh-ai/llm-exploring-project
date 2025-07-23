package com.javamastery.ecommerce.notification.service;

import com.javamastery.ecommerce.notification.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    
    public String processSubjectTemplate(NotificationType type, Map<String, Object> templateData) {
        return switch (type) {
            case ORDER_CONFIRMATION -> "Order Confirmation - Order #" + templateData.get("orderId");
            case PAYMENT_CONFIRMATION -> "Payment Confirmed - Payment #" + templateData.get("paymentId");
            case PAYMENT_FAILED -> "Payment Failed - Payment #" + templateData.get("paymentId");
            case PAYMENT_REFUNDED -> "Payment Refunded - Payment #" + templateData.get("paymentId");
            case ORDER_SHIPPED -> "Order Shipped - Order #" + templateData.get("orderId");
            case ORDER_DELIVERED -> "Order Delivered - Order #" + templateData.get("orderId");
            default -> "Notification from E-commerce Platform";
        };
    }
    
    public String processContentTemplate(NotificationType type, Map<String, Object> templateData) {
        return switch (type) {
            case ORDER_CONFIRMATION -> buildOrderConfirmationContent(templateData);
            case PAYMENT_CONFIRMATION -> buildPaymentConfirmationContent(templateData);
            case PAYMENT_FAILED -> buildPaymentFailedContent(templateData);
            case PAYMENT_REFUNDED -> buildPaymentRefundedContent(templateData);
            case ORDER_SHIPPED -> buildOrderShippedContent(templateData);
            case ORDER_DELIVERED -> buildOrderDeliveredContent(templateData);
            default -> "You have a new notification from E-commerce Platform.";
        };
    }
    
    private String buildOrderConfirmationContent(Map<String, Object> data) {
        return String.format(
            "Dear Customer,\n\n" +
            "Thank you for your order! Your order #%s has been confirmed and is being processed.\n\n" +
            "Order Details:\n" +
            "- Order ID: %s\n" +
            "- Order Date: %s\n\n" +
            "You will receive another notification when your order ships.\n\n" +
            "Thank you for shopping with us!\n\n" +
            "Best regards,\n" +
            "E-commerce Platform Team",
            data.get("orderId"),
            data.get("orderId"),
            data.getOrDefault("orderDate", "Today")
        );
    }
    
    private String buildPaymentConfirmationContent(Map<String, Object> data) {
        return String.format(
            "Dear Customer,\n\n" +
            "Your payment has been processed successfully.\n\n" +
            "Payment Details:\n" +
            "- Payment ID: %s\n" +
            "- Order ID: %s\n" +
            "- Amount: %s\n" +
            "- Status: Completed\n\n" +
            "Thank you for your payment!\n\n" +
            "Best regards,\n" +
            "E-commerce Platform Team",
            data.get("paymentId"),
            data.get("orderId"),
            data.getOrDefault("amount", "N/A")
        );
    }
    
    private String buildPaymentFailedContent(Map<String, Object> data) {
        return String.format(
            "Dear Customer,\n\n" +
            "We were unable to process your payment. Please check your payment information and try again.\n\n" +
            "Payment Details:\n" +
            "- Payment ID: %s\n" +
            "- Order ID: %s\n" +
            "- Status: Failed\n\n" +
            "Please update your payment method or contact customer support if you need assistance.\n\n" +
            "Best regards,\n" +
            "E-commerce Platform Team",
            data.get("paymentId"),
            data.get("orderId")
        );
    }
    
    private String buildPaymentRefundedContent(Map<String, Object> data) {
        return String.format(
            "Dear Customer,\n\n" +
            "Your payment has been refunded successfully.\n\n" +
            "Refund Details:\n" +
            "- Payment ID: %s\n" +
            "- Order ID: %s\n" +
            "- Amount: %s\n" +
            "- Status: Refunded\n\n" +
            "The refund will appear in your account within 3-5 business days.\n\n" +
            "Best regards,\n" +
            "E-commerce Platform Team",
            data.get("paymentId"),
            data.get("orderId"),
            data.getOrDefault("amount", "N/A")
        );
    }
    
    private String buildOrderShippedContent(Map<String, Object> data) {
        return String.format(
            "Dear Customer,\n\n" +
            "Great news! Your order has been shipped.\n\n" +
            "Shipping Details:\n" +
            "- Order ID: %s\n" +
            "- Tracking Number: %s\n" +
            "- Estimated Delivery: %s\n\n" +
            "You can track your package using the tracking number above.\n\n" +
            "Best regards,\n" +
            "E-commerce Platform Team",
            data.get("orderId"),
            data.getOrDefault("trackingNumber", "TBD"),
            data.getOrDefault("estimatedDelivery", "3-5 business days")
        );
    }
    
    private String buildOrderDeliveredContent(Map<String, Object> data) {
        return String.format(
            "Dear Customer,\n\n" +
            "Your order has been delivered successfully!\n\n" +
            "Delivery Details:\n" +
            "- Order ID: %s\n" +
            "- Delivered On: %s\n" +
            "- Delivered To: %s\n\n" +
            "We hope you enjoy your purchase! Please leave a review if you're satisfied with your order.\n\n" +
            "Best regards,\n" +
            "E-commerce Platform Team",
            data.get("orderId"),
            data.getOrDefault("deliveryDate", "Today"),
            data.getOrDefault("deliveryAddress", "Your address")
        );
    }
}