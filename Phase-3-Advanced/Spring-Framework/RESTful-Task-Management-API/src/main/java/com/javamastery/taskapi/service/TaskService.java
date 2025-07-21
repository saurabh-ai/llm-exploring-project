package com.javamastery.taskapi.service;

import com.javamastery.taskapi.dto.*;
import com.javamastery.taskapi.exception.CategoryNotFoundException;
import com.javamastery.taskapi.exception.TaskNotFoundException;
import com.javamastery.taskapi.exception.UserNotFoundException;
import com.javamastery.taskapi.exception.ValidationException;
import com.javamastery.taskapi.model.Category;
import com.javamastery.taskapi.model.Task;
import com.javamastery.taskapi.model.TaskStatus;
import com.javamastery.taskapi.model.User;
import com.javamastery.taskapi.repository.CategoryRepository;
import com.javamastery.taskapi.repository.TaskRepository;
import com.javamastery.taskapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for Task operations
 */
@Service
@Transactional
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, 
                      UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      @Lazy UserService userService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }
    
    /**
     * Get all tasks with pagination
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> getAllTasks(Pageable pageable) {
        logger.debug("Fetching all tasks with pagination: {}", pageable);
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(this::convertToDto);
    }
    
    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        logger.debug("Fetching task with ID: {}", id);
        Task task = taskRepository.findByIdWithUserAndCategory(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return convertToDto(task);
    }
    
    /**
     * Create a new task
     */
    public TaskDto createTask(CreateTaskRequest request) {
        logger.debug("Creating new task: {}", request);
        
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        
        // Validate category if provided
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
        }
        
        // Validate due date is in the future
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Due date must be in the future");
        }
        
        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getDueDate(),
                request.getPriority(),
                user,
                category
        );
        
        Task savedTask = taskRepository.save(task);
        logger.info("Task created with ID: {}", savedTask.getId());
        
        return convertToDto(savedTask);
    }
    
    /**
     * Update task
     */
    public TaskDto updateTask(Long id, UpdateTaskRequest request) {
        logger.debug("Updating task with ID: {}", id);
        
        Task existingTask = taskRepository.findByIdWithUserAndCategory(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        // Update fields if provided
        if (request.getTitle() != null) {
            existingTask.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            existingTask.setDescription(request.getDescription());
        }
        
        if (request.getStatus() != null) {
            existingTask.setStatus(request.getStatus());
        }
        
        if (request.getDueDate() != null) {
            existingTask.setDueDate(request.getDueDate());
        } else if (Boolean.TRUE.equals(request.getClearDueDate())) {
            existingTask.setDueDate(null);
        }
        
        if (request.getPriority() != null) {
            existingTask.setPriority(request.getPriority());
        }
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            existingTask.setCategory(category);
        } else if (Boolean.TRUE.equals(request.getClearCategory())) {
            existingTask.setCategory(null);
        }
        
        Task updatedTask = taskRepository.save(existingTask);
        logger.info("Task updated with ID: {}", updatedTask.getId());
        
        return convertToDto(updatedTask);
    }
    
    /**
     * Update task status only
     */
    public TaskDto updateTaskStatus(Long id, TaskStatus status) {
        logger.debug("Updating task status for ID: {} to status: {}", id, status);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        logger.info("Task status updated for ID: {}", id);
        
        return convertToDto(updatedTask);
    }
    
    /**
     * Delete task
     */
    public void deleteTask(Long id) {
        logger.debug("Deleting task with ID: {}", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        taskRepository.delete(task);
        logger.info("Task deleted with ID: {}", id);
    }
    
    /**
     * Search tasks with criteria
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> searchTasks(TaskSearchRequest searchRequest) {
        logger.debug("Searching tasks with criteria: {}", searchRequest);
        
        // Build pageable with sorting
        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getSortDirection()), 
                           searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        Page<Task> tasks = taskRepository.findTasksBySearchCriteria(
                searchRequest.getUserId(),
                searchRequest.getCategoryId(),
                searchRequest.getStatus(),
                searchRequest.getPriority(),
                searchRequest.getDueDateFrom(),
                searchRequest.getDueDateTo(),
                searchRequest.getSearchTerm(),
                pageable
        );
        
        return tasks.map(this::convertToDto);
    }
    
    /**
     * Get tasks by user ID
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByUserId(Long userId) {
        logger.debug("Fetching tasks for user ID: {}", userId);
        List<Task> tasks = taskRepository.findByUserIdWithCategory(userId);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getOverdueTasks() {
        logger.debug("Fetching overdue tasks");
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        return overdueTasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get tasks due within specified days
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksDueWithinDays(int days) {
        logger.debug("Fetching tasks due within {} days", days);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);
        
        List<Task> tasks = taskRepository.findTasksDueWithinDays(now, endDate);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get task analytics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskAnalytics() {
        logger.debug("Generating task analytics");
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Task counts by status
        List<Object[]> statusCounts = taskRepository.getTaskCountByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] result : statusCounts) {
            TaskStatus status = (TaskStatus) result[0];
            Long count = (Long) result[1];
            statusMap.put(status.name(), count);
        }
        analytics.put("tasksByStatus", statusMap);
        
        // Task counts by priority
        List<Object[]> priorityCounts = taskRepository.getTaskCountByPriority();
        Map<String, Long> priorityMap = new HashMap<>();
        for (Object[] result : priorityCounts) {
            Integer priority = (Integer) result[0];
            Long count = (Long) result[1];
            String priorityName = switch (priority) {
                case 1 -> "Low";
                case 2 -> "Medium";
                case 3 -> "High";
                default -> "Unknown";
            };
            priorityMap.put(priorityName, count);
        }
        analytics.put("tasksByPriority", priorityMap);
        
        // Task counts by category
        List<Object[]> categoryCounts = taskRepository.getTaskCountByCategory();
        Map<String, Long> categoryMap = new HashMap<>();
        for (Object[] result : categoryCounts) {
            String categoryName = (String) result[0];
            Long count = (Long) result[1];
            categoryMap.put(categoryName, count);
        }
        analytics.put("tasksByCategory", categoryMap);
        
        // Total counts
        analytics.put("totalTasks", taskRepository.count());
        analytics.put("overdueTasks", taskRepository.findOverdueTasks(LocalDateTime.now()).size());
        
        return analytics;
    }
    
    /**
     * Convert Task entity to TaskDto
     */
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getPriority()
        );
        
        // Set user information
        if (task.getUser() != null) {
            dto.setUserId(task.getUser().getId());
            dto.setUsername(task.getUser().getUsername());
            dto.setUserFullName(task.getUser().getFirstName() + " " + task.getUser().getLastName());
        }
        
        // Set category information
        if (task.getCategory() != null) {
            dto.setCategoryId(task.getCategory().getId());
            dto.setCategoryName(task.getCategory().getName());
            dto.setCategoryColorCode(task.getCategory().getColorCode());
        }
        
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        
        return dto;
    }
}