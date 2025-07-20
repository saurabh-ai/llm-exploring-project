package com.javamastery.grades.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for StatisticsCalculator demonstrating generic utility methods.
 */
public class StatisticsCalculatorTest {

    @Test
    void testCalculateAverage() {
        List<Double> values = Arrays.asList(10.0, 20.0, 30.0, 40.0);
        double average = StatisticsCalculator.calculateAverage(values);
        assertEquals(25.0, average, 0.01);

        // Test with integers
        List<Integer> intValues = Arrays.asList(1, 2, 3, 4, 5);
        double intAverage = StatisticsCalculator.calculateAverage(intValues);
        assertEquals(3.0, intAverage, 0.01);

        // Test empty collection
        assertEquals(0.0, StatisticsCalculator.calculateAverage(Collections.emptyList()));

        // Test null collection
        assertEquals(0.0, StatisticsCalculator.calculateAverage(null));
    }

    @Test
    void testFindMinMax() {
        List<Double> values = Arrays.asList(10.0, 5.0, 30.0, 15.0);
        
        Optional<Double> min = StatisticsCalculator.findMin(values);
        assertTrue(min.isPresent());
        assertEquals(5.0, min.get());

        Optional<Double> max = StatisticsCalculator.findMax(values);
        assertTrue(max.isPresent());
        assertEquals(30.0, max.get());

        // Test empty collection
        assertTrue(StatisticsCalculator.findMin(Collections.<Double>emptyList()).isEmpty());
        assertTrue(StatisticsCalculator.findMax(Collections.<Double>emptyList()).isEmpty());
    }

    @Test
    void testCalculateMedian() {
        // Odd number of values
        List<Double> oddValues = Arrays.asList(1.0, 3.0, 5.0, 7.0, 9.0);
        assertEquals(5.0, StatisticsCalculator.calculateMedian(oddValues), 0.01);

        // Even number of values
        List<Double> evenValues = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        assertEquals(2.5, StatisticsCalculator.calculateMedian(evenValues), 0.01);

        // Single value
        List<Double> singleValue = Arrays.asList(42.0);
        assertEquals(42.0, StatisticsCalculator.calculateMedian(singleValue), 0.01);

        // Empty collection
        assertEquals(0.0, StatisticsCalculator.calculateMedian(Collections.emptyList()));
    }

    @Test
    void testCalculateStandardDeviation() {
        List<Double> values = Arrays.asList(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        double stdDev = StatisticsCalculator.calculateStandardDeviation(values);
        assertTrue(stdDev > 0);
        assertEquals(2.0, stdDev, 0.1);

        // Test with single value
        List<Double> singleValue = Arrays.asList(5.0);
        assertEquals(0.0, StatisticsCalculator.calculateStandardDeviation(singleValue));
    }

    @Test
    void testCalculatePercentile() {
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

        assertEquals(1.0, StatisticsCalculator.calculatePercentile(values, 0), 0.01);
        assertEquals(5.5, StatisticsCalculator.calculatePercentile(values, 50), 0.01);
        assertEquals(10.0, StatisticsCalculator.calculatePercentile(values, 100), 0.01);

        // Test edge cases
        assertEquals(0.0, StatisticsCalculator.calculatePercentile(Collections.emptyList(), 50));
        assertEquals(0.0, StatisticsCalculator.calculatePercentile(values, -1));
        assertEquals(0.0, StatisticsCalculator.calculatePercentile(values, 101));
    }

    @Test
    void testCalculateQuartiles() {
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
        Map<String, Double> quartiles = StatisticsCalculator.calculateQuartiles(values);

        assertTrue(quartiles.containsKey("Q1"));
        assertTrue(quartiles.containsKey("Q2"));
        assertTrue(quartiles.containsKey("Q3"));

        assertEquals(2.75, quartiles.get("Q1"), 0.01);
        assertEquals(4.5, quartiles.get("Q2"), 0.01);
        assertEquals(6.25, quartiles.get("Q3"), 0.01);
    }

    @Test
    void testDescriptiveStatistics() {
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Map<String, Double> stats = StatisticsCalculator.calculateDescriptiveStatistics(values);

        assertFalse(stats.isEmpty());
        assertTrue(stats.containsKey("count"));
        assertTrue(stats.containsKey("mean"));
        assertTrue(stats.containsKey("median"));
        assertTrue(stats.containsKey("standardDeviation"));
        assertTrue(stats.containsKey("minimum"));
        assertTrue(stats.containsKey("maximum"));

        assertEquals(5.0, stats.get("count"));
        assertEquals(3.0, stats.get("mean"), 0.01);
        assertEquals(3.0, stats.get("median"), 0.01);
        assertEquals(1.0, stats.get("minimum"));
        assertEquals(5.0, stats.get("maximum"));
    }

    @Test
    void testDetectOutliers() {
        // Dataset with outliers
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 100.0); // 100 is an outlier
        List<Double> outliers = StatisticsCalculator.detectOutliers(values);

        assertFalse(outliers.isEmpty());
        assertTrue(outliers.contains(100.0));

        // Dataset without outliers
        List<Double> normalValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> noOutliers = StatisticsCalculator.detectOutliers(normalValues);
        assertTrue(noOutliers.isEmpty());
    }

    @Test
    void testCalculateCorrelation() {
        List<Double> x = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> y = Arrays.asList(2.0, 4.0, 6.0, 8.0, 10.0); // Perfect positive correlation

        double correlation = StatisticsCalculator.calculateCorrelation(x, y);
        assertEquals(1.0, correlation, 0.01);

        List<Double> negY = Arrays.asList(10.0, 8.0, 6.0, 4.0, 2.0); // Perfect negative correlation
        double negCorrelation = StatisticsCalculator.calculateCorrelation(x, negY);
        assertEquals(-1.0, negCorrelation, 0.01);

        // Test with different sized collections
        List<Double> shortX = Arrays.asList(1.0, 2.0);
        double noCorrelation = StatisticsCalculator.calculateCorrelation(shortX, y);
        assertEquals(0.0, noCorrelation);
    }

    @Test
    void testCalculateMovingAverage() {
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> movingAvg = StatisticsCalculator.calculateMovingAverage(values, 3);

        assertEquals(3, movingAvg.size());
        assertEquals(2.0, movingAvg.get(0), 0.01); // Average of 1,2,3
        assertEquals(3.0, movingAvg.get(1), 0.01); // Average of 2,3,4
        assertEquals(4.0, movingAvg.get(2), 0.01); // Average of 3,4,5

        // Test edge cases
        assertTrue(StatisticsCalculator.calculateMovingAverage(values, 0).isEmpty());
        assertTrue(StatisticsCalculator.calculateMovingAverage(values, 10).isEmpty());
    }

    @Test
    void testCreateHistogram() {
        List<Double> values = Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0);
        Map<String, Integer> histogram = StatisticsCalculator.createHistogram(values, 4);

        assertEquals(4, histogram.size());
        
        // Check that all bins have some counts
        int totalCount = histogram.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(9, totalCount);

        // Test edge cases
        assertTrue(StatisticsCalculator.createHistogram(Collections.emptyList(), 3).isEmpty());
        assertTrue(StatisticsCalculator.createHistogram(values, 0).isEmpty());
    }

    @Test
    void testCoefficientOfVariation() {
        List<Double> values = Arrays.asList(10.0, 12.0, 14.0, 16.0, 18.0);
        double cv = StatisticsCalculator.calculateCoefficientOfVariation(values);
        
        assertTrue(cv > 0);
        assertTrue(cv < 100); // Should be reasonable percentage
    }

    @Test
    void testCalculateSkewness() {
        // Symmetric distribution (approximately normal)
        List<Double> symmetric = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        double skewness = StatisticsCalculator.calculateSkewness(symmetric);
        assertEquals(0.0, skewness, 0.1); // Should be close to 0 for symmetric data

        // Test with insufficient data
        List<Double> twoValues = Arrays.asList(1.0, 2.0);
        assertEquals(0.0, StatisticsCalculator.calculateSkewness(twoValues));
    }
}