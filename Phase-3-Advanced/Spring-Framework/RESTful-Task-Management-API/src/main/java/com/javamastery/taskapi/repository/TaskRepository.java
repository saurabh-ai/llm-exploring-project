package com.javamastery.taskapi.repository;

import com.javamastery.taskapi.model.Task;
import com.javamastery.taskapi.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Find tasks by user ID
     */
    Page<Task> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find tasks by status
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    
    /**
     * Find tasks by category ID
     */
    Page<Task> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * Find tasks by user ID and status
     */
    Page<Task> findByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);
    
    /**
     * Find overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find tasks due within specified days
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.status != 'COMPLETED'")
    List<Task> findTasksDueWithinDays(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find tasks by title containing (case insensitive)
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Task> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
    
    /**
     * Find tasks by description containing (case insensitive)
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Task> findByDescriptionContainingIgnoreCase(@Param("description") String description, Pageable pageable);
    
    /**
     * Find tasks by priority
     */
    Page<Task> findByPriority(Integer priority, Pageable pageable);
    
    /**
     * Complex search query
     */
    @Query("SELECT t FROM Task t WHERE " +
           "(:userId IS NULL OR t.user.id = :userId) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) AND " +
           "(:dueDateTo IS NULL OR t.dueDate <= :dueDateTo) AND " +
           "(:searchTerm IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findTasksBySearchCriteria(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("status") TaskStatus status,
        @Param("priority") Integer priority,
        @Param("dueDateFrom") LocalDateTime dueDateFrom,
        @Param("dueDateTo") LocalDateTime dueDateTo,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);
    
    /**
     * Count tasks by status
     */
    long countByStatus(TaskStatus status);
    
    /**
     * Count tasks by user ID and status
     */
    long countByUserIdAndStatus(Long userId, TaskStatus status);
    
    /**
     * Find task with user and category information
     */
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.user LEFT JOIN FETCH t.category WHERE t.id = :id")
    Optional<Task> findByIdWithUserAndCategory(@Param("id") Long id);
    
    /**
     * Find tasks by user ID with category information
     */
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.category WHERE t.user.id = :userId")
    List<Task> findByUserIdWithCategory(@Param("userId") Long userId);
    
    /**
     * Analytics: Count tasks by status
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> getTaskCountByStatus();
    
    /**
     * Analytics: Count tasks by priority
     */
    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> getTaskCountByPriority();
    
    /**
     * Analytics: Count tasks by category
     */
    @Query("SELECT c.name, COUNT(t) FROM Task t JOIN t.category c GROUP BY c.name")
    List<Object[]> getTaskCountByCategory();
}