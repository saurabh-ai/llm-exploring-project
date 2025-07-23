package com.javamastery.ecommerce.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", fallback = OrderServiceFallback.class)
public interface OrderServiceClient {
    
    @GetMapping("/api/orders/{orderId}")
    OrderDto getOrder(@PathVariable Long orderId);
    
    @PutMapping("/api/orders/{orderId}/payment-status")
    void updatePaymentStatus(@PathVariable Long orderId, @RequestParam String status);
    
    // Simple DTO for order information
    class OrderDto {
        private Long id;
        private Long userId;
        private String status;
        private Double totalAmount;
        
        public OrderDto() {}
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Double getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}