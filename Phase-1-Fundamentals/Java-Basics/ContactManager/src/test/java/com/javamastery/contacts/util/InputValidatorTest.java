package com.javamastery.contacts.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InputValidator}.
 */
class InputValidatorTest {

    @Test
    void isValidPhone_ValidFormats() {
        assertTrue(InputValidator.isValidPhone("+1-555-0123"));
        assertTrue(InputValidator.isValidPhone("+15550123"));
        assertTrue(InputValidator.isValidPhone("(555) 123-4567"));
        assertTrue(InputValidator.isValidPhone("555-123-4567"));
        assertTrue(InputValidator.isValidPhone("5551234567"));
    }

    @Test
    void isValidPhone_InvalidFormats() {
        assertFalse(InputValidator.isValidPhone(null));
        assertFalse(InputValidator.isValidPhone(""));
        assertFalse(InputValidator.isValidPhone("   "));
        assertFalse(InputValidator.isValidPhone("abc-def-ghij"));
        assertFalse(InputValidator.isValidPhone("123-456"));  // Too short
    }

    @Test
    void isValidEmail_ValidFormats() {
        assertTrue(InputValidator.isValidEmail("test@example.com"));
        assertTrue(InputValidator.isValidEmail("user.name@domain.co.uk"));
        assertTrue(InputValidator.isValidEmail("user+tag@example.org"));
        assertTrue(InputValidator.isValidEmail(""));  // Email is optional
        assertTrue(InputValidator.isValidEmail(null));  // Email is optional
    }

    @Test
    void isValidEmail_InvalidFormats() {
        assertFalse(InputValidator.isValidEmail("invalid"));
        assertFalse(InputValidator.isValidEmail("@domain.com"));
        assertFalse(InputValidator.isValidEmail("user@"));
        assertFalse(InputValidator.isValidEmail("user@domain"));
        assertFalse(InputValidator.isValidEmail("user..name@domain.com"));
        assertFalse(InputValidator.isValidEmail("user@.domain.com"));
    }

    @Test
    void isValidName_ValidNames() {
        assertTrue(InputValidator.isValidName("John Doe"));
        assertTrue(InputValidator.isValidName("Mary-Jane Watson"));
        assertTrue(InputValidator.isValidName("O'Connor"));
        assertTrue(InputValidator.isValidName("José García"));
        assertTrue(InputValidator.isValidName("Al"));
    }

    @Test
    void isValidName_InvalidNames() {
        assertFalse(InputValidator.isValidName(null));
        assertFalse(InputValidator.isValidName(""));
        assertFalse(InputValidator.isValidName("   "));
        assertFalse(InputValidator.isValidName("A"));  // Too short
        assertFalse(InputValidator.isValidName("John123"));  // Contains numbers
        assertFalse(InputValidator.isValidName("John@Doe"));  // Contains special characters
    }

    @Test
    void isNotEmpty() {
        assertTrue(InputValidator.isNotEmpty("test"));
        assertTrue(InputValidator.isNotEmpty("  test  "));
        assertFalse(InputValidator.isNotEmpty(null));
        assertFalse(InputValidator.isNotEmpty(""));
        assertFalse(InputValidator.isNotEmpty("   "));
    }

    @Test
    void normalizePhone() {
        assertEquals("+1-555-0123", InputValidator.normalizePhone("+1-555-0123"));
        assertEquals("555123-4567", InputValidator.normalizePhone("(555) 123-4567"));
        assertEquals("+15550123", InputValidator.normalizePhone("+1 555 0123"));
        assertNull(InputValidator.normalizePhone(null));
    }

    @Test
    void normalizeEmail() {
        assertEquals("test@example.com", InputValidator.normalizeEmail("TEST@EXAMPLE.COM"));
        assertEquals("user@domain.org", InputValidator.normalizeEmail("  user@domain.org  "));
        assertNull(InputValidator.normalizeEmail(null));
    }

    @Test
    void normalizeName() {
        assertEquals("John Doe", InputValidator.normalizeName("john doe"));
        assertEquals("Mary-Jane Watson", InputValidator.normalizeName("MARY-JANE WATSON"));
        assertEquals("O'Connor", InputValidator.normalizeName("o'connor"));
        assertEquals("Test", InputValidator.normalizeName("  test  "));
        assertNull(InputValidator.normalizeName(null));
    }

    @Test
    void isValidMenuChoice() {
        assertTrue(InputValidator.isValidMenuChoice("1", 1, 9));
        assertTrue(InputValidator.isValidMenuChoice("5", 1, 9));
        assertTrue(InputValidator.isValidMenuChoice("9", 1, 9));
        assertFalse(InputValidator.isValidMenuChoice("0", 1, 9));
        assertFalse(InputValidator.isValidMenuChoice("10", 1, 9));
        assertFalse(InputValidator.isValidMenuChoice("abc", 1, 9));
        assertFalse(InputValidator.isValidMenuChoice(null, 1, 9));
        assertFalse(InputValidator.isValidMenuChoice("", 1, 9));
    }
}