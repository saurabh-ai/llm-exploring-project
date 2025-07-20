package com.javamastery.dataprocessor.model;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Generic data container with advanced type safety and functional operations
 * Demonstrates bounded generics, wildcards, and functional interfaces
 */
public class DataContainer<T extends Comparable<T>> {
    
    private final List<T> items;
    
    public DataContainer() {
        this.items = new ArrayList<>();
    }
    
    public DataContainer(Collection<T> items) {
        this.items = new ArrayList<>(items);
    }
    
    // Bounded generic method with wildcard
    public <U extends T> void addAll(Collection<? extends U> newItems) {
        this.items.addAll(newItems);
    }
    
    // Generic method with functional interface
    public <R> List<R> map(Function<? super T, ? extends R> mapper) {
        return items.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    
    // Stream operations with predicates
    public List<T> filter(Predicate<? super T> predicate) {
        return items.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    // Advanced generic method with reducer - fixed generic signature
    public <R> R reduce(R identity, 
                       Function<? super T, R> mapper,
                       BinaryOperator<R> reducer) {
        return items.stream()
                .map(mapper)
                .reduce(identity, reducer);
    }
    
    // Generic grouping operation
    public <K> Map<K, List<T>> groupBy(Function<? super T, ? extends K> classifier) {
        return items.stream()
                .collect(Collectors.groupingBy(classifier));
    }
    
    // Bounded wildcard method for maximum type safety
    public void merge(DataContainer<? extends T> other) {
        this.items.addAll(other.getItems());
    }
    
    // Sorted view using Comparable constraint
    public List<T> getSorted() {
        return items.stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    // Custom comparator support
    public List<T> getSorted(Comparator<? super T> comparator) {
        return items.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    // Advanced aggregation operations
    public OptionalDouble getAverageDouble(Function<? super T, Double> extractor) {
        return items.stream()
                .mapToDouble(extractor::apply)
                .average();
    }
    
    public Optional<T> findFirst(Predicate<? super T> predicate) {
        return items.stream()
                .filter(predicate)
                .findFirst();
    }
    
    public long count(Predicate<? super T> predicate) {
        return items.stream()
                .filter(predicate)
                .count();
    }
    
    public boolean anyMatch(Predicate<? super T> predicate) {
        return items.stream().anyMatch(predicate);
    }
    
    public boolean allMatch(Predicate<? super T> predicate) {
        return items.stream().allMatch(predicate);
    }
    
    // Statistical operations for numeric types
    public static class Statistics {
        private final long count;
        private final double sum;
        private final double average;
        private final double min;
        private final double max;
        
        public Statistics(long count, double sum, double average, double min, double max) {
            this.count = count;
            this.sum = sum;
            this.average = average;
            this.min = min;
            this.max = max;
        }
        
        // Getters
        public long getCount() { return count; }
        public double getSum() { return sum; }
        public double getAverage() { return average; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        
        @Override
        public String toString() {
            return String.format("Statistics{count=%d, sum=%.2f, avg=%.2f, min=%.2f, max=%.2f}",
                    count, sum, average, min, max);
        }
    }
    
    public Statistics getStatistics(Function<? super T, Double> extractor) {
        DoubleSummaryStatistics stats = items.stream()
                .mapToDouble(extractor::apply)
                .summaryStatistics();
        
        return new Statistics(
                stats.getCount(),
                stats.getSum(),
                stats.getAverage(),
                stats.getMin(),
                stats.getMax()
        );
    }
    
    // Immutable view
    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public int size() {
        return items.size();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    @Override
    public String toString() {
        return "DataContainer{" + "items=" + items.size() + " elements}";
    }
}