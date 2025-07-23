package com.javamastery.ecommerce.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${notification.email.simulation-mode:true}")
    private boolean simulationMode;
    
    @Value("${notification.email.from-address:notifications@ecommerce.com}")
    private String fromAddress;
    
    public boolean sendEmail(String to, String subject, String content) {
        if (simulationMode) {
            return simulateEmailSending(to, subject, content);
        } else {
            return sendRealEmail(to, subject, content);
        }
    }
    
    private boolean simulateEmailSending(String to, String subject, String content) {
        logger.info("SIMULATING EMAIL SEND:");
        logger.info("From: {}", fromAddress);
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Content: {}", content);
        logger.info("--- END EMAIL SIMULATION ---");
        
        // Simulate occasional failures for testing
        if (Math.random() < 0.05) { // 5% failure rate
            logger.error("Simulated email sending failure");
            return false;
        }
        
        return true;
    }
    
    private boolean sendRealEmail(String to, String subject, String content) {
        // This would integrate with actual email service (JavaMail, SendGrid, etc.)
        logger.info("Real email sending not implemented yet. Use simulation mode.");
        return false;
    }
}