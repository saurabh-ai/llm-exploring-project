package com.javamastery.llm.prompt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

/**
 * Unit tests for SimplePromptTemplate.
 * Demonstrates template pattern testing and parameter validation.
 * 
 * @author Java Mastery Student
 */
public class SimplePromptTemplateTest {
    
    private PromptTemplate template;
    
    @BeforeEach
    void setUp() {
        String templateContent = "Hello {{name}}, you are {{age}} years old. {{greeting}}";
        template = new SimplePromptTemplate(
            "test-template", 
            templateContent, 
            new String[]{"greeting"}, // optional parameter
            TemplateMetadata.builder("test-template").build()
        );
    }
    
    @Test
    void testBasicTemplateRendering() throws PromptTemplateException {
        Map<String, Object> params = Map.of(
            "name", "John",
            "age", 25,
            "greeting", "Have a nice day!"
        );
        
        String result = template.render(params);
        assertEquals("Hello John, you are 25 years old. Have a nice day!", result);
    }
    
    @Test
    void testOptionalParameterOmitted() throws PromptTemplateException {
        Map<String, Object> params = Map.of(
            "name", "Alice",
            "age", 30
        );
        
        String result = template.render(params);
        assertEquals("Hello Alice, you are 30 years old.", result.trim());
    }
    
    @Test
    void testMissingRequiredParameter() {
        Map<String, Object> params = Map.of("name", "Bob");
        
        PromptTemplateException exception = assertThrows(
            PromptTemplateException.class,
            () -> template.render(params)
        );
        
        assertTrue(exception.getMessage().contains("Missing required parameter"));
        assertEquals("age", exception.getParameterName());
    }
    
    @Test
    void testUnknownParameter() {
        Map<String, Object> params = Map.of(
            "name", "Charlie",
            "age", 40,
            "unknown", "value"
        );
        
        PromptTemplateException exception = assertThrows(
            PromptTemplateException.class,
            () -> template.render(params)
        );
        
        assertTrue(exception.getMessage().contains("Unknown parameter"));
        assertEquals("unknown", exception.getParameterName());
    }
    
    @Test
    void testNullParametersMap() {
        PromptTemplateException exception = assertThrows(
            PromptTemplateException.class,
            () -> template.render(null)
        );
        
        assertTrue(exception.getMessage().contains("Parameters map cannot be null"));
    }
    
    @Test
    void testTemplateMetadata() {
        assertEquals("test-template", template.getName());
        assertNotNull(template.getMetadata());
        assertTrue(template.getRequiredParameters().length >= 0);
        assertTrue(template.getOptionalParameters().length >= 0);
    }
}