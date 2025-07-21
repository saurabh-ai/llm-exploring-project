package com.javamastery.taskmgmt.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * TaskModel manages the collection of tasks and provides business logic operations.
 * This class implements the Model in the MVC pattern and uses Observer pattern
 * to notify views of changes.
 */
public class TaskModel {
    
    private final List<Task> tasks;
    private final List<TaskModelObserver> observers;
    private final ObjectMapper objectMapper;
    private File dataFile;
    
    // Sorting options
    public enum SortBy {
        CREATION_DATE, DUE_DATE, PRIORITY, TITLE
    }
    
    // Filtering options
    public enum FilterBy {
        ALL, PENDING, COMPLETED, OVERDUE
    }
    
    public TaskModel() {
        this.tasks = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        // Set default data file
        this.dataFile = new File(System.getProperty("user.home"), ".taskmgmt/tasks.json");
        createDataDirectoryIfNeeded();
    }
    
    private void createDataDirectoryIfNeeded() {
        File dataDir = dataFile.getParentFile();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    // Observer pattern implementation
    public interface TaskModelObserver {
        void onTaskAdded(Task task);
        void onTaskUpdated(Task task);
        void onTaskRemoved(Task task);
        void onTasksLoaded();
        void onModelCleared();
    }
    
    public void addObserver(TaskModelObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(TaskModelObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyTaskAdded(Task task) {
        observers.forEach(observer -> observer.onTaskAdded(task));
    }
    
    private void notifyTaskUpdated(Task task) {
        observers.forEach(observer -> observer.onTaskUpdated(task));
    }
    
    private void notifyTaskRemoved(Task task) {
        observers.forEach(observer -> observer.onTaskRemoved(task));
    }
    
    private void notifyTasksLoaded() {
        observers.forEach(TaskModelObserver::onTasksLoaded);
    }
    
    private void notifyModelCleared() {
        observers.forEach(TaskModelObserver::onModelCleared);
    }
    
    // CRUD operations
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        tasks.add(task);
        notifyTaskAdded(task);
        autoSave();
    }
    
    public void updateTask(Task updatedTask) {
        if (updatedTask == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        int index = findTaskIndexById(updatedTask.getId());
        if (index >= 0) {
            tasks.set(index, updatedTask);
            notifyTaskUpdated(updatedTask);
            autoSave();
        } else {
            throw new IllegalArgumentException("Task not found: " + updatedTask.getId());
        }
    }
    
    public void removeTask(Task task) {
        if (task != null && tasks.remove(task)) {
            notifyTaskRemoved(task);
            autoSave();
        }
    }
    
    public void removeTaskById(String taskId) {
        Task task = findTaskById(taskId);
        if (task != null) {
            removeTask(task);
        }
    }
    
    public Task findTaskById(String taskId) {
        return tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);
    }
    
    private int findTaskIndexById(String taskId) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(taskId)) {
                return i;
            }
        }
        return -1;
    }
    
    // Task operations
    public void markTaskComplete(String taskId) {
        Task task = findTaskById(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.COMPLETED);
            notifyTaskUpdated(task);
            autoSave();
        }
    }
    
    public void markTaskPending(String taskId) {
        Task task = findTaskById(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.PENDING);
            notifyTaskUpdated(task);
            autoSave();
        }
    }
    
    // Filtering and sorting
    public List<Task> getTasks(FilterBy filter, SortBy sortBy) {
        return tasks.stream()
                .filter(getFilterPredicate(filter))
                .sorted(getComparator(sortBy))
                .collect(Collectors.toList());
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    private Predicate<Task> getFilterPredicate(FilterBy filter) {
        switch (filter) {
            case PENDING:
                return task -> task.getStatus() == TaskStatus.PENDING;
            case COMPLETED:
                return task -> task.getStatus() == TaskStatus.COMPLETED;
            case OVERDUE:
                return Task::isOverdue;
            case ALL:
            default:
                return task -> true;
        }
    }
    
    private Comparator<Task> getComparator(SortBy sortBy) {
        switch (sortBy) {
            case DUE_DATE:
                return Comparator.comparing(Task::getDueDate, 
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case PRIORITY:
                return Comparator.comparingInt(Task::getPriority);
            case TITLE:
                return Comparator.comparing(Task::getTitle, 
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case CREATION_DATE:
            default:
                return Comparator.comparing(Task::getCreationDate).reversed();
        }
    }
    
    // Statistics
    public int getTotalTaskCount() {
        return tasks.size();
    }
    
    public int getPendingTaskCount() {
        return (int) tasks.stream().filter(task -> task.getStatus() == TaskStatus.PENDING).count();
    }
    
    public int getCompletedTaskCount() {
        return (int) tasks.stream().filter(task -> task.getStatus() == TaskStatus.COMPLETED).count();
    }
    
    public int getOverdueTaskCount() {
        return (int) tasks.stream().filter(Task::isOverdue).count();
    }
    
    // Persistence operations
    public void saveToFile() throws IOException {
        objectMapper.writeValue(dataFile, tasks);
    }
    
    public void loadFromFile() throws IOException {
        if (dataFile.exists()) {
            List<Task> loadedTasks = objectMapper.readValue(dataFile, new TypeReference<List<Task>>() {});
            tasks.clear();
            tasks.addAll(loadedTasks);
            notifyTasksLoaded();
        }
    }
    
    private void autoSave() {
        try {
            saveToFile();
        } catch (IOException e) {
            // In a real application, you might want to show an error dialog
            System.err.println("Auto-save failed: " + e.getMessage());
        }
    }
    
    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
        createDataDirectoryIfNeeded();
    }
    
    public File getDataFile() {
        return dataFile;
    }
    
    public void clearAllTasks() {
        tasks.clear();
        notifyModelCleared();
        autoSave();
    }
}