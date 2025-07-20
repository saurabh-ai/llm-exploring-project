package com.javamastery.llm.prompt;

/**
 * Exception class for prompt template operations.
 * Demonstrates custom exception design with context information.
 * 
 * Learning Objectives:
 * - Custom exception hierarchy design
 * - Context preservation in exceptions
 * - Proper exception chaining
 * 
 * @author Java Mastery Student
 */
public class PromptTemplateException extends Exception {
    
    private final String templateName;
    private final String parameterName;
    
    public PromptTemplateException(String message) {
        super(message);
        this.templateName = null;
        this.parameterName = null;
    }
    
    public PromptTemplateException(String message, Throwable cause) {
        super(message, cause);
        this.templateName = null;
        this.parameterName = null;
    }
    
    public PromptTemplateException(String message, String templateName) {
        super(message);
        this.templateName = templateName;
        this.parameterName = null;
    }
    
    public PromptTemplateException(String message, String templateName, String parameterName) {
        super(message);
        this.templateName = templateName;
        this.parameterName = parameterName;
    }
    
    public PromptTemplateException(String message, Throwable cause, String templateName) {
        super(message, cause);
        this.templateName = templateName;
        this.parameterName = null;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        
        if (templateName != null) {
            sb.append("[template: ").append(templateName).append("]");
        }
        
        if (parameterName != null) {
            sb.append("[parameter: ").append(parameterName).append("]");
        }
        
        sb.append(": ").append(getMessage());
        return sb.toString();
    }
}