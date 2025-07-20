package com.javamastery.calculator;

/**
 * Simple calculator providing basic arithmetic operations.
 *
 * Learning Objectives:
 * - Practice basic Java syntax and operators
 * - Demonstrate method creation and documentation
 *
 * Key Concepts Demonstrated:
 * - Exception handling
 * - Input validation
 *
 * @author Java Mastery Student
 * @version 1.0
 * @since Phase 1
 */
public class Calculator {

    /**
     * Adds two numbers.
     *
     * @param a first operand
     * @param b second operand
     * @return sum of a and b
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * Subtracts one number from another.
     *
     * @param a minuend
     * @param b subtrahend
     * @return difference of a and b
     */
    public double subtract(double a, double b) {
        return a - b;
    }

    /**
     * Multiplies two numbers.
     *
     * @param a first operand
     * @param b second operand
     * @return product of a and b
     */
    public double multiply(double a, double b) {
        return a * b;
    }

    /**
     * Divides one number by another.
     *
     * @param a dividend
     * @param b divisor (must not be zero)
     * @return quotient of a divided by b
     * @throws IllegalArgumentException if b is zero
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Divisor must not be zero");
        }
        return a / b;
    }
}
