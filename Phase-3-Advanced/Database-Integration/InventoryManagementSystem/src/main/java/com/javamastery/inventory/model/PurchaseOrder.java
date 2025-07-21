package com.javamastery.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Purchase Order entity representing orders placed with suppliers
 */
public class PurchaseOrder {
    private Long poId;
    private Long supplierId;
    private String poNumber;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private LocalDate receivedDate;
    private String notes;
    private LocalDateTime createdAt;
    
    // Optional fields for joins
    private String supplierName;
    private List<PurchaseOrderItem> items = new ArrayList<>();
    
    // Constructors
    public PurchaseOrder() {}
    
    public PurchaseOrder(Long supplierId, String poNumber, LocalDate orderDate, LocalDate expectedDate) {
        this.supplierId = supplierId;
        this.poNumber = poNumber;
        this.orderDate = orderDate;
        this.expectedDate = expectedDate;
        this.status = PurchaseOrderStatus.DRAFT;
        this.totalAmount = BigDecimal.ZERO;
    }
    
    public PurchaseOrder(Long poId, Long supplierId, String poNumber, PurchaseOrderStatus status,
                        BigDecimal totalAmount, LocalDate orderDate, LocalDate expectedDate,
                        LocalDate receivedDate, String notes, LocalDateTime createdAt) {
        this.poId = poId;
        this.supplierId = supplierId;
        this.poNumber = poNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.expectedDate = expectedDate;
        this.receivedDate = receivedDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getPoId() {
        return poId;
    }
    
    public void setPoId(Long poId) {
        this.poId = poId;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getPoNumber() {
        return poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    public PurchaseOrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(PurchaseOrderStatus status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDate getExpectedDate() {
        return expectedDate;
    }
    
    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }
    
    public LocalDate getReceivedDate() {
        return receivedDate;
    }
    
    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public List<PurchaseOrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
    
    // Business methods
    public void addItem(PurchaseOrderItem item) {
        if (item != null) {
            items.add(item);
            recalculateTotal();
        }
    }
    
    public void removeItem(PurchaseOrderItem item) {
        if (items.remove(item)) {
            recalculateTotal();
        }
    }
    
    public void recalculateTotal() {
        totalAmount = items.stream()
                .map(PurchaseOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean canChangeStatus(PurchaseOrderStatus newStatus) {
        return status != null && status.canTransitionTo(newStatus);
    }
    
    public boolean isEditable() {
        return status != null && status.isEditable();
    }
    
    public boolean isActive() {
        return status != null && status.isActive();
    }
    
    public boolean isOverdue() {
        return expectedDate != null && 
               LocalDate.now().isAfter(expectedDate) && 
               status != PurchaseOrderStatus.RECEIVED;
    }
    
    public int getTotalItemsOrdered() {
        return items.stream()
                .mapToInt(PurchaseOrderItem::getQuantityOrdered)
                .sum();
    }
    
    public int getTotalItemsReceived() {
        return items.stream()
                .mapToInt(PurchaseOrderItem::getQuantityReceived)
                .sum();
    }
    
    public boolean isFullyReceived() {
        return !items.isEmpty() && 
               items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
    }
    
    public boolean isPartiallyReceived() {
        return items.stream().anyMatch(item -> item.getQuantityReceived() > 0) &&
               !isFullyReceived();
    }
    
    // Validation methods
    public boolean isValid() {
        return supplierId != null &&
               poNumber != null && !poNumber.trim().isEmpty() &&
               orderDate != null &&
               status != null &&
               totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return Objects.equals(poId, that.poId) &&
               Objects.equals(poNumber, that.poNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(poId, poNumber);
    }
    
    @Override
    public String toString() {
        return "PurchaseOrder{" +
               "poId=" + poId +
               ", poNumber='" + poNumber + '\'' +
               ", status=" + status +
               ", totalAmount=" + totalAmount +
               ", orderDate=" + orderDate +
               ", expectedDate=" + expectedDate +
               ", itemCount=" + items.size() +
               '}';
    }
}