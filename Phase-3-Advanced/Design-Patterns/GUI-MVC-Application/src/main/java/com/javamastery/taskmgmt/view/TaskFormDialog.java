package com.javamastery.taskmgmt.view;

import com.javamastery.taskmgmt.model.Task;
import com.javamastery.taskmgmt.model.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Dialog for creating and editing tasks
 */
public class TaskFormDialog extends JDialog {
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityComboBox;
    private JTextField dueDateField;
    private JComboBox<TaskStatus> statusComboBox;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Task task; // The task being edited (null for new task)
    private boolean isConfirmed = false;
    
    // Priority options
    private static final String[] PRIORITY_OPTIONS = {
        "1 - Critical",
        "2 - High", 
        "3 - Medium",
        "4 - Low",
        "5 - Lowest"
    };
    
    public TaskFormDialog(Frame parent, String title) {
        super(parent, title, true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureDialog();
    }
    
    private void initializeComponents() {
        titleField = new JTextField(20);
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        priorityComboBox = new JComboBox<>(PRIORITY_OPTIONS);
        priorityComboBox.setSelectedIndex(2); // Default to Medium priority
        
        dueDateField = new JTextField(10);
        dueDateField.setToolTipText("Format: YYYY-MM-DD (e.g., 2023-12-25)");
        
        statusComboBox = new JComboBox<>(TaskStatus.values());
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleField, gbc);
        
        // Description field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Priority field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(priorityComboBox, gbc);
        
        // Due date field
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Due Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dueDateField, gbc);
        
        // Status field (only shown when editing)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusComboBox, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add required field note
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC));
        noteLabel.setForeground(Color.GRAY);
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.add(noteLabel);
        add(notePanel, BorderLayout.NORTH);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateAndSave()) {
                    isConfirmed = true;
                    dispose();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Add escape key to cancel
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void configureDialog() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        setResizable(true);
    }
    
    // Public methods
    public void setTaskForEditing(Task task) {
        this.task = task;
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            priorityComboBox.setSelectedIndex(task.getPriority() - 1);
            dueDateField.setText(task.getDueDate() != null ? task.getDueDate().toString() : "");
            statusComboBox.setSelectedItem(task.getStatus());
            statusComboBox.setVisible(true);
            setTitle("Edit Task");
        } else {
            clearForm();
            statusComboBox.setVisible(false);
            setTitle("New Task");
        }
    }
    
    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        priorityComboBox.setSelectedIndex(2); // Medium priority
        dueDateField.setText("");
        statusComboBox.setSelectedItem(TaskStatus.PENDING);
    }
    
    private boolean validateAndSave() {
        // Validate title
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showErrorMessage("Title is required.");
            titleField.requestFocus();
            return false;
        }
        
        // Validate and parse due date
        LocalDate dueDate = null;
        String dueDateText = dueDateField.getText().trim();
        if (!dueDateText.isEmpty()) {
            try {
                dueDate = LocalDate.parse(dueDateText);
            } catch (DateTimeParseException e) {
                showErrorMessage("Invalid date format. Please use YYYY-MM-DD format.");
                dueDateField.requestFocus();
                return false;
            }
        }
        
        // Create or update task
        String description = descriptionArea.getText().trim();
        int priority = priorityComboBox.getSelectedIndex() + 1;
        
        if (task == null) {
            // Creating new task
            task = new Task(title, description, priority, dueDate);
        } else {
            // Updating existing task
            task.setTitle(title);
            task.setDescription(description);
            task.setPriority(priority);
            task.setDueDate(dueDate);
            task.setStatus((TaskStatus) statusComboBox.getSelectedItem());
        }
        
        return true;
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public Task getTask() {
        return task;
    }
    
    public boolean isConfirmed() {
        return isConfirmed;
    }
    
    // Factory methods for common usage
    public static TaskFormDialog createNewTaskDialog(Frame parent) {
        TaskFormDialog dialog = new TaskFormDialog(parent, "New Task");
        dialog.setTaskForEditing(null);
        return dialog;
    }
    
    public static TaskFormDialog createEditTaskDialog(Frame parent, Task task) {
        TaskFormDialog dialog = new TaskFormDialog(parent, "Edit Task");
        dialog.setTaskForEditing(task);
        return dialog;
    }
}