package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.PurchaseOrderDao;
import com.javamastery.inventory.dao.SupplierDao;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing purchase orders with business logic and workflow management
 */
public class PurchaseOrderService {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);
    
    private final PurchaseOrderDao purchaseOrderDao;
    private final SupplierDao supplierDao;
    
    public PurchaseOrderService() {
        this.purchaseOrderDao = new PurchaseOrderDao();
        this.supplierDao = new SupplierDao();
    }
    
    public PurchaseOrderService(PurchaseOrderDao purchaseOrderDao, SupplierDao supplierDao) {
        this.purchaseOrderDao = purchaseOrderDao;
        this.supplierDao = supplierDao;
    }
    
    /**
     * Get all purchase orders with supplier information
     */
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderDao.findAllWithSupplier();
    }
    
    /**
     * Get purchase order by ID
     */
    public Optional<PurchaseOrder> getPurchaseOrderById(Long poId) {
        return purchaseOrderDao.findById(poId);
    }
    
    /**
     * Create a new purchase order
     */
    public PurchaseOrder createPurchaseOrder(Long supplierId, String notes) {
        validateSupplier(supplierId);
        
        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(supplierId);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setOrderDate(java.time.LocalDate.now());
        po.setTotalAmount(BigDecimal.ZERO);
        po.setNotes(notes != null ? notes : "");
        
        PurchaseOrder saved = purchaseOrderDao.insert(po);
        
        logger.info("Created new purchase order: PO-{} for supplier {}", 
                   saved.getPoId(), supplierId);
        
        return saved;
    }
    
    /**
     * Update purchase order status - implements workflow management
     */
    public PurchaseOrder updateStatus(Long poId, PurchaseOrderStatus newStatus) {
        Optional<PurchaseOrder> poOpt = purchaseOrderDao.findById(poId);
        if (poOpt.isEmpty()) {
            throw new ValidationException("Purchase order with ID " + poId + " not found");
        }
        
        PurchaseOrder po = poOpt.get();
        PurchaseOrderStatus currentStatus = po.getStatus();
        
        // Validate workflow transition
        validateStatusTransition(currentStatus, newStatus);
        
        po.setStatus(newStatus);
        PurchaseOrder updated = purchaseOrderDao.update(po);
        
        logger.info("Updated purchase order PO-{} status from {} to {}", 
                   poId, currentStatus, newStatus);
        
        return updated;
    }
    
    /**
     * Update purchase order total amount
     */
    public PurchaseOrder updateTotalAmount(Long poId, BigDecimal totalAmount) {
        Optional<PurchaseOrder> poOpt = purchaseOrderDao.findById(poId);
        if (poOpt.isEmpty()) {
            throw new ValidationException("Purchase order with ID " + poId + " not found");
        }
        
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Total amount cannot be negative");
        }
        
        PurchaseOrder po = poOpt.get();
        po.setTotalAmount(totalAmount);
        
        return purchaseOrderDao.update(po);
    }
    
    /**
     * Get purchase orders by status
     */
    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderDao.findByStatus(status);
    }
    
    /**
     * Get purchase orders by supplier
     */
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(Long supplierId) {
        validateSupplier(supplierId);
        return purchaseOrderDao.findBySupplier(supplierId);
    }
    
    /**
     * Complete purchase order workflow
     */
    public PurchaseOrder completePurchaseOrder(Long poId) {
        return updateStatus(poId, PurchaseOrderStatus.RECEIVED);
    }
    
    /**
     * Cancel purchase order
     */
    public PurchaseOrder cancelPurchaseOrder(Long poId) {
        return updateStatus(poId, PurchaseOrderStatus.CANCELLED);
    }
    
    /**
     * Get purchase order summary statistics
     */
    public PurchaseOrderSummary getPurchaseOrderSummary() {
        List<PurchaseOrder> allOrders = purchaseOrderDao.findAll();
        
        int pendingCount = 0;
        int orderedCount = 0;
        int receivedCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        
        for (PurchaseOrder po : allOrders) {
            switch (po.getStatus()) {
                case DRAFT -> pendingCount++;
                case SENT -> orderedCount++;
                case CONFIRMED -> receivedCount++;
                case RECEIVED -> completedCount++;
                case CANCELLED -> cancelledCount++;
            }
            if (po.getTotalAmount() != null) {
                totalValue = totalValue.add(po.getTotalAmount());
            }
        }
        
        return new PurchaseOrderSummary(
            allOrders.size(),
            pendingCount,
            orderedCount,
            receivedCount,
            completedCount,
            cancelledCount,
            totalValue
        );
    }
    
    /**
     * Validate status transition according to workflow rules
     */
    private void validateStatusTransition(PurchaseOrderStatus current, PurchaseOrderStatus target) {
        if (!current.canTransitionTo(target)) {
            throw new ValidationException(
                String.format("Invalid status transition from %s to %s", current, target)
            );
        }
    }
    
    /**
     * Validate supplier exists
     */
    private void validateSupplier(Long supplierId) {
        if (supplierId == null) {
            throw new ValidationException("Supplier ID is required");
        }
        
        if (!supplierDao.existsById(supplierId)) {
            throw new ValidationException("Supplier with ID " + supplierId + " does not exist");
        }
    }
    
    /**
     * Purchase order summary record
     */
    public record PurchaseOrderSummary(
        int totalOrders,
        int pendingOrders,
        int orderedOrders,
        int receivedOrders,
        int completedOrders,
        int cancelledOrders,
        BigDecimal totalValue
    ) {}
}