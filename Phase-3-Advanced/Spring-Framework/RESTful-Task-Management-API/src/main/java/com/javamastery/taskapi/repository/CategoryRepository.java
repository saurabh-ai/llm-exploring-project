package com.javamastery.taskapi.repository;

import com.javamastery.taskapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name
     */
    Optional<Category> findByName(String name);
    
    /**
     * Check if category name exists
     */
    boolean existsByName(String name);
    
    /**
     * Find categories containing name (case insensitive)
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find category with tasks count
     */
    @Query("SELECT c, COUNT(t) FROM Category c LEFT JOIN c.tasks t WHERE c.id = :id GROUP BY c")
    Optional<Object[]> findByIdWithTaskCount(@Param("id") Long id);
    
    /**
     * Find all categories with tasks count
     */
    @Query("SELECT c, COUNT(t) FROM Category c LEFT JOIN c.tasks t GROUP BY c ORDER BY c.name")
    List<Object[]> findAllWithTaskCount();
}