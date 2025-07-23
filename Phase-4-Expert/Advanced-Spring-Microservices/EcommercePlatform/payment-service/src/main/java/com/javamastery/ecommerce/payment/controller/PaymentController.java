package com.javamastery.ecommerce.payment.controller;

import com.javamastery.ecommerce.payment.dto.PaymentRequest;
import com.javamastery.ecommerce.payment.dto.PaymentResponse;
import com.javamastery.ecommerce.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "Payment processing and management operations")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    @Operation(summary = "Process a payment", description = "Process a new payment for an order")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        logger.info("Processing payment request for order {}", request.getOrderId());
        try {
            PaymentResponse response = paymentService.processPayment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            logger.error("Payment processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error processing payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details", description = "Retrieve payment details by payment ID")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        logger.info("Retrieving payment {}", paymentId);
        return paymentService.getPayment(paymentId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user payments", description = "Retrieve all payments for a specific user")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Retrieving payments for user {}", userId);
        List<PaymentResponse> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get order payments", description = "Retrieve all payments for a specific order")
    public ResponseEntity<List<PaymentResponse>> getOrderPayments(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        logger.info("Retrieving payments for order {}", orderId);
        List<PaymentResponse> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Process a refund for a completed payment")
    public ResponseEntity<PaymentResponse> refundPayment(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        logger.info("Processing refund for payment {}", paymentId);
        try {
            PaymentResponse response = paymentService.refundPayment(paymentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Refund failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            logger.error("Refund not allowed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error processing refund", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check payment service health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running");
    }
}