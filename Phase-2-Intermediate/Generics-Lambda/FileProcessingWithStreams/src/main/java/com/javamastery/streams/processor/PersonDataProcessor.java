package com.javamastery.streams.processor;

import com.javamastery.streams.model.Person;
import com.javamastery.streams.collector.CustomCollectors;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Processor for Person data demonstrating various stream operations
 * Showcases filtering, mapping, collecting, grouping, and statistical operations
 */
public class PersonDataProcessor {
    
    private final List<Person> people;
    
    public PersonDataProcessor(List<Person> people) {
        this.people = Objects.requireNonNull(people, "People list cannot be null");
    }
    
    /**
     * Filter people by age range using functional predicates
     */
    public List<Person> filterByAgeRange(int minAge, int maxAge) {
        Predicate<Person> ageFilter = person -> person.age() >= minAge && person.age() <= maxAge;
        
        return people.stream()
            .filter(ageFilter)
            .sorted(Comparator.comparing(Person::age))
            .collect(Collectors.toList());
    }
    
    /**
     * Group people by department and calculate average salary per department
     */
    public Map<String, Double> getAverageSalaryByDepartment() {
        return people.stream()
            .collect(Collectors.groupingBy(
                Person::department,
                Collectors.averagingDouble(Person::salary)
            ));
    }
    
    /**
     * Find top N highest earners using custom collector
     */
    public List<Person> getTopEarners(int n) {
        return people.stream()
            .collect(CustomCollectors.toTopN(n, Comparator.comparing(Person::salary)));
    }
    
    /**
     * Get salary statistics using custom collector
     */
    public CustomCollectors.Statistics getSalaryStatistics() {
        return people.stream()
            .collect(CustomCollectors.toStatistics(Person::salary));
    }
    
    /**
     * Group people by country and city with nested grouping
     */
    public Map<String, Map<String, List<Person>>> groupByCountryAndCity() {
        return people.stream()
            .collect(Collectors.groupingBy(
                Person::country,
                Collectors.groupingBy(Person::city)
            ));
    }
    
    /**
     * Find people who joined in a specific year using streams
     */
    public List<Person> getPeopleJoinedInYear(int year) {
        return people.stream()
            .filter(person -> person.joinDate().getYear() == year)
            .sorted(Comparator.comparing(Person::joinDate))
            .collect(Collectors.toList());
    }
    
    /**
     * Create email list for people matching criteria
     */
    public List<String> getEmailsForHighEarningRockets(double minSalary) {
        return people.stream()
            .filter(person -> person.salary() >= minSalary)
            .filter(person -> person.yearsOfExperience() >= 3)
            .map(Person::email)
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Partition people into seniors and juniors
     */
    public Map<Boolean, List<Person>> partitionBySeniorStatus() {
        return people.stream()
            .collect(Collectors.partitioningBy(Person::isSenior));
    }
    
    /**
     * Calculate department demographics (count by age groups)
     */
    public Map<String, Map<String, Long>> getDepartmentDemographics() {
        Function<Person, String> ageGroupClassifier = person -> {
            int age = person.age();
            if (age < 30) return "Young (< 30)";
            else if (age < 50) return "Middle (30-49)";
            else return "Senior (50+)";
        };
        
        return people.stream()
            .collect(Collectors.groupingBy(
                Person::department,
                Collectors.groupingBy(
                    ageGroupClassifier,
                    Collectors.counting()
                )
            ));
    }
    
    /**
     * Find people with unique characteristics using complex filtering
     */
    public List<Person> findUniquePersons() {
        return people.stream()
            .filter(person -> person.salary() > 80_000)
            .filter(person -> person.yearsOfExperience() >= 5)
            .filter(person -> person.city().length() > 5)
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }
    
    /**
     * Generate summary report using various stream operations
     */
    public String generateSummaryReport() {
        long totalPeople = people.size();
        double averageSalary = people.stream()
            .mapToDouble(Person::salary)
            .average()
            .orElse(0.0);
        
        long highEarners = people.stream()
            .filter(Person::isHighEarner)
            .count();
        
        String topDepartments = people.stream()
            .collect(Collectors.groupingBy(Person::department, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
            .collect(CustomCollectors.toDelimitedString(", ", "[", "]"));
        
        return String.format(
            "Summary Report:%n" +
            "Total People: %d%n" +
            "Average Salary: $%.2f%n" +
            "High Earners (>$100K): %d (%.1f%%)%n" +
            "Top Departments: %s",
            totalPeople, averageSalary, highEarners, 
            (highEarners * 100.0 / totalPeople), topDepartments
        );
    }
    
    /**
     * Parallel processing example - find expensive operations
     */
    public List<Person> expensiveOperation() {
        return people.parallelStream()
            .filter(this::expensiveFilter)
            .map(this::expensiveTransformation)
            .sorted(Comparator.comparing(Person::salary).reversed())
            .collect(Collectors.toList());
    }
    
    private boolean expensiveFilter(Person person) {
        // Simulate expensive operation
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return person.salary() > 50_000;
    }
    
    private Person expensiveTransformation(Person person) {
        // Simulate expensive transformation
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Return same person for simplicity (in real scenario, you'd transform)
        return person;
    }
}