package com.javamastery.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Calculator}.
 */
class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addTwoNumbers() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void subtractTwoNumbers() {
        assertEquals(1, calculator.subtract(3, 2));
    }

    @Test
    void multiplyTwoNumbers() {
        assertEquals(6, calculator.multiply(2, 3));
    }

    @Test
    void divideTwoNumbers() {
        assertEquals(2, calculator.divide(6, 3));
    }

    @Test
    void divideByZeroThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calculator.divide(1, 0));
        assertEquals("Divisor must not be zero", exception.getMessage());
    }
}
