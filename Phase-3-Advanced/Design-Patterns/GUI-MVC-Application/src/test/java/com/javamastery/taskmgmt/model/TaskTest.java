package com.javamastery.taskmgmt.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Task class
 */
class TaskTest {
    
    @Test
    void testTaskCreation() {
        Task task = new Task("Test Task", "Test Description", 3, LocalDate.now().plusDays(5));
        
        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(3, task.getPriority());
        assertEquals(LocalDate.now().plusDays(5), task.getDueDate());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertNotNull(task.getCreationDate());
    }
    
    @Test
    void testDefaultConstructor() {
        Task task = new Task();
        
        assertNotNull(task.getId());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertNotNull(task.getCreationDate());
    }
    
    @Test
    void testPriorityValidation() {
        Task task = new Task();
        
        // Valid priorities (1-5)
        assertDoesNotThrow(() -> task.setPriority(1));
        assertDoesNotThrow(() -> task.setPriority(5));
        
        // Invalid priorities
        assertThrows(IllegalArgumentException.class, () -> task.setPriority(0));
        assertThrows(IllegalArgumentException.class, () -> task.setPriority(6));
    }
    
    @Test
    void testPriorityText() {
        Task task = new Task();
        
        task.setPriority(1);
        assertEquals("Critical", task.getPriorityText());
        
        task.setPriority(2);
        assertEquals("High", task.getPriorityText());
        
        task.setPriority(3);
        assertEquals("Medium", task.getPriorityText());
        
        task.setPriority(4);
        assertEquals("Low", task.getPriorityText());
        
        task.setPriority(5);
        assertEquals("Lowest", task.getPriorityText());
    }
    
    @Test
    void testIsOverdue() {
        // Task with future due date
        Task futureTask = new Task("Future Task", "Description", 2, LocalDate.now().plusDays(1));
        assertFalse(futureTask.isOverdue());
        
        // Task with past due date
        Task overdueTask = new Task("Overdue Task", "Description", 2, LocalDate.now().minusDays(1));
        assertTrue(overdueTask.isOverdue());
        
        // Completed task with past due date should not be overdue
        Task completedTask = new Task("Completed Task", "Description", 2, LocalDate.now().minusDays(1));
        completedTask.setStatus(TaskStatus.COMPLETED);
        assertFalse(completedTask.isOverdue());
        
        // Task with no due date
        Task noDueDateTask = new Task("No Due Date", "Description", 2, null);
        assertFalse(noDueDateTask.isOverdue());
    }
    
    @Test
    void testTaskEquality() {
        Task task1 = new Task("Task", "Description", 2, LocalDate.now());
        Task task2 = new Task("Task", "Description", 2, LocalDate.now());
        
        // Different tasks should not be equal (different IDs)
        assertNotEquals(task1, task2);
        
        // Same task should be equal to itself
        assertEquals(task1, task1);
        
        // Tasks with same ID should be equal
        task2.setId(task1.getId());
        assertEquals(task1, task2);
    }
    
    @Test
    void testTaskToString() {
        Task task = new Task("Test Task", "Description", 2, LocalDate.now().plusDays(1));
        String taskString = task.toString();
        
        assertTrue(taskString.contains("Test Task"));
        assertTrue(taskString.contains("priority=2"));
        assertTrue(taskString.contains("Pending"));
    }
}