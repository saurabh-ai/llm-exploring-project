package com.javamastery.banking.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InputValidator utility.
 */
class InputValidatorTest {
    
    @Test
    void testValidateAccountNumber() {
        assertTrue(InputValidator.validateAccountNumber("12345678"));
        assertTrue(InputValidator.validateAccountNumber("10000000"));
        
        assertFalse(InputValidator.validateAccountNumber("1234567")); // Too short
        assertFalse(InputValidator.validateAccountNumber("123456789")); // Too long
        assertFalse(InputValidator.validateAccountNumber("1234567a")); // Contains letter
        assertFalse(InputValidator.validateAccountNumber(null));
        assertFalse(InputValidator.validateAccountNumber(""));
    }
    
    @Test
    void testValidateAmount() {
        assertTrue(InputValidator.validateAmount(new BigDecimal("100.00")));
        assertTrue(InputValidator.validateAmount(new BigDecimal("0.01")));
        
        assertFalse(InputValidator.validateAmount(new BigDecimal("0")));
        assertFalse(InputValidator.validateAmount(new BigDecimal("-100")));
        assertFalse(InputValidator.validateAmount(null));
    }
    
    @Test
    void testValidateAccountType() {
        assertTrue(InputValidator.validateAccountType("checking"));
        assertTrue(InputValidator.validateAccountType("savings"));
        assertTrue(InputValidator.validateAccountType("business"));
        assertTrue(InputValidator.validateAccountType("CHECKING")); // Case insensitive
        assertTrue(InputValidator.validateAccountType(" savings ")); // Whitespace trimmed
        
        assertFalse(InputValidator.validateAccountType("invalid"));
        assertFalse(InputValidator.validateAccountType(null));
        assertFalse(InputValidator.validateAccountType(""));
    }
    
    @Test
    void testValidateName() {
        assertTrue(InputValidator.validateName("John Doe"));
        assertTrue(InputValidator.validateName("Mary-Jane Smith"));
        assertTrue(InputValidator.validateName("James O'Connor"));
        assertTrue(InputValidator.validateName("Dr. Smith"));
        
        assertFalse(InputValidator.validateName("J")); // Too short
        assertFalse(InputValidator.validateName("John123")); // Contains numbers
        assertFalse(InputValidator.validateName("John@Doe")); // Invalid characters
        assertFalse(InputValidator.validateName(null));
        assertFalse(InputValidator.validateName(""));
    }
    
    @Test
    void testValidateBusinessName() {
        assertTrue(InputValidator.validateBusinessName("Wilson LLC"));
        assertTrue(InputValidator.validateBusinessName("Smith & Associates"));
        assertTrue(InputValidator.validateBusinessName("Tech Solutions, Inc."));
        assertTrue(InputValidator.validateBusinessName("123 Main Street Corp"));
        
        assertFalse(InputValidator.validateBusinessName("A")); // Too short
        assertFalse(InputValidator.validateBusinessName(null));
        assertFalse(InputValidator.validateBusinessName(""));
    }
    
    @Test
    void testValidateTaxId() {
        assertTrue(InputValidator.validateTaxId("12-3456789"));
        assertTrue(InputValidator.validateTaxId("98-7654321"));
        
        assertFalse(InputValidator.validateTaxId("123456789")); // Missing dash
        assertFalse(InputValidator.validateTaxId("12-345678")); // Too short
        assertFalse(InputValidator.validateTaxId("12-34567890")); // Too long
        assertFalse(InputValidator.validateTaxId("1a-3456789")); // Contains letter
        assertFalse(InputValidator.validateTaxId(null));
        assertFalse(InputValidator.validateTaxId(""));
    }
    
    @Test
    void testValidateInitialDeposit() {
        // Checking account - no minimum
        assertTrue(InputValidator.validateInitialDeposit("checking", new BigDecimal("1.00")));
        assertTrue(InputValidator.validateInitialDeposit("checking", new BigDecimal("0.01")));
        
        // Savings account - $100 minimum
        assertTrue(InputValidator.validateInitialDeposit("savings", new BigDecimal("100.00")));
        assertTrue(InputValidator.validateInitialDeposit("savings", new BigDecimal("500.00")));
        assertFalse(InputValidator.validateInitialDeposit("savings", new BigDecimal("99.99")));
        
        // Business account - $10,000 minimum
        assertTrue(InputValidator.validateInitialDeposit("business", new BigDecimal("10000.00")));
        assertTrue(InputValidator.validateInitialDeposit("business", new BigDecimal("15000.00")));
        assertFalse(InputValidator.validateInitialDeposit("business", new BigDecimal("9999.99")));
        
        // Invalid inputs
        assertFalse(InputValidator.validateInitialDeposit("invalid", new BigDecimal("100.00")));
        assertFalse(InputValidator.validateInitialDeposit("checking", new BigDecimal("-100.00")));
        assertFalse(InputValidator.validateInitialDeposit("checking", null));
    }
    
    @Test
    void testFormatName() {
        assertEquals("John Doe", InputValidator.formatName("john doe"));
        assertEquals("Mary-Jane Smith", InputValidator.formatName("mary-jane smith"));
        assertEquals("James O'Connor", InputValidator.formatName("JAMES O'CONNOR"));
        assertEquals("Dr. Smith", InputValidator.formatName("dr. smith"));
        assertEquals("John Doe", InputValidator.formatName("  john doe  ")); // Trim whitespace
        
        assertNull(InputValidator.formatName(null));
        assertNull(InputValidator.formatName(""));
        assertNull(InputValidator.formatName("   "));
    }
    
    @Test
    void testNormalizeAccountType() {
        assertEquals("Checking", InputValidator.normalizeAccountType("checking"));
        assertEquals("Savings", InputValidator.normalizeAccountType("SAVINGS"));
        assertEquals("Business", InputValidator.normalizeAccountType(" business "));
        
        assertNull(InputValidator.normalizeAccountType("invalid"));
        assertNull(InputValidator.normalizeAccountType(null));
        assertNull(InputValidator.normalizeAccountType(""));
    }
    
    @Test
    void testGetMinimumDeposit() {
        assertEquals(BigDecimal.ZERO, InputValidator.getMinimumDeposit("checking"));
        assertEquals(new BigDecimal("100.00"), InputValidator.getMinimumDeposit("savings"));
        assertEquals(new BigDecimal("10000.00"), InputValidator.getMinimumDeposit("business"));
        
        assertNull(InputValidator.getMinimumDeposit("invalid"));
        assertNull(InputValidator.getMinimumDeposit(null));
    }
}