package com.javamastery.ecommerce.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    
    @Value("${notification.sms.simulation-mode:true}")
    private boolean simulationMode;
    
    @Value("${notification.sms.provider:twilio}")
    private String smsProvider;
    
    public boolean sendSms(String phoneNumber, String message) {
        if (simulationMode) {
            return simulateSmsSending(phoneNumber, message);
        } else {
            return sendRealSms(phoneNumber, message);
        }
    }
    
    private boolean simulateSmsSending(String phoneNumber, String message) {
        logger.info("SIMULATING SMS SEND:");
        logger.info("Provider: {}", smsProvider);
        logger.info("To: {}", phoneNumber);
        logger.info("Message: {}", message);
        logger.info("--- END SMS SIMULATION ---");
        
        // Simulate occasional failures for testing
        if (Math.random() < 0.05) { // 5% failure rate
            logger.error("Simulated SMS sending failure");
            return false;
        }
        
        return true;
    }
    
    private boolean sendRealSms(String phoneNumber, String message) {
        // This would integrate with actual SMS service (Twilio, AWS SNS, etc.)
        logger.info("Real SMS sending not implemented yet. Use simulation mode.");
        return false;
    }
}