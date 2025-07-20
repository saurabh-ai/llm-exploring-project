package com.javamastery.llm.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for configuration properties.
 * Demonstrates custom annotation creation and usage.
 * 
 * Learning Objectives:
 * - Custom annotation design
 * - Annotation processing patterns
 * - Reflection-based configuration
 * 
 * @author Java Mastery Student
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {
    
    /**
     * The property name (defaults to field name if not specified).
     */
    String value() default "";
    
    /**
     * Default value for the property.
     */
    String defaultValue() default "";
    
    /**
     * Whether this property is required.
     */
    boolean required() default false;
    
    /**
     * Description of the property.
     */
    String description() default "";
}