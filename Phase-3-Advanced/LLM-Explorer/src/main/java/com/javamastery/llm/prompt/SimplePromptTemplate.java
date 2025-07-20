package com.javamastery.llm.prompt;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Simple implementation of PromptTemplate using string replacement.
 * Uses {{parameter}} syntax for parameter substitution.
 * 
 * Learning Objectives:
 * - Template Method pattern implementation
 * - Regular expression usage for text processing
 * - Stream API for data processing
 * - Parameter validation and error handling
 * 
 * Key Concepts Demonstrated:
 * - Template parameter extraction using regex
 * - Stream operations for collection processing
 * - Defensive programming with parameter validation
 * 
 * @author Java Mastery Student
 */
public class SimplePromptTemplate implements PromptTemplate {
    
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    
    private final String template;
    private final String name;
    private final TemplateMetadata metadata;
    private final String[] requiredParameters;
    private final String[] optionalParameters;
    
    /**
     * Creates a new SimplePromptTemplate.
     * 
     * @param name the template name
     * @param template the template content with {{parameter}} placeholders
     */
    public SimplePromptTemplate(String name, String template) {
        this(name, template, new String[0], TemplateMetadata.builder(name).build());
    }
    
    /**
     * Creates a new SimplePromptTemplate with optional parameters.
     * 
     * @param name the template name
     * @param template the template content
     * @param optionalParameters names of optional parameters
     * @param metadata template metadata
     */
    public SimplePromptTemplate(String name, String template, String[] optionalParameters, TemplateMetadata metadata) {
        this.name = name;
        this.template = template;
        this.metadata = metadata;
        this.optionalParameters = optionalParameters != null ? optionalParameters.clone() : new String[0];
        
        // Extract required parameters from template (excluding optional ones)
        Set<String> allParameters = extractParameterNames(template);
        Set<String> optionalSet = Set.of(this.optionalParameters);
        
        this.requiredParameters = allParameters.stream()
                                               .filter(param -> !optionalSet.contains(param))
                                               .toArray(String[]::new);
    }
    
    @Override
    public String render(Map<String, Object> parameters) throws PromptTemplateException {
        validateParameters(parameters);
        
        String result = template;
        
        // Replace all parameter placeholders
        Matcher matcher = PARAMETER_PATTERN.matcher(template);
        while (matcher.find()) {
            String paramName = matcher.group(1).trim();
            String placeholder = "{{" + matcher.group(1) + "}}";
            
            if (parameters.containsKey(paramName)) {
                Object value = parameters.get(paramName);
                String replacement = value != null ? value.toString() : "";
                result = result.replace(placeholder, replacement);
            } else if (Arrays.asList(optionalParameters).contains(paramName)) {
                // Optional parameter not provided, replace with empty string
                result = result.replace(placeholder, "");
            }
        }
        
        return result.trim();
    }
    
    @Override
    public String getTemplate() {
        return template;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String[] getRequiredParameters() {
        return requiredParameters.clone();
    }
    
    @Override
    public String[] getOptionalParameters() {
        return optionalParameters.clone();
    }
    
    @Override
    public void validateParameters(Map<String, Object> parameters) throws PromptTemplateException {
        if (parameters == null) {
            throw new PromptTemplateException("Parameters map cannot be null", name);
        }
        
        // Check for missing required parameters
        for (String required : requiredParameters) {
            if (!parameters.containsKey(required)) {
                throw new PromptTemplateException(
                    "Missing required parameter: " + required, name, required);
            }
        }
        
        // Validate parameter values
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object value = entry.getValue();
            
            // Check if parameter is recognized
            if (!isValidParameter(paramName)) {
                throw new PromptTemplateException(
                    "Unknown parameter: " + paramName, name, paramName);
            }
            
            // Basic value validation
            if (value != null && value.toString().trim().isEmpty() && 
                Arrays.asList(requiredParameters).contains(paramName)) {
                throw new PromptTemplateException(
                    "Required parameter cannot be empty: " + paramName, name, paramName);
            }
        }
    }
    
    @Override
    public TemplateMetadata getMetadata() {
        return metadata;
    }
    
    /**
     * Extracts parameter names from the template using regular expressions.
     * Demonstrates Stream API usage for text processing.
     * 
     * @param template the template string
     * @return set of parameter names found in the template
     */
    private Set<String> extractParameterNames(String template) {
        return PARAMETER_PATTERN.matcher(template)
                                .results()
                                .map(matchResult -> matchResult.group(1).trim())
                                .collect(Collectors.toSet());
    }
    
    /**
     * Checks if a parameter name is valid for this template.
     * 
     * @param paramName the parameter name to check
     * @return true if the parameter is valid, false otherwise
     */
    private boolean isValidParameter(String paramName) {
        return Arrays.asList(requiredParameters).contains(paramName) ||
               Arrays.asList(optionalParameters).contains(paramName);
    }
    
    @Override
    public String toString() {
        return String.format("SimplePromptTemplate{name='%s', requiredParams=%d, optionalParams=%d}",
                           name, requiredParameters.length, optionalParameters.length);
    }
}