package com.javamastery.taskmgmt.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TaskModel class
 */
class TaskModelTest {
    
    private TaskModel taskModel;
    private Task sampleTask;
    
    @TempDir
    File tempDir;
    
    @BeforeEach
    void setUp() {
        taskModel = new TaskModel();
        // Set a temporary file for testing
        File testFile = new File(tempDir, "test-tasks.json");
        taskModel.setDataFile(testFile);
        
        sampleTask = new Task("Sample Task", "This is a test task", 2, LocalDate.now().plusDays(7));
    }
    
    @Test
    void testAddTask() {
        assertEquals(0, taskModel.getTotalTaskCount());
        
        taskModel.addTask(sampleTask);
        
        assertEquals(1, taskModel.getTotalTaskCount());
        assertEquals(1, taskModel.getPendingTaskCount());
        assertEquals(0, taskModel.getCompletedTaskCount());
    }
    
    @Test
    void testAddNullTask() {
        assertThrows(IllegalArgumentException.class, () -> taskModel.addTask(null));
    }
    
    @Test
    void testUpdateTask() {
        taskModel.addTask(sampleTask);
        
        sampleTask.setTitle("Updated Title");
        sampleTask.setDescription("Updated Description");
        
        taskModel.updateTask(sampleTask);
        
        Task retrievedTask = taskModel.findTaskById(sampleTask.getId());
        assertEquals("Updated Title", retrievedTask.getTitle());
        assertEquals("Updated Description", retrievedTask.getDescription());
    }
    
    @Test
    void testRemoveTask() {
        taskModel.addTask(sampleTask);
        assertEquals(1, taskModel.getTotalTaskCount());
        
        taskModel.removeTask(sampleTask);
        assertEquals(0, taskModel.getTotalTaskCount());
        assertNull(taskModel.findTaskById(sampleTask.getId()));
    }
    
    @Test
    void testMarkTaskComplete() {
        taskModel.addTask(sampleTask);
        assertEquals(TaskStatus.PENDING, sampleTask.getStatus());
        
        taskModel.markTaskComplete(sampleTask.getId());
        
        assertEquals(TaskStatus.COMPLETED, sampleTask.getStatus());
        assertEquals(0, taskModel.getPendingTaskCount());
        assertEquals(1, taskModel.getCompletedTaskCount());
    }
    
    @Test
    void testMarkTaskPending() {
        taskModel.addTask(sampleTask);
        taskModel.markTaskComplete(sampleTask.getId());
        
        taskModel.markTaskPending(sampleTask.getId());
        
        assertEquals(TaskStatus.PENDING, sampleTask.getStatus());
        assertEquals(1, taskModel.getPendingTaskCount());
        assertEquals(0, taskModel.getCompletedTaskCount());
    }
    
    @Test
    void testFilterTasks() {
        // Add tasks with different statuses
        Task pendingTask = new Task("Pending Task", "Pending", 1, LocalDate.now());
        Task completedTask = new Task("Completed Task", "Completed", 2, LocalDate.now());
        
        taskModel.addTask(pendingTask);
        taskModel.addTask(completedTask);
        taskModel.markTaskComplete(completedTask.getId());
        
        // Test filtering
        List<Task> allTasks = taskModel.getTasks(TaskModel.FilterBy.ALL, TaskModel.SortBy.CREATION_DATE);
        assertEquals(2, allTasks.size());
        
        List<Task> pendingTasks = taskModel.getTasks(TaskModel.FilterBy.PENDING, TaskModel.SortBy.CREATION_DATE);
        assertEquals(1, pendingTasks.size());
        assertEquals("Pending Task", pendingTasks.get(0).getTitle());
        
        List<Task> completedTasks = taskModel.getTasks(TaskModel.FilterBy.COMPLETED, TaskModel.SortBy.CREATION_DATE);
        assertEquals(1, completedTasks.size());
        assertEquals("Completed Task", completedTasks.get(0).getTitle());
    }
    
    @Test
    void testSortTasks() {
        Task highPriorityTask = new Task("High Priority", "Description", 1, LocalDate.now().plusDays(5));
        Task lowPriorityTask = new Task("Low Priority", "Description", 5, LocalDate.now().plusDays(3));
        
        taskModel.addTask(lowPriorityTask);
        taskModel.addTask(highPriorityTask);
        
        // Sort by priority
        List<Task> sortedByPriority = taskModel.getTasks(TaskModel.FilterBy.ALL, TaskModel.SortBy.PRIORITY);
        assertEquals("High Priority", sortedByPriority.get(0).getTitle());
        assertEquals("Low Priority", sortedByPriority.get(1).getTitle());
        
        // Sort by due date
        List<Task> sortedByDueDate = taskModel.getTasks(TaskModel.FilterBy.ALL, TaskModel.SortBy.DUE_DATE);
        assertEquals("Low Priority", sortedByDueDate.get(0).getTitle()); // Earlier due date
        assertEquals("High Priority", sortedByDueDate.get(1).getTitle());
    }
    
    @Test
    void testOverdueTasks() {
        Task overdueTask = new Task("Overdue Task", "Description", 2, LocalDate.now().minusDays(1));
        taskModel.addTask(overdueTask);
        
        assertEquals(1, taskModel.getOverdueTaskCount());
        assertTrue(overdueTask.isOverdue());
        
        List<Task> overdueTasks = taskModel.getTasks(TaskModel.FilterBy.OVERDUE, TaskModel.SortBy.CREATION_DATE);
        assertEquals(1, overdueTasks.size());
    }
    
    @Test
    void testPersistence() throws IOException {
        // Add some tasks
        taskModel.addTask(sampleTask);
        Task anotherTask = new Task("Another Task", "Description", 3, LocalDate.now().plusDays(1));
        taskModel.addTask(anotherTask);
        
        // Save to file
        taskModel.saveToFile();
        
        // Create new model and load
        TaskModel newModel = new TaskModel();
        newModel.setDataFile(taskModel.getDataFile());
        newModel.loadFromFile();
        
        assertEquals(2, newModel.getTotalTaskCount());
        assertNotNull(newModel.findTaskById(sampleTask.getId()));
        assertNotNull(newModel.findTaskById(anotherTask.getId()));
    }
    
    @Test
    void testClearAllTasks() {
        taskModel.addTask(sampleTask);
        Task anotherTask = new Task("Another Task", "Description", 3, LocalDate.now().plusDays(1));
        taskModel.addTask(anotherTask);
        
        assertEquals(2, taskModel.getTotalTaskCount());
        
        taskModel.clearAllTasks();
        
        assertEquals(0, taskModel.getTotalTaskCount());
        assertEquals(0, taskModel.getAllTasks().size());
    }
}