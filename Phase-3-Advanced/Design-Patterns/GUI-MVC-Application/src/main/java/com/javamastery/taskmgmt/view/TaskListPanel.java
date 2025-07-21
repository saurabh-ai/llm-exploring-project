package com.javamastery.taskmgmt.view;

import com.javamastery.taskmgmt.model.Task;
import com.javamastery.taskmgmt.model.TaskStatus;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel that displays the list of tasks in a JTable
 */
public class TaskListPanel extends JPanel {
    
    private JTable taskTable;
    private TaskTableModel tableModel;
    private JScrollPane scrollPane;
    
    public TaskListPanel() {
        initializeComponents();
        setupLayout();
        setupTableBehavior();
    }
    
    private void initializeComponents() {
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        scrollPane = new JScrollPane(taskTable);
        
        // Configure table appearance
        configureTable();
    }
    
    private void configureTable() {
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(25);
        taskTable.setShowGrid(true);
        taskTable.setGridColor(Color.LIGHT_GRAY);
        
        // Set column widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Title
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Description
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Priority
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Due Date
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Created
        
        // Enable auto-resizing
        taskTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        // Set custom cell renderer for better appearance
        TaskCellRenderer cellRenderer = new TaskCellRenderer();
        for (int i = 0; i < taskTable.getColumnCount(); i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Tasks"));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupTableBehavior() {
        // Add double-click behavior (could be used for editing)
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click event - this could trigger edit action
                    firePropertyChange("taskDoubleClicked", null, getSelectedTask());
                }
            }
        });
    }
    
    // Public methods
    public void updateTasks(List<Task> tasks) {
        tableModel.updateTasks(tasks);
    }
    
    public Task getSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            return tableModel.getTaskAt(selectedRow);
        }
        return null;
    }
    
    public void clearSelection() {
        taskTable.clearSelection();
    }
    
    public void addListSelectionListener(ListSelectionListener listener) {
        taskTable.getSelectionModel().addListSelectionListener(listener);
    }
    
    // Table model for tasks
    private static class TaskTableModel extends AbstractTableModel {
        private static final String[] COLUMN_NAMES = {
            "Title", "Description", "Priority", "Due Date", "Status", "Created"
        };
        
        private List<Task> tasks;
        
        public TaskTableModel() {
            this.tasks = List.of(); // Empty list initially
        }
        
        public void updateTasks(List<Task> tasks) {
            this.tasks = tasks;
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return tasks.size();
        }
        
        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= 0 && rowIndex < tasks.size()) {
                Task task = tasks.get(rowIndex);
                switch (columnIndex) {
                    case 0: return task.getTitle();
                    case 1: return task.getDescription();
                    case 2: return task.getPriorityText();
                    case 3: return task.getDueDate() != null ? 
                           task.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "";
                    case 4: return task.getStatus().getDisplayName();
                    case 5: return task.getCreationDate().format(
                           DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                    default: return "";
                }
            }
            return "";
        }
        
        public Task getTaskAt(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < tasks.size()) {
                return tasks.get(rowIndex);
            }
            return null;
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; // Make table read-only
        }
    }
    
    // Custom cell renderer for better appearance
    private static class TaskCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            
            // Get the task for this row
            TaskTableModel model = (TaskTableModel) table.getModel();
            Task task = model.getTaskAt(row);
            
            if (task != null && !isSelected) {
                // Color coding based on task properties
                if (task.isOverdue()) {
                    c.setBackground(new Color(255, 230, 230)); // Light red for overdue
                } else if (task.getStatus() == TaskStatus.COMPLETED) {
                    c.setBackground(new Color(230, 255, 230)); // Light green for completed
                } else {
                    c.setBackground(Color.WHITE);
                }
                
                // Priority-based text color
                switch (task.getPriority()) {
                    case 1: // Critical
                        c.setForeground(Color.RED);
                        break;
                    case 2: // High
                        c.setForeground(new Color(255, 140, 0)); // Dark orange
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                        break;
                }
            } else if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            return c;
        }
    }
}