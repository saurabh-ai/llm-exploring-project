package com.javamastery.llm.prompt;

import java.util.Map;

/**
 * Interface for prompt template management.
 * Demonstrates the Template Method pattern and Strategy pattern.
 * 
 * Learning Objectives:
 * - Template Method pattern for customizable algorithms
 * - Strategy pattern for different template engines
 * - Interface segregation principle
 * - Generic programming with wildcards
 * 
 * Key Concepts Demonstrated:
 * - Template processing and parameter substitution
 * - Pluggable template engine architecture
 * - Type-safe parameter handling
 * 
 * @author Java Mastery Student
 */
public interface PromptTemplate {
    
    /**
     * Renders the template with the provided parameters.
     * 
     * @param parameters the parameters to substitute in the template
     * @return the rendered prompt string
     * @throws PromptTemplateException if rendering fails
     */
    String render(Map<String, Object> parameters) throws PromptTemplateException;
    
    /**
     * Returns the raw template string.
     * 
     * @return the template content
     */
    String getTemplate();
    
    /**
     * Returns the name/identifier of this template.
     * 
     * @return the template name
     */
    String getName();
    
    /**
     * Returns the required parameter names for this template.
     * 
     * @return array of required parameter names
     */
    String[] getRequiredParameters();
    
    /**
     * Returns the optional parameter names for this template.
     * 
     * @return array of optional parameter names
     */
    String[] getOptionalParameters();
    
    /**
     * Validates that all required parameters are present.
     * 
     * @param parameters the parameters to validate
     * @throws PromptTemplateException if validation fails
     */
    void validateParameters(Map<String, Object> parameters) throws PromptTemplateException;
    
    /**
     * Returns metadata about this template.
     * 
     * @return template metadata
     */
    TemplateMetadata getMetadata();
}