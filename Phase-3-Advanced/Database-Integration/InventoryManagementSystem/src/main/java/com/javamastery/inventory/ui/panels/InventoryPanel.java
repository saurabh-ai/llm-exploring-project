package com.javamastery.inventory.ui.panels;

import com.javamastery.inventory.model.Inventory;
import com.javamastery.inventory.service.InventoryService;
import com.javamastery.inventory.service.ProductService;
import com.javamastery.inventory.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Simple inventory management panel
 */
public class InventoryPanel extends JPanel implements MainWindow.RefreshablePanel {
    private static final Logger logger = LoggerFactory.getLogger(InventoryPanel.class);
    
    private final InventoryService inventoryService;
    private final ProductService productService;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    
    public InventoryPanel(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        
        initializeComponents();
        setupLayout();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All", "In Stock", "Low Stock", "Out of Stock"});
        
        // Create table
        String[] columnNames = {"Product", "SKU", "Location", "On Hand", "Allocated", "Available", "Reorder Level", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(100);
    }
    
    private void setupLayout() {
        // Top panel with filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Status:"));
        topPanel.add(statusFilter);
        
        JButton filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(e -> applyFilter());
        topPanel.add(filterButton);
        
        JButton clearButton = new JButton("Show All");
        clearButton.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            refreshData();
        });
        topPanel.add(clearButton);
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        
        JButton summaryButton = new JButton("Show Summary");
        summaryButton.addActionListener(e -> showInventorySummary());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(summaryButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void refreshData() {
        logger.info("Refreshing inventory data");
        
        try {
            List<Inventory> inventoryItems = inventoryService.getAllInventory();
            updateTable(inventoryItems);
            
        } catch (Exception e) {
            logger.error("Error refreshing inventory data", e);
            JOptionPane.showMessageDialog(this,
                "Error refreshing inventory: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyFilter() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        try {
            List<Inventory> inventoryItems;
            
            switch (selectedStatus) {
                case "Low Stock":
                    inventoryItems = inventoryService.getLowStockItems();
                    break;
                case "Out of Stock":
                    inventoryItems = inventoryService.getOutOfStockItems();
                    break;
                case "In Stock":
                    inventoryItems = inventoryService.getAllInventory().stream()
                        .filter(inv -> !inv.isOutOfStock() && !inv.isLowStock())
                        .toList();
                    break;
                default:
                    inventoryItems = inventoryService.getAllInventory();
                    break;
            }
            
            updateTable(inventoryItems);
            
        } catch (Exception e) {
            logger.error("Error applying filter", e);
            JOptionPane.showMessageDialog(this,
                "Error applying filter: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Inventory> inventoryItems) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add inventory items to table
        for (Inventory item : inventoryItems) {
            Object[] row = {
                item.getProductName() != null ? item.getProductName() : "N/A",
                item.getProductSku() != null ? item.getProductSku() : "N/A",
                item.getLocation(),
                item.getQuantityOnHand(),
                item.getQuantityAllocated(),
                item.getQuantityAvailable(),
                item.getReorderLevel() != null ? item.getReorderLevel() : "N/A",
                item.getStockStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showInventorySummary() {
        try {
            InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Inventory Summary", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            
            JTextArea summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            StringBuilder details = new StringBuilder();
            details.append("=== INVENTORY SUMMARY ===\n\n");
            details.append(String.format("Total Products: %d\n", summary.getTotalProducts()));
            details.append(String.format("Total Quantity On Hand: %,d units\n", summary.getTotalQuantityOnHand()));
            details.append(String.format("Low Stock Items: %d\n", summary.getLowStockCount()));
            details.append(String.format("Out of Stock Items: %d\n", summary.getOutOfStockCount()));
            details.append("\n=== STATUS BREAKDOWN ===\n");
            
            int inStock = summary.getTotalProducts() - summary.getLowStockCount() - summary.getOutOfStockCount();
            details.append(String.format("In Stock: %d items\n", inStock));
            details.append(String.format("Low Stock: %d items\n", summary.getLowStockCount()));
            details.append(String.format("Out of Stock: %d items\n", summary.getOutOfStockCount()));
            
            details.append("\n=== LOCATIONS ===\n");
            List<String> locations = inventoryService.getAllLocations();
            for (String location : locations) {
                details.append(String.format("- %s\n", location));
            }
            
            details.append(String.format("\nGenerated: %s\n", 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )));
            
            summaryArea.setText(details.toString());
            
            JScrollPane scrollPane = new JScrollPane(summaryArea);
            dialog.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
            
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            logger.error("Error showing inventory summary", e);
            JOptionPane.showMessageDialog(this,
                "Error showing summary: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}