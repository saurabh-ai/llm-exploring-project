package com.javamastery.dataprocessor.collector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Custom collectors demonstrating advanced stream aggregation patterns
 * Shows implementation of Collector interface and functional composition
 */
public class CustomCollectors {
    
    /**
     * Collector that creates statistics for numeric data
     */
    public static <T> Collector<T, ?, Statistics> toStatistics(ToDoubleFunction<T> mapper) {
        return Collector.of(
            () -> new StatisticsAccumulator(),
            (acc, item) -> acc.accept(mapper.applyAsDouble(item)),
            StatisticsAccumulator::combine,
            StatisticsAccumulator::getStatistics
        );
    }
    
    /**
     * Collector that groups by key but only includes groups with minimum count
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingByWithMinCount(
            Function<T, K> classifier, long minCount) {
        return Collector.<T, Map<K, List<T>>, Map<K, List<T>>>of(
            HashMap::new,
            (map, item) -> map.computeIfAbsent(classifier.apply(item), k -> new ArrayList<>()).add(item),
            (map1, map2) -> {
                map2.forEach((key, list) -> 
                    map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                return map1;
            },
            map -> map.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= minCount)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
    
    /**
     * Collector that creates buckets based on numeric ranges
     */
    public static <T> Collector<T, ?, Map<String, List<T>>> toBuckets(
            ToDoubleFunction<T> valueFunction, double... bucketLimits) {
        
        Arrays.sort(bucketLimits);
        
        return Collector.of(
            HashMap::new,
            (map, item) -> {
                double value = valueFunction.applyAsDouble(item);
                String bucket = getBucketName(value, bucketLimits);
                map.computeIfAbsent(bucket, k -> new ArrayList<>()).add(item);
            },
            (map1, map2) -> {
                map2.forEach((bucket, list) -> 
                    map1.computeIfAbsent(bucket, k -> new ArrayList<>()).addAll(list));
                return map1;
            }
        );
    }
    
    /**
     * Collector that finds the most frequent elements
     */
    public static <T> Collector<T, ?, List<Map.Entry<T, Long>>> toTopFrequent(int limit) {
        return Collector.<T, Map<T, Long>, List<Map.Entry<T, Long>>>of(
            HashMap::new,
            (map, item) -> map.merge(item, 1L, Long::sum),
            (map1, map2) -> {
                map2.forEach((key, count) -> map1.merge(key, count, Long::sum));
                return map1;
            },
            map -> map.entrySet().stream()
                .sorted(Map.Entry.<T, Long>comparingByValue().reversed())
                .limit(limit)
                .toList()
        );
    }
    
    /**
     * Parallel-safe collector for concurrent operations
     */
    public static <T, K> Collector<T, ?, ConcurrentHashMap<K, Long>> toConcurrentHistogram(
            Function<T, K> classifier) {
        return Collector.of(
            ConcurrentHashMap::new,
            (map, item) -> map.merge(classifier.apply(item), 1L, Long::sum),
            (map1, map2) -> {
                map2.forEach((key, count) -> map1.merge(key, count, Long::sum));
                return map1;
            },
            Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Collector that partitions data into multiple predicates
     */
    public static <T> Collector<T, ?, Map<String, List<T>>> partitioningByMultiple(
            Map<String, Predicate<T>> predicates) {
        return Collector.of(
            HashMap::new,
            (map, item) -> {
                predicates.forEach((label, predicate) -> {
                    if (predicate.test(item)) {
                        map.computeIfAbsent(label, k -> new ArrayList<>()).add(item);
                    }
                });
            },
            (map1, map2) -> {
                map2.forEach((label, list) -> 
                    map1.computeIfAbsent(label, k -> new ArrayList<>()).addAll(list));
                return map1;
            }
        );
    }
    
    // Helper method for bucket naming
    private static String getBucketName(double value, double[] limits) {
        for (int i = 0; i < limits.length; i++) {
            if (value <= limits[i]) {
                if (i == 0) {
                    return "0-" + limits[i];
                } else {
                    return limits[i-1] + "-" + limits[i];
                }
            }
        }
        return limits[limits.length-1] + "+";
    }
    
    /**
     * Statistics accumulator for custom collector
     */
    private static class StatisticsAccumulator {
        private long count = 0;
        private double sum = 0.0;
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;
        
        public void accept(double value) {
            count++;
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        
        public StatisticsAccumulator combine(StatisticsAccumulator other) {
            StatisticsAccumulator result = new StatisticsAccumulator();
            result.count = this.count + other.count;
            result.sum = this.sum + other.sum;
            result.min = Math.min(this.min, other.min);
            result.max = Math.max(this.max, other.max);
            return result;
        }
        
        public Statistics getStatistics() {
            return new Statistics(count, sum, count > 0 ? sum / count : 0.0, 
                               count > 0 ? min : 0.0, count > 0 ? max : 0.0);
        }
    }
    
    /**
     * Statistics record for aggregated data
     */
    public record Statistics(
        long count,
        double sum,
        double average,
        double min,
        double max
    ) {
        @Override
        public String toString() {
            return String.format("Statistics{count=%d, sum=%.2f, avg=%.2f, min=%.2f, max=%.2f}",
                    count, sum, average, min, max);
        }
    }
}