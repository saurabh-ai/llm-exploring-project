package com.javamastery.streams.collector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CustomCollectors
 */
@DisplayName("Custom Collectors Tests")
class CustomCollectorsTest {
    
    @Test
    @DisplayName("Should collect statistics correctly")
    void shouldCollectStatisticsCorrectly() {
        // Given
        List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        
        // When
        CustomCollectors.Statistics stats = numbers.stream()
            .collect(CustomCollectors.toStatistics(Double::doubleValue));
        
        // Then
        assertEquals(5, stats.getCount());
        assertEquals(15.0, stats.getSum(), 0.001);
        assertEquals(3.0, stats.getAverage(), 0.001);
        assertEquals(1.0, stats.getMin(), 0.001);
        assertEquals(5.0, stats.getMax(), 0.001);
    }
    
    @Test
    @DisplayName("Should collect statistics for empty stream")
    void shouldCollectStatisticsForEmptyStream() {
        // Given
        List<Double> numbers = List.of();
        
        // When
        CustomCollectors.Statistics stats = numbers.stream()
            .collect(CustomCollectors.toStatistics(Double::doubleValue));
        
        // Then
        assertEquals(0, stats.getCount());
        assertEquals(0.0, stats.getSum());
        assertEquals(0.0, stats.getAverage());
        assertEquals(0.0, stats.getMin());
        assertEquals(0.0, stats.getMax());
    }
    
    @Test
    @DisplayName("Should group by with minimum count")
    void shouldGroupByWithMinimumCount() {
        // Given
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "apple", "banana");
        
        // When
        Map<String, Long> result = words.stream()
            .collect(CustomCollectors.groupingByWithMinCount(s -> s, 2));
        
        // Then
        assertEquals(2, result.size());
        assertEquals(3L, result.get("apple"));
        assertEquals(2L, result.get("banana"));
        assertNull(result.get("cherry")); // Only appears once, below threshold
    }
    
    @Test
    @DisplayName("Should create delimited string with prefix and suffix")
    void shouldCreateDelimitedStringWithPrefixAndSuffix() {
        // Given
        List<String> items = Arrays.asList("apple", "banana", "cherry");
        
        // When
        String result = items.stream()
            .collect(CustomCollectors.toDelimitedString(", ", "[", "]"));
        
        // Then
        assertEquals("[apple, banana, cherry]", result);
    }
    
    @Test
    @DisplayName("Should handle empty stream for delimited string")
    void shouldHandleEmptyStreamForDelimitedString() {
        // Given
        List<String> items = List.of();
        
        // When
        String result = items.stream()
            .collect(CustomCollectors.toDelimitedString(", ", "[", "]"));
        
        // Then
        assertEquals("[]", result);
    }
    
    @Test
    @DisplayName("Should create buckets correctly")
    void shouldCreateBucketsCorrectly() {
        // Given
        List<TestItem> items = Arrays.asList(
            new TestItem(5.0),
            new TestItem(15.0),
            new TestItem(25.0),
            new TestItem(35.0),
            new TestItem(150.0)
        );
        
        // When
        Map<String, List<TestItem>> buckets = items.stream()
            .collect(CustomCollectors.toBuckets(TestItem::value, 10.0, 20.0, 30.0));
        
        // Then
        assertEquals(4, buckets.size());
        assertEquals(1, buckets.get("0-10.0").size()); // 5.0
        assertEquals(1, buckets.get("10.0-20.0").size()); // 15.0
        assertEquals(1, buckets.get("20.0-30.0").size()); // 25.0
        assertEquals(2, buckets.get("30.0+").size()); // 35.0, 150.0
    }
    
    @Test
    @DisplayName("Should collect top N elements")
    void shouldCollectTopNElements() {
        // Given
        List<Integer> numbers = Arrays.asList(1, 5, 3, 9, 2, 7, 4, 8, 6);
        
        // When
        List<Integer> topThree = numbers.stream()
            .collect(CustomCollectors.toTopN(3, Comparator.<Integer>naturalOrder()));
        
        // Then
        assertEquals(3, topThree.size());
        assertEquals(Arrays.asList(9, 8, 7), topThree); // Should be in descending order
    }
    
    @Test
    @DisplayName("Should handle top N when stream has fewer elements")
    void shouldHandleTopNWhenStreamHasFewerElements() {
        // Given
        List<Integer> numbers = Arrays.asList(1, 2);
        
        // When
        List<Integer> topFive = numbers.stream()
            .collect(CustomCollectors.toTopN(5, Comparator.<Integer>naturalOrder()));
        
        // Then
        assertEquals(2, topFive.size());
        assertEquals(Arrays.asList(2, 1), topFive);
    }
    
    @Test
    @DisplayName("Statistics should combine correctly")
    void statisticsShouldCombineCorrectly() {
        // Given
        CustomCollectors.Statistics stats1 = new CustomCollectors.Statistics();
        stats1.accept(1.0);
        stats1.accept(2.0);
        
        CustomCollectors.Statistics stats2 = new CustomCollectors.Statistics();
        stats2.accept(3.0);
        stats2.accept(4.0);
        
        // When
        CustomCollectors.Statistics combined = stats1.combine(stats2);
        
        // Then
        assertEquals(4, combined.getCount());
        assertEquals(10.0, combined.getSum());
        assertEquals(2.5, combined.getAverage());
        assertEquals(1.0, combined.getMin());
        assertEquals(4.0, combined.getMax());
    }
    
    @Test
    @DisplayName("Statistics should handle combining with empty statistics")
    void statisticsShouldHandleCombiningWithEmptyStatistics() {
        // Given
        CustomCollectors.Statistics stats1 = new CustomCollectors.Statistics();
        stats1.accept(1.0);
        stats1.accept(2.0);
        
        CustomCollectors.Statistics emptyStats = new CustomCollectors.Statistics();
        
        // When
        CustomCollectors.Statistics combined = stats1.combine(emptyStats);
        
        // Then
        assertEquals(2, combined.getCount());
        assertEquals(3.0, combined.getSum());
        assertEquals(1.5, combined.getAverage());
    }
    
    // Helper class for testing
    private record TestItem(double value) {}
}