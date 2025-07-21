package com.javamastery.inventory.ui.panels;

import com.javamastery.inventory.model.Category;
import com.javamastery.inventory.service.CategoryService;
import com.javamastery.inventory.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Simple category management panel
 */
public class CategoryPanel extends JPanel implements MainWindow.RefreshablePanel {
    private static final Logger logger = LoggerFactory.getLogger(CategoryPanel.class);
    
    private final CategoryService categoryService;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    
    public CategoryPanel(CategoryService categoryService) {
        this.categoryService = categoryService;
        
        initializeComponents();
        setupLayout();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        String[] columnNames = {"ID", "Name", "Description", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        categoryTable.getColumnModel().getColumn(3).setPreferredWidth(150);
    }
    
    private void setupLayout() {
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        
        JButton addButton = new JButton("Add Category");
        addButton.addActionListener(e -> showAddCategoryDialog());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void refreshData() {
        logger.info("Refreshing category data");
        
        try {
            List<Category> categories = categoryService.getAllCategories();
            
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Add categories to table
            for (Category category : categories) {
                Object[] row = {
                    category.getCategoryId(),
                    category.getName(),
                    category.getDescription(),
                    category.getCreatedAt() != null ? 
                        category.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : ""
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            logger.error("Error refreshing category data", e);
            JOptionPane.showMessageDialog(this,
                "Error refreshing categories: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Category", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Category name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                categoryService.createCategory(name, description);
                refreshData();
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this, "Category created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                logger.error("Error creating category", ex);
                JOptionPane.showMessageDialog(dialog, "Error creating category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}