package com.javamastery.llm.prompt;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Metadata information about a prompt template.
 * Demonstrates immutable value objects and comprehensive metadata handling.
 * 
 * Learning Objectives:
 * - Value object pattern implementation
 * - Immutable data structures
 * - Builder pattern for complex objects
 * - Proper equals/hashCode implementation
 * 
 * @author Java Mastery Student
 */
public class TemplateMetadata {
    
    private final String name;
    private final String description;
    private final String category;
    private final String version;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModified;
    private final String[] tags;
    
    private TemplateMetadata(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category;
        this.version = builder.version;
        this.author = builder.author;
        this.createdAt = builder.createdAt;
        this.lastModified = builder.lastModified;
        this.tags = builder.tags.clone(); // Defensive copy
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getVersion() { return version; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    public String[] getTags() { return tags.clone(); } // Defensive copy
    
    public static Builder builder(String name) {
        return new Builder(name);
    }
    
    /**
     * Builder class for TemplateMetadata.
     */
    public static class Builder {
        private final String name;
        private String description = "";
        private String category = "general";
        private String version = "1.0";
        private String author = "Unknown";
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime lastModified = LocalDateTime.now();
        private String[] tags = new String[0];
        
        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "Name cannot be null");
        }
        
        public Builder description(String description) {
            this.description = description != null ? description : "";
            return this;
        }
        
        public Builder category(String category) {
            this.category = category != null ? category : "general";
            return this;
        }
        
        public Builder version(String version) {
            this.version = version != null ? version : "1.0";
            return this;
        }
        
        public Builder author(String author) {
            this.author = author != null ? author : "Unknown";
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
            return this;
        }
        
        public Builder lastModified(LocalDateTime lastModified) {
            this.lastModified = lastModified != null ? lastModified : LocalDateTime.now();
            return this;
        }
        
        public Builder tags(String... tags) {
            this.tags = tags != null ? tags.clone() : new String[0];
            return this;
        }
        
        public TemplateMetadata build() {
            return new TemplateMetadata(this);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TemplateMetadata that = (TemplateMetadata) obj;
        return Objects.equals(name, that.name) &&
               Objects.equals(version, that.version);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }
    
    @Override
    public String toString() {
        return String.format("TemplateMetadata{name='%s', version='%s', category='%s', author='%s', tags=%d}",
                           name, version, category, author, tags.length);
    }
}