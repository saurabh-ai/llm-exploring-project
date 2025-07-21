package com.javamastery.inventory.ui.panels;

import com.javamastery.inventory.model.Inventory;
import com.javamastery.inventory.model.Product;
import com.javamastery.inventory.service.InventoryService;
import com.javamastery.inventory.service.ProductService;
import com.javamastery.inventory.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Dashboard panel showing inventory overview and key metrics
 */
public class DashboardPanel extends JPanel implements MainWindow.RefreshablePanel {
    private static final Logger logger = LoggerFactory.getLogger(DashboardPanel.class);
    
    private final InventoryService inventoryService;
    private final ProductService productService;
    
    private JLabel totalProductsLabel;
    private JLabel totalStockLabel;
    private JLabel lowStockLabel;
    private JLabel outOfStockLabel;
    private JList<String> lowStockList;
    private JList<String> outOfStockList;
    private JTextArea summaryTextArea;
    
    public DashboardPanel(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        
        initializeComponents();
        setupLayout();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Metric labels
        totalProductsLabel = new JLabel("0");
        totalStockLabel = new JLabel("0");
        lowStockLabel = new JLabel("0");
        outOfStockLabel = new JLabel("0");
        
        // Lists for low stock and out of stock items
        lowStockList = new JList<>();
        lowStockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lowStockList.setVisibleRowCount(8);
        
        outOfStockList = new JList<>();
        outOfStockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outOfStockList.setVisibleRowCount(8);
        
        // Summary text area
        summaryTextArea = new JTextArea(10, 30);
        summaryTextArea.setEditable(false);
        summaryTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        summaryTextArea.setBackground(getBackground());
    }
    
    private void setupLayout() {
        // Top panel with key metrics
        JPanel metricsPanel = createMetricsPanel();
        
        // Center panel with lists
        JPanel centerPanel = createCenterPanel();
        
        // Bottom panel with summary
        JPanel summaryPanel = createSummaryPanel();
        
        add(metricsPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Key Metrics"));
        panel.setPreferredSize(new Dimension(0, 80));
        
        panel.add(createMetricCard("Total Products", totalProductsLabel, Color.BLUE));
        panel.add(createMetricCard("Total Stock", totalStockLabel, Color.GREEN));
        panel.add(createMetricCard("Low Stock Items", lowStockLabel, Color.ORANGE));
        panel.add(createMetricCard("Out of Stock", outOfStockLabel, Color.RED));
        
        return panel;
    }
    
    private JPanel createMetricCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEtchedBorder());
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        titleLabel.setForeground(color);
        
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Low stock panel
        JPanel lowStockPanel = new JPanel(new BorderLayout());
        lowStockPanel.setBorder(BorderFactory.createTitledBorder("Low Stock Items"));
        
        JScrollPane lowStockScroll = new JScrollPane(lowStockList);
        lowStockPanel.add(lowStockScroll, BorderLayout.CENTER);
        
        JButton refreshLowStockBtn = new JButton("Refresh");
        refreshLowStockBtn.addActionListener(e -> refreshLowStockData());
        lowStockPanel.add(refreshLowStockBtn, BorderLayout.SOUTH);
        
        // Out of stock panel
        JPanel outOfStockPanel = new JPanel(new BorderLayout());
        outOfStockPanel.setBorder(BorderFactory.createTitledBorder("Out of Stock Items"));
        
        JScrollPane outOfStockScroll = new JScrollPane(outOfStockList);
        outOfStockPanel.add(outOfStockScroll, BorderLayout.CENTER);
        
        JButton refreshOutOfStockBtn = new JButton("Refresh");
        refreshOutOfStockBtn.addActionListener(e -> refreshOutOfStockData());
        outOfStockPanel.add(refreshOutOfStockBtn, BorderLayout.SOUTH);
        
        panel.add(lowStockPanel);
        panel.add(outOfStockPanel);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Inventory Summary"));
        panel.setPreferredSize(new Dimension(0, 150));
        
        JScrollPane scrollPane = new JScrollPane(summaryTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshSummaryBtn = new JButton("Refresh Summary");
        refreshSummaryBtn.addActionListener(e -> refreshSummaryData());
        panel.add(refreshSummaryBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    @Override
    public void refreshData() {
        logger.info("Refreshing dashboard data");
        
        try {
            refreshMetrics();
            refreshLowStockData();
            refreshOutOfStockData();
            refreshSummaryData();
            
        } catch (Exception e) {
            logger.error("Error refreshing dashboard data", e);
            JOptionPane.showMessageDialog(this,
                "Error refreshing dashboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshMetrics() {
        InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
        
        totalProductsLabel.setText(String.valueOf(summary.getTotalProducts()));
        totalStockLabel.setText(String.valueOf(summary.getTotalQuantityOnHand()));
        lowStockLabel.setText(String.valueOf(summary.getLowStockCount()));
        outOfStockLabel.setText(String.valueOf(summary.getOutOfStockCount()));
    }
    
    private void refreshLowStockData() {
        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        DefaultListModel<String> model = new DefaultListModel<>();
        
        for (Inventory item : lowStockItems) {
            String text = String.format("%s (%s) - Available: %d, Reorder: %d",
                item.getProductName(),
                item.getProductSku(),
                item.getQuantityAvailable(),
                item.getReorderLevel()
            );
            model.addElement(text);
        }
        
        lowStockList.setModel(model);
    }
    
    private void refreshOutOfStockData() {
        List<Inventory> outOfStockItems = inventoryService.getOutOfStockItems();
        DefaultListModel<String> model = new DefaultListModel<>();
        
        for (Inventory item : outOfStockItems) {
            String text = String.format("%s (%s) - On Hand: %d, Allocated: %d",
                item.getProductName(),
                item.getProductSku(),
                item.getQuantityOnHand(),
                item.getQuantityAllocated()
            );
            model.addElement(text);
        }
        
        outOfStockList.setModel(model);
    }
    
    private void refreshSummaryData() {
        StringBuilder summary = new StringBuilder();
        
        try {
            // Get overall statistics
            List<Product> allProducts = productService.getAllProducts();
            List<Inventory> allInventory = inventoryService.getAllInventory();
            
            summary.append("=== INVENTORY OVERVIEW ===\n");
            summary.append(String.format("Total Products in Catalog: %d\n", allProducts.size()));
            summary.append(String.format("Total Inventory Items: %d\n", allInventory.size()));
            summary.append("\n");
            
            // Stock status breakdown
            int inStock = 0, lowStock = 0, outOfStock = 0;
            int totalOnHand = 0, totalAllocated = 0;
            
            for (Inventory inv : allInventory) {
                totalOnHand += inv.getQuantityOnHand();
                totalAllocated += inv.getQuantityAllocated();
                
                if (inv.isOutOfStock()) {
                    outOfStock++;
                } else if (inv.isLowStock()) {
                    lowStock++;
                } else {
                    inStock++;
                }
            }
            
            summary.append("=== STOCK STATUS ===\n");
            summary.append(String.format("In Stock: %d items\n", inStock));
            summary.append(String.format("Low Stock: %d items\n", lowStock));
            summary.append(String.format("Out of Stock: %d items\n", outOfStock));
            summary.append("\n");
            
            summary.append("=== QUANTITY SUMMARY ===\n");
            summary.append(String.format("Total Quantity On Hand: %,d units\n", totalOnHand));
            summary.append(String.format("Total Quantity Allocated: %,d units\n", totalAllocated));
            summary.append(String.format("Total Available Quantity: %,d units\n", totalOnHand - totalAllocated));
            summary.append("\n");
            
            // Top categories by product count
            summary.append("=== RECENT ACTIVITY ===\n");
            summary.append("Dashboard refreshed at: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
        } catch (Exception e) {
            summary.append("Error generating summary: ").append(e.getMessage());
        }
        
        summaryTextArea.setText(summary.toString());
        summaryTextArea.setCaretPosition(0);
    }
}