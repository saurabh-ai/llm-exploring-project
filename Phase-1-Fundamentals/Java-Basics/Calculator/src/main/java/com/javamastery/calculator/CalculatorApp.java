package com.javamastery.calculator;

import java.util.Scanner;

/**
 * Command-line interface for the Calculator.
 *
 * Learning Objectives:
 * - Practice input handling and control structures
 * - Demonstrate simple user interaction
 *
 * Key Concepts Demonstrated:
 * - Scanner for console input
 * - Basic switch-case usage
 */
public class CalculatorApp {

    /**
     * Entry point for the calculator application.
     *
     * @param args program arguments (unused)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();

        System.out.println("Simple Calculator\nChoose operation: + - * /");
        String operation = scanner.nextLine();

        System.out.print("Enter first number: ");
        double a = scanner.nextDouble();
        System.out.print("Enter second number: ");
        double b = scanner.nextDouble();

        double result;
        try {
            switch (operation) {
                case "+":
                    result = calculator.add(a, b);
                    break;
                case "-":
                    result = calculator.subtract(a, b);
                    break;
                case "*":
                    result = calculator.multiply(a, b);
                    break;
                case "/":
                    result = calculator.divide(a, b);
                    break;
                default:
                    System.out.println("Unsupported operation");
                    scanner.close();
                    return;
            }
            System.out.println("Result: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        scanner.close();
    }
}
