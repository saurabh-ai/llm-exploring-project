package com.javamastery.ecommerce.payment.service;

import com.javamastery.ecommerce.payment.client.OrderServiceClient;
import com.javamastery.ecommerce.payment.dto.PaymentRequest;
import com.javamastery.ecommerce.payment.dto.PaymentResponse;
import com.javamastery.ecommerce.payment.entity.Payment;
import com.javamastery.ecommerce.payment.entity.PaymentMethod;
import com.javamastery.ecommerce.payment.entity.PaymentStatus;
import com.javamastery.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private OrderServiceClient orderServiceClient;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private PaymentService paymentService;
    
    private PaymentRequest paymentRequest;
    private Payment payment;
    
    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setUserId(1L);
        paymentRequest.setAmount(new BigDecimal("99.99"));
        paymentRequest.setMethod(PaymentMethod.CREDIT_CARD);
        
        payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setUserId(1L);
        payment.setAmount(new BigDecimal("99.99"));
        payment.setMethod(PaymentMethod.CREDIT_CARD);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("TXN-123456");
        payment.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void testProcessPayment_Success() {
        // Given
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doNothing().when(orderServiceClient).updatePaymentStatus(anyLong(), anyString());
        
        // When
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getOrderId());
        assertEquals(new BigDecimal("99.99"), response.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, response.getMethod());
        
        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(eventPublisher).publishEvent(any(PaymentService.PaymentEvent.class));
        verify(orderServiceClient).updatePaymentStatus(anyLong(), anyString());
    }
    
    @Test
    void testProcessPayment_DuplicateOrder() {
        // Given
        when(paymentRepository.existsByOrderId(1L)).thenReturn(true);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });
        
        verify(paymentRepository, never()).save(any(Payment.class));
    }
    
    @Test
    void testGetPayment_Found() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        
        // When
        Optional<PaymentResponse> response = paymentService.getPayment(1L);
        
        // Then
        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getId());
        assertEquals(PaymentStatus.COMPLETED, response.get().getStatus());
    }
    
    @Test
    void testGetPayment_NotFound() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<PaymentResponse> response = paymentService.getPayment(1L);
        
        // Then
        assertFalse(response.isPresent());
    }
    
    @Test
    void testRefundPayment_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doNothing().when(orderServiceClient).updatePaymentStatus(anyLong(), anyString());
        
        // When
        PaymentResponse response = paymentService.refundPayment(1L);
        
        // Then
        assertNotNull(response);
        verify(paymentRepository).save(any(Payment.class));
        verify(eventPublisher).publishEvent(any(PaymentService.PaymentEvent.class));
        verify(orderServiceClient).updatePaymentStatus(anyLong(), anyString());
    }
    
    @Test
    void testRefundPayment_NotFound() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.refundPayment(1L);
        });
    }
}