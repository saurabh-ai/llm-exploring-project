package com.javamastery.banking.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utility class for validating and parsing user input.
 */
public class InputValidator {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private InputValidator() {}
    
    /**
     * Validates and parses a BigDecimal amount from user input.
     * 
     * @param prompt the prompt to display to the user
     * @param allowZero whether zero values are allowed
     * @return the validated BigDecimal amount
     */
    public static BigDecimal getValidAmount(String prompt, boolean allowZero) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Please enter a valid amount.");
                    continue;
                }
                
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Amount cannot be negative. Please try again.");
                    continue;
                }
                
                if (!allowZero && amount.compareTo(BigDecimal.ZERO) == 0) {
                    System.out.println("Amount must be greater than zero. Please try again.");
                    continue;
                }
                
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount format. Please enter a valid number.");
            }
        }
    }
    
    /**
     * Validates and retrieves a non-empty string from user input.
     * 
     * @param prompt the prompt to display to the user
     * @return the validated string
     */
    public static String getValidString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }
    
    /**
     * Validates and retrieves an integer within the specified range.
     * 
     * @param prompt the prompt to display to the user
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @return the validated integer
     */
    public static int getValidInteger(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Please enter a valid number.");
                    continue;
                }
                
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid integer.");
            }
        }
    }
    
    /**
     * Prompts the user for a yes/no confirmation.
     * 
     * @param prompt the prompt to display to the user
     * @return true if the user confirms (y/yes), false otherwise
     */
    public static boolean getConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        }
    }
    
    /**
     * Validates an account number format (8 digits).
     * 
     * @param accountNumber the account number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = accountNumber.trim();
        return trimmed.matches("\\d{8}");
    }
}