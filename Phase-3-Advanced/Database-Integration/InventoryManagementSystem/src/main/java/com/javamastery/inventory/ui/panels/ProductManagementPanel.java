package com.javamastery.inventory.ui.panels;

import com.javamastery.inventory.model.Product;
import com.javamastery.inventory.service.CategoryService;
import com.javamastery.inventory.service.ProductService;
import com.javamastery.inventory.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Simple product management panel
 */
public class ProductManagementPanel extends JPanel implements MainWindow.RefreshablePanel {
    private static final Logger logger = LoggerFactory.getLogger(ProductManagementPanel.class);
    
    private final ProductService productService;
    private final CategoryService categoryService;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public ProductManagementPanel(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        
        initializeComponents();
        setupLayout();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search field
        searchField = new JTextField(20);
        
        // Create table
        String[] columnNames = {"ID", "Name", "SKU", "Category", "Unit Price", "Cost Price", "Reorder Level"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100);
    }
    
    private void setupLayout() {
        // Top panel with search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        topPanel.add(searchButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshData();
        });
        topPanel.add(clearButton);
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        
        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewProductDetails());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Enter key for search
        searchField.addActionListener(e -> performSearch());
    }
    
    @Override
    public void refreshData() {
        logger.info("Refreshing product data");
        
        try {
            List<Product> products = productService.getAllProducts();
            updateTable(products);
            
        } catch (Exception e) {
            logger.error("Error refreshing product data", e);
            JOptionPane.showMessageDialog(this,
                "Error refreshing products: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        
        try {
            List<Product> products = productService.searchProducts(searchTerm);
            updateTable(products);
            
        } catch (Exception e) {
            logger.error("Error searching products", e);
            JOptionPane.showMessageDialog(this,
                "Error searching products: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Product> products) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add products to table
        for (Product product : products) {
            Object[] row = {
                product.getProductId(),
                product.getName(),
                product.getSku(),
                product.getCategoryName() != null ? product.getCategoryName() : "N/A",
                String.format("$%.2f", product.getUnitPrice()),
                String.format("$%.2f", product.getCostPrice()),
                product.getReorderLevel()
            };
            tableModel.addRow(row);
        }
    }
    
    private void viewProductDetails() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a product to view details",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Product product = productService.getProductById(productId).orElse(null);
            if (product == null) {
                JOptionPane.showMessageDialog(this,
                    "Product not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Show product details dialog
            showProductDetailsDialog(product);
            
        } catch (Exception e) {
            logger.error("Error viewing product details", e);
            JOptionPane.showMessageDialog(this,
                "Error viewing product details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showProductDetailsDialog(Product product) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Product Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        StringBuilder details = new StringBuilder();
        details.append("=== PRODUCT DETAILS ===\n\n");
        details.append(String.format("ID: %d\n", product.getProductId()));
        details.append(String.format("Name: %s\n", product.getName()));
        details.append(String.format("SKU: %s\n", product.getSku()));
        details.append(String.format("Description: %s\n", product.getDescription() != null ? product.getDescription() : "N/A"));
        details.append(String.format("Category: %s\n", product.getCategoryName() != null ? product.getCategoryName() : "N/A"));
        details.append("\n=== PRICING ===\n");
        details.append(String.format("Unit Price: $%.2f\n", product.getUnitPrice()));
        details.append(String.format("Cost Price: $%.2f\n", product.getCostPrice()));
        details.append(String.format("Margin: $%.2f\n", product.getMargin()));
        details.append(String.format("Margin %%: %.2f%%\n", product.getMarginPercentage()));
        details.append("\n=== INVENTORY ===\n");
        details.append(String.format("Reorder Level: %d\n", product.getReorderLevel()));
        details.append("\n=== TIMESTAMPS ===\n");
        details.append(String.format("Created: %s\n", 
            product.getCreatedAt() != null ? 
                product.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A"));
        details.append(String.format("Updated: %s\n", 
            product.getUpdatedAt() != null ? 
                product.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A"));
        
        detailsArea.setText(details.toString());
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}