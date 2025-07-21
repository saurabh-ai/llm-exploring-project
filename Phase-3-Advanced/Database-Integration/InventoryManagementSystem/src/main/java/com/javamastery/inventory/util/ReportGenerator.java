package com.javamastery.inventory.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javamastery.inventory.model.Inventory;
import com.javamastery.inventory.model.Product;
import com.javamastery.inventory.model.StockMovement;
import com.javamastery.inventory.service.CategoryService;
import com.javamastery.inventory.service.InventoryService;
import com.javamastery.inventory.service.ProductService;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for generating comprehensive reports in various formats
 */
public class ReportGenerator {
    private final InventoryService inventoryService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;
    
    public ReportGenerator() {
        this.inventoryService = new InventoryService();
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
        
        // Configure JSON mapper for LocalDateTime handling
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Generate inventory valuation report in CSV format
     */
    public String generateInventoryValuationCSV() throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("Product Name,SKU,Category,Quantity On Hand,Unit Price,Total Value,Stock Status\n");
        
        List<Inventory> inventory = inventoryService.getAllInventory();
        BigDecimal totalValue = BigDecimal.ZERO;
        
        for (Inventory item : inventory) {
            Optional<Product> productOpt = productService.getProductById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal itemValue = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantityOnHand()));
                totalValue = totalValue.add(itemValue);
                
                csv.append(String.format("\"%s\",\"%s\",\"%s\",%d,%.2f,%.2f,\"%s\"\n",
                    escapeCSV(product.getName()),
                    product.getSku(),
                    escapeCSV(product.getCategoryName()),
                    item.getQuantityOnHand(),
                    product.getUnitPrice(),
                    itemValue,
                    item.getStockStatus()
                ));
            }
        }
        
        csv.append(String.format("\nTotal Inventory Value,,,,,,%.2f,\n", totalValue));
        return csv.toString();
    }
    
    /**
     * Generate inventory valuation report in JSON format
     */
    public String generateInventoryValuationJSON() throws IOException {
        List<Inventory> inventory = inventoryService.getAllInventory();
        
        List<InventoryValuationItem> items = inventory.stream()
            .map(item -> {
                Optional<Product> productOpt = productService.getProductById(item.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    BigDecimal itemValue = product.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantityOnHand()));
                        
                    return new InventoryValuationItem(
                        product.getName(),
                        product.getSku(),
                        product.getCategoryName(),
                        item.getQuantityOnHand(),
                        product.getUnitPrice(),
                        itemValue,
                        item.getStockStatus().toString()
                    );
                }
                return null;
            })
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toList());
            
        BigDecimal totalValue = items.stream()
            .map(InventoryValuationItem::totalValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        InventoryValuationReport report = new InventoryValuationReport(
            LocalDateTime.now(),
            items,
            totalValue,
            items.size()
        );
        
        return objectMapper.writeValueAsString(report);
    }
    
    /**
     * Generate low stock report in CSV format
     */
    public String generateLowStockCSV() throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("Product Name,SKU,Category,Quantity On Hand,Reorder Level,Stock Status,Shortage\n");
        
        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        
        for (Inventory item : lowStockItems) {
            Optional<Product> productOpt = productService.getProductById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                int shortage = item.getReorderLevel() - item.getQuantityOnHand();
                
                csv.append(String.format("\"%s\",\"%s\",\"%s\",%d,%d,\"%s\",%d\n",
                    escapeCSV(product.getName()),
                    product.getSku(),
                    escapeCSV(product.getCategoryName()),
                    item.getQuantityOnHand(),
                    item.getReorderLevel(),
                    item.getStockStatus(),
                    shortage
                ));
            }
        }
        
        return csv.toString();
    }
    
    /**
     * Generate stock movement report in CSV format
     */
    public String generateStockMovementCSV(LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Product Name,SKU,Movement Type,Quantity,Reference Type,Reference ID,Notes,User\n");
        
        List<StockMovement> movements;
        if (startDate != null && endDate != null) {
            movements = inventoryService.getStockMovementHistory(startDate, endDate);
        } else {
            movements = inventoryService.getAllStockMovements();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (StockMovement movement : movements) {
            Optional<Product> productOpt = productService.getProductById(movement.getProductId());
            String productName = productOpt.map(Product::getName).orElse("Unknown");
            String sku = productOpt.map(Product::getSku).orElse("Unknown");
            
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",%s,\"%s\",\"%s\"\n",
                movement.getCreatedAt().format(formatter),
                escapeCSV(productName),
                sku,
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getReferenceType(),
                movement.getReferenceId() != null ? movement.getReferenceId().toString() : "",
                escapeCSV(movement.getMovementDescription()),
                escapeCSV(movement.getCreatedBy())
            ));
        }
        
        return csv.toString();
    }
    
    /**
     * Generate supplier performance report in JSON format
     */
    public String generateSupplierPerformanceJSON() throws IOException {
        // This would require additional tracking data
        // For now, return a placeholder structure
        
        var report = Map.of(
            "reportDate", LocalDateTime.now(),
            "reportType", "Supplier Performance",
            "note", "Supplier performance tracking requires additional order history data",
            "suppliers", List.of()
        );
        
        return objectMapper.writeValueAsString(report);
    }
    
    /**
     * Save report to file
     */
    public void saveReportToFile(String content, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
    }
    
    /**
     * Generate comprehensive dashboard data in JSON format
     */
    public String generateDashboardJSON() throws IOException {
        InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
        List<Inventory> lowStock = inventoryService.getLowStockItems();
        List<Inventory> outOfStock = inventoryService.getOutOfStockItems();
        
        var dashboard = Map.of(
            "reportDate", LocalDateTime.now(),
            "summary", Map.of(
                "totalItems", summary.totalItems(),
                "totalStockValue", summary.totalStockValue(),
                "lowStockItems", summary.lowStockItems(),
                "outOfStockItems", summary.outOfStockItems(),
                "totalLocations", summary.totalLocations()
            ),
            "alerts", Map.of(
                "lowStockCount", lowStock.size(),
                "outOfStockCount", outOfStock.size(),
                "criticalItems", lowStock.stream()
                    .filter(item -> item.getQuantityOnHand() == 0)
                    .count()
            )
        );
        
        return objectMapper.writeValueAsString(dashboard);
    }
    
    // Helper methods
    private String escapeCSV(String input) {
        if (input == null) return "";
        // Escape quotes and handle commas
        return input.replace("\"", "\"\"");
    }
    
    // Report DTOs
    public record InventoryValuationItem(
        String productName,
        String sku,
        String category,
        int quantityOnHand,
        BigDecimal unitPrice,
        BigDecimal totalValue,
        String stockStatus
    ) {}
    
    public record InventoryValuationReport(
        LocalDateTime reportDate,
        List<InventoryValuationItem> items,
        BigDecimal totalValue,
        int totalItems
    ) {}
}