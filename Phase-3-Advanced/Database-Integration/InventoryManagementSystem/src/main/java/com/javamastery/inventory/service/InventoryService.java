package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.InventoryDao;
import com.javamastery.inventory.dao.ProductDao;
import com.javamastery.inventory.dao.StockMovementDao;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing inventory with business logic and stock movement tracking
 */
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    
    private final InventoryDao inventoryDao;
    private final ProductDao productDao;
    private final StockMovementDao stockMovementDao;
    
    public InventoryService() {
        this.inventoryDao = new InventoryDao();
        this.productDao = new ProductDao();
        this.stockMovementDao = new StockMovementDao();
    }
    
    public InventoryService(InventoryDao inventoryDao, ProductDao productDao, StockMovementDao stockMovementDao) {
        this.inventoryDao = inventoryDao;
        this.productDao = productDao;
        this.stockMovementDao = stockMovementDao;
    }
    
    /**
     * Get all inventory items with product information
     */
    public List<Inventory> getAllInventory() {
        return inventoryDao.findAllWithProductInfo();
    }
    
    /**
     * Get inventory for a specific product
     */
    public Optional<Inventory> getInventoryByProduct(Long productId) {
        return inventoryDao.findByProductId(productId);
    }
    
    /**
     * Get inventory by product and location
     */
    public Optional<Inventory> getInventoryByProductAndLocation(Long productId, String location) {
        return inventoryDao.findByProductIdAndLocation(productId, location);
    }
    
    /**
     * Get inventory by location
     */
    public List<Inventory> getInventoryByLocation(String location) {
        return inventoryDao.findByLocation(location);
    }
    
    /**
     * Get low stock items
     */
    public List<Inventory> getLowStockItems() {
        return inventoryDao.findLowStockItems();
    }
    
    /**
     * Get out of stock items
     */
    public List<Inventory> getOutOfStockItems() {
        return inventoryDao.findOutOfStockItems();
    }
    
    /**
     * Receive stock (incoming inventory)
     */
    public void receiveStock(Long productId, String location, int quantity, 
                            ReferenceType referenceType, Long referenceId, 
                            String notes, String user) {
        validateStockOperation(productId, location, quantity, user);
        
        if (quantity <= 0) {
            throw new ValidationException("Receive quantity must be positive");
        }
        
        // Get or create inventory record
        Optional<Inventory> existingInventory = inventoryDao.findByProductIdAndLocation(productId, location);
        Inventory inventory;
        
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
            inventoryDao.update(inventory);
        } else {
            inventory = new Inventory(productId, quantity, 0, location);
            inventoryDao.insert(inventory);
        }
        
        // Record stock movement
        StockMovement movement = new StockMovement(
            productId, MovementType.IN, quantity, referenceType, referenceId, notes, user
        );
        stockMovementDao.insert(movement);
        
        logger.info("Received {} units of product {} at location {} by user {}", 
                   quantity, productId, location, user);
    }
    
    /**
     * Issue stock (outgoing inventory)
     */
    public void issueStock(Long productId, String location, int quantity,
                          ReferenceType referenceType, Long referenceId,
                          String notes, String user) {
        validateStockOperation(productId, location, quantity, user);
        
        if (quantity <= 0) {
            throw new ValidationException("Issue quantity must be positive");
        }
        
        Optional<Inventory> existingInventory = inventoryDao.findByProductIdAndLocation(productId, location);
        if (existingInventory.isEmpty()) {
            throw new ValidationException("No inventory found for product at location");
        }
        
        Inventory inventory = existingInventory.get();
        int availableQuantity = inventory.getQuantityAvailable();
        
        if (availableQuantity < quantity) {
            throw new ValidationException("Insufficient stock available. Available: " + 
                                        availableQuantity + ", Requested: " + quantity);
        }
        
        inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
        inventoryDao.update(inventory);
        
        // Record stock movement
        StockMovement movement = new StockMovement(
            productId, MovementType.OUT, -quantity, referenceType, referenceId, notes, user
        );
        stockMovementDao.insert(movement);
        
        logger.info("Issued {} units of product {} from location {} by user {}", 
                   quantity, productId, location, user);
    }
    
    /**
     * Adjust stock (manual adjustment for discrepancies)
     */
    public void adjustStock(Long productId, String location, int newQuantity,
                           String reason, String user) {
        validateStockOperation(productId, location, 1, user);
        
        if (newQuantity < 0) {
            throw new ValidationException("Adjusted quantity cannot be negative");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Reason for stock adjustment is required");
        }
        
        Optional<Inventory> existingInventory = inventoryDao.findByProductIdAndLocation(productId, location);
        Inventory inventory;
        int currentQuantity = 0;
        
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            currentQuantity = inventory.getQuantityOnHand();
            inventory.setQuantityOnHand(newQuantity);
            inventoryDao.update(inventory);
        } else {
            inventory = new Inventory(productId, newQuantity, 0, location);
            inventoryDao.insert(inventory);
        }
        
        int adjustmentQuantity = newQuantity - currentQuantity;
        
        if (adjustmentQuantity != 0) {
            // Record stock movement
            StockMovement movement = new StockMovement(
                productId, MovementType.ADJUSTMENT, adjustmentQuantity, 
                ReferenceType.ADJUSTMENT, null, reason, user
            );
            stockMovementDao.insert(movement);
            
            logger.info("Adjusted stock for product {} at location {} from {} to {} by user {}. Reason: {}", 
                       productId, location, currentQuantity, newQuantity, user, reason);
        }
    }
    
    /**
     * Allocate stock for an order
     */
    public boolean allocateStock(Long productId, String location, int quantity, String user) {
        validateStockOperation(productId, location, quantity, user);
        
        if (quantity <= 0) {
            throw new ValidationException("Allocation quantity must be positive");
        }
        
        boolean allocated = inventoryDao.allocateStock(productId, location, quantity);
        
        if (allocated) {
            logger.info("Allocated {} units of product {} at location {} by user {}", 
                       quantity, productId, location, user);
        } else {
            logger.warn("Failed to allocate {} units of product {} at location {} - insufficient stock", 
                       quantity, productId, location);
        }
        
        return allocated;
    }
    
    /**
     * Deallocate stock (release allocation)
     */
    public boolean deallocateStock(Long productId, String location, int quantity, String user) {
        validateStockOperation(productId, location, quantity, user);
        
        if (quantity <= 0) {
            throw new ValidationException("Deallocation quantity must be positive");
        }
        
        boolean deallocated = inventoryDao.deallocateStock(productId, location, quantity);
        
        if (deallocated) {
            logger.info("Deallocated {} units of product {} at location {} by user {}", 
                       quantity, productId, location, user);
        } else {
            logger.warn("Failed to deallocate {} units of product {} at location {} - insufficient allocation", 
                       quantity, productId, location);
        }
        
        return deallocated;
    }
    
    /**
     * Transfer stock between locations
     */
    public void transferStock(Long productId, String fromLocation, String toLocation, 
                             int quantity, String notes, String user) {
        validateStockOperation(productId, fromLocation, quantity, user);
        validateLocation(toLocation);
        
        if (quantity <= 0) {
            throw new ValidationException("Transfer quantity must be positive");
        }
        
        if (fromLocation.equals(toLocation)) {
            throw new ValidationException("Source and destination locations cannot be the same");
        }
        
        // Check available stock at source location
        Optional<Inventory> sourceInventory = inventoryDao.findByProductIdAndLocation(productId, fromLocation);
        if (sourceInventory.isEmpty()) {
            throw new ValidationException("No inventory found for product at source location");
        }
        
        int availableQuantity = sourceInventory.get().getQuantityAvailable();
        if (availableQuantity < quantity) {
            throw new ValidationException("Insufficient stock available for transfer. Available: " + 
                                        availableQuantity + ", Requested: " + quantity);
        }
        
        // Perform transfer
        // Remove from source location
        Inventory source = sourceInventory.get();
        source.setQuantityOnHand(source.getQuantityOnHand() - quantity);
        inventoryDao.update(source);
        
        // Add to destination location
        Optional<Inventory> destInventory = inventoryDao.findByProductIdAndLocation(productId, toLocation);
        if (destInventory.isPresent()) {
            Inventory dest = destInventory.get();
            dest.setQuantityOnHand(dest.getQuantityOnHand() + quantity);
            inventoryDao.update(dest);
        } else {
            Inventory dest = new Inventory(productId, quantity, 0, toLocation);
            inventoryDao.insert(dest);
        }
        
        // Record stock movements
        StockMovement outMovement = new StockMovement(
            productId, MovementType.TRANSFER, -quantity, ReferenceType.TRANSFER, 
            null, "Transfer to " + toLocation + ": " + notes, user
        );
        stockMovementDao.insert(outMovement);
        
        StockMovement inMovement = new StockMovement(
            productId, MovementType.TRANSFER, quantity, ReferenceType.TRANSFER, 
            null, "Transfer from " + fromLocation + ": " + notes, user
        );
        stockMovementDao.insert(inMovement);
        
        logger.info("Transferred {} units of product {} from {} to {} by user {}", 
                   quantity, productId, fromLocation, toLocation, user);
    }
    
    /**
     * Get stock movement history for a product
     */
    public List<StockMovement> getStockMovementHistory(Long productId) {
        return stockMovementDao.findByProductId(productId);
    }
    
    /**
     * Get stock movement history for a date range
     */
    public List<StockMovement> getStockMovementHistory(java.time.LocalDateTime startDate, 
                                                      java.time.LocalDateTime endDate) {
        return stockMovementDao.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get all distinct locations
     */
    public List<String> getAllLocations() {
        return inventoryDao.findAllLocations();
    }
    
    /**
     * Get total quantity on hand for a product across all locations
     */
    public int getTotalQuantityOnHand(Long productId) {
        return inventoryDao.getTotalQuantityOnHand(productId);
    }
    
    /**
     * Get total available quantity for a product across all locations
     */
    public int getTotalAvailableQuantity(Long productId) {
        return inventoryDao.getTotalAvailableQuantity(productId);
    }
    
    /**
     * Validate stock operation parameters
     */
    private void validateStockOperation(Long productId, String location, int quantity, String user) {
        if (productId == null) {
            throw new ValidationException("Product ID is required");
        }
        
        if (!productDao.existsById(productId)) {
            throw new ValidationException("Product with ID " + productId + " does not exist");
        }
        
        validateLocation(location);
        
        if (user == null || user.trim().isEmpty()) {
            throw new ValidationException("User is required for stock operations");
        }
    }
    
    /**
     * Validate location
     */
    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Location is required");
        }
        
        if (location.trim().length() > 100) {
            throw new ValidationException("Location must be 100 characters or less");
        }
    }
    
    /**
     * Get inventory summary
     */
    public InventorySummary getInventorySummary() {
        List<Inventory> allInventory = inventoryDao.findAllWithProductInfo();
        List<Inventory> lowStockItems = inventoryDao.findLowStockItems();
        List<Inventory> outOfStockItems = inventoryDao.findOutOfStockItems();
        
        return new InventorySummary(allInventory, lowStockItems, outOfStockItems);
    }
    
    /**
     * Inner class for inventory summary
     */
    public static class InventorySummary {
        private final int totalProducts;
        private final int totalQuantityOnHand;
        private final int lowStockCount;
        private final int outOfStockCount;
        private final List<Inventory> lowStockItems;
        private final List<Inventory> outOfStockItems;
        
        public InventorySummary(List<Inventory> allInventory, List<Inventory> lowStockItems, 
                               List<Inventory> outOfStockItems) {
            this.totalProducts = allInventory.size();
            this.totalQuantityOnHand = allInventory.stream()
                .mapToInt(Inventory::getQuantityOnHand)
                .sum();
            this.lowStockCount = lowStockItems.size();
            this.outOfStockCount = outOfStockItems.size();
            this.lowStockItems = lowStockItems;
            this.outOfStockItems = outOfStockItems;
        }
        
        // Getters
        public int getTotalProducts() { return totalProducts; }
        public int getTotalQuantityOnHand() { return totalQuantityOnHand; }
        public int getLowStockCount() { return lowStockCount; }
        public int getOutOfStockCount() { return outOfStockCount; }
        public List<Inventory> getLowStockItems() { return lowStockItems; }
        public List<Inventory> getOutOfStockItems() { return outOfStockItems; }
        
        @Override
        public String toString() {
            return "InventorySummary{" +
                   "totalProducts=" + totalProducts +
                   ", totalQuantityOnHand=" + totalQuantityOnHand +
                   ", lowStockCount=" + lowStockCount +
                   ", outOfStockCount=" + outOfStockCount +
                   '}';
        }
    }
}