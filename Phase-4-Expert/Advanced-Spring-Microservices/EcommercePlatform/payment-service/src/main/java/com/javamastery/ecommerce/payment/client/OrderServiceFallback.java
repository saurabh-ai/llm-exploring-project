package com.javamastery.ecommerce.payment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceFallback implements OrderServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceFallback.class);
    
    @Override
    public OrderDto getOrder(Long orderId) {
        logger.warn("Fallback: Unable to get order {}, returning default order", orderId);
        OrderDto fallbackOrder = new OrderDto();
        fallbackOrder.setId(orderId);
        fallbackOrder.setStatus("UNKNOWN");
        return fallbackOrder;
    }
    
    @Override
    public void updatePaymentStatus(Long orderId, String status) {
        logger.warn("Fallback: Unable to update payment status for order {}, status: {}", orderId, status);
    }
}