package com.javamastery.banking.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for validating various inputs in the banking system.
 */
public class InputValidator {
    
    // Regular expressions for validation
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-\\.]{2,50}$");
    private static final Pattern BUSINESS_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s'&\\-\\.,]{2,100}$");
    private static final Pattern TAX_ID_PATTERN = Pattern.compile("^\\d{2}-\\d{7}$"); // Format: XX-XXXXXXX
    
    // Account type constants
    public static final String CHECKING_ACCOUNT = "checking";
    public static final String SAVINGS_ACCOUNT = "savings";
    public static final String BUSINESS_ACCOUNT = "business";

    /**
     * Validates an account number format.
     *
     * @param accountNumber The account number to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }

    /**
     * Validates a monetary amount.
     *
     * @param amount The amount to validate
     * @return true if valid (positive and not null), false otherwise
     */
    public static boolean validateAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validates an account type.
     *
     * @param accountType The account type to validate
     * @return true if valid account type, false otherwise
     */
    public static boolean validateAccountType(String accountType) {
        if (accountType == null) return false;
        
        String lowerType = accountType.toLowerCase().trim();
        return CHECKING_ACCOUNT.equals(lowerType) || 
               SAVINGS_ACCOUNT.equals(lowerType) || 
               BUSINESS_ACCOUNT.equals(lowerType);
    }

    /**
     * Validates a person's name.
     *
     * @param name The name to validate
     * @return true if valid name format, false otherwise
     */
    public static boolean validateName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates a business name.
     *
     * @param businessName The business name to validate
     * @return true if valid business name format, false otherwise
     */
    public static boolean validateBusinessName(String businessName) {
        return businessName != null && BUSINESS_NAME_PATTERN.matcher(businessName.trim()).matches();
    }

    /**
     * Validates a tax ID format.
     *
     * @param taxId The tax ID to validate
     * @return true if valid tax ID format, false otherwise
     */
    public static boolean validateTaxId(String taxId) {
        return taxId != null && TAX_ID_PATTERN.matcher(taxId.trim()).matches();
    }

    /**
     * Validates initial deposit amount based on account type.
     *
     * @param accountType The type of account
     * @param initialDeposit The initial deposit amount
     * @return true if deposit meets minimum requirements, false otherwise
     */
    public static boolean validateInitialDeposit(String accountType, BigDecimal initialDeposit) {
        if (!validateAmount(initialDeposit) || !validateAccountType(accountType)) {
            return false;
        }

        String lowerType = accountType.toLowerCase().trim();
        switch (lowerType) {
            case CHECKING_ACCOUNT:
                return true; // No minimum for checking
            case SAVINGS_ACCOUNT:
                return initialDeposit.compareTo(new BigDecimal("100.00")) >= 0;
            case BUSINESS_ACCOUNT:
                return initialDeposit.compareTo(new BigDecimal("10000.00")) >= 0;
            default:
                return false;
        }
    }

    /**
     * Sanitizes and formats a name by trimming whitespace and capitalizing properly.
     *
     * @param name The name to format
     * @return Formatted name
     */
    public static String formatName(String name) {
        if (name == null) return null;
        
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return null;
        
        // Capitalize first letter of each word
        StringBuilder formatted = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : trimmed.toCharArray()) {
            if (Character.isWhitespace(c) || c == '-' || c == '\'' || c == '.') {
                formatted.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                formatted.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                formatted.append(Character.toLowerCase(c));
            }
        }
        
        return formatted.toString();
    }

    /**
     * Normalizes account type to standard format.
     *
     * @param accountType The account type to normalize
     * @return Normalized account type or null if invalid
     */
    public static String normalizeAccountType(String accountType) {
        if (!validateAccountType(accountType)) return null;
        
        String lowerType = accountType.toLowerCase().trim();
        switch (lowerType) {
            case CHECKING_ACCOUNT:
                return "Checking";
            case SAVINGS_ACCOUNT:
                return "Savings";
            case BUSINESS_ACCOUNT:
                return "Business";
            default:
                return null;
        }
    }

    /**
     * Gets minimum deposit requirement for account type.
     *
     * @param accountType The account type
     * @return Minimum deposit amount
     */
    public static BigDecimal getMinimumDeposit(String accountType) {
        if (!validateAccountType(accountType)) return null;
        
        String lowerType = accountType.toLowerCase().trim();
        switch (lowerType) {
            case CHECKING_ACCOUNT:
                return BigDecimal.ZERO;
            case SAVINGS_ACCOUNT:
                return new BigDecimal("100.00");
            case BUSINESS_ACCOUNT:
                return new BigDecimal("10000.00");
            default:
                return null;
        }
    }
}