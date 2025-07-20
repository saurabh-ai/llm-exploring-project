package com.javamastery.streams.collector;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * Custom collectors for advanced stream operations
 * Demonstrates implementation of Collector interface and functional programming concepts
 */
public class CustomCollectors {
    
    /**
     * Collector to calculate statistics (count, sum, average, min, max)
     */
    public static <T> Collector<T, ?, Statistics> toStatistics(ToDoubleFunction<T> mapper) {
        return Collector.of(
            Statistics::new,
            (stats, item) -> stats.accept(mapper.applyAsDouble(item)),
            Statistics::combine,
            Function.identity()
        );
    }
    
    /**
     * Collector to group elements and count them, returning only groups with count > threshold
     */
    public static <T, K> Collector<T, ?, Map<K, Long>> groupingByWithMinCount(
            Function<T, K> classifier, long minCount) {
        return Collector.of(
            () -> new HashMap<K, Long>(),
            (map, item) -> map.merge(classifier.apply(item), 1L, Long::sum),
            (map1, map2) -> {
                map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
                return map1;
            },
            map -> map.entrySet().stream()
                .filter(entry -> entry.getValue() >= minCount)
                .collect(HashMap::new, 
                    (m, entry) -> m.put(entry.getKey(), entry.getValue()), 
                    HashMap::putAll)
        );
    }
    
    /**
     * Collector to create a comma-separated string with custom prefix and suffix
     */
    public static Collector<String, ?, String> toDelimitedString(
            String delimiter, String prefix, String suffix) {
        return Collector.of(
            StringBuilder::new,
            (sb, s) -> {
                if (sb.length() > 0) sb.append(delimiter);
                sb.append(s);
            },
            (sb1, sb2) -> {
                if (sb1.length() > 0 && sb2.length() > 0) {
                    sb1.append(delimiter);
                }
                return sb1.append(sb2);
            },
            sb -> prefix + sb.toString() + suffix
        );
    }
    
    /**
     * Collector to partition elements into buckets based on a value function
     */
    public static <T> Collector<T, ?, Map<String, List<T>>> toBuckets(
            ToDoubleFunction<T> valueFunction, double... bucketLimits) {
        Arrays.sort(bucketLimits);
        
        return Collector.of(
            () -> new HashMap<String, List<T>>(),
            (map, item) -> {
                double value = valueFunction.applyAsDouble(item);
                String bucket = getBucketName(value, bucketLimits);
                map.computeIfAbsent(bucket, k -> new ArrayList<>()).add(item);
            },
            (map1, map2) -> {
                map2.forEach((key, value) -> 
                    map1.merge(key, value, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    })
                );
                return map1;
            }
        );
    }
    
    /**
     * Collector to find the top N elements based on a comparator
     */
    public static <T> Collector<T, ?, List<T>> toTopN(int n, Comparator<T> comparator) {
        return Collector.of(
            () -> new PriorityQueue<>(comparator),
            (queue, item) -> {
                if (queue.size() < n) {
                    queue.offer(item);
                } else if (comparator.compare(item, queue.peek()) > 0) {
                    queue.poll();
                    queue.offer(item);
                }
            },
            (queue1, queue2) -> {
                for (T item : queue2) {
                    if (queue1.size() < n) {
                        queue1.offer(item);
                    } else if (comparator.compare(item, queue1.peek()) > 0) {
                        queue1.poll();
                        queue1.offer(item);
                    }
                }
                return queue1;
            },
            queue -> {
                List<T> result = new ArrayList<>(queue);
                result.sort(comparator.reversed());
                return result;
            }
        );
    }
    
    private static String getBucketName(double value, double[] bucketLimits) {
        for (int i = 0; i < bucketLimits.length; i++) {
            if (value <= bucketLimits[i]) {
                return i == 0 ? "0-" + bucketLimits[i] : bucketLimits[i-1] + "-" + bucketLimits[i];
            }
        }
        return bucketLimits[bucketLimits.length - 1] + "+";
    }
    
    /**
     * Statistics accumulator class
     */
    public static class Statistics {
        private long count = 0;
        private double sum = 0.0;
        private double min = Double.MAX_VALUE;
        private double max = Double.MIN_VALUE;
        
        public void accept(double value) {
            count++;
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        
        public Statistics combine(Statistics other) {
            if (other.count == 0) return this;
            if (this.count == 0) return other;
            
            Statistics result = new Statistics();
            result.count = this.count + other.count;
            result.sum = this.sum + other.sum;
            result.min = Math.min(this.min, other.min);
            result.max = Math.max(this.max, other.max);
            return result;
        }
        
        public long getCount() { return count; }
        public double getSum() { return sum; }
        public double getAverage() { return count > 0 ? sum / count : 0.0; }
        public double getMin() { return count > 0 ? min : 0.0; }
        public double getMax() { return count > 0 ? max : 0.0; }
        
        @Override
        public String toString() {
            return String.format("Statistics{count=%d, sum=%.2f, avg=%.2f, min=%.2f, max=%.2f}", 
                count, sum, getAverage(), getMin(), getMax());
        }
    }
}