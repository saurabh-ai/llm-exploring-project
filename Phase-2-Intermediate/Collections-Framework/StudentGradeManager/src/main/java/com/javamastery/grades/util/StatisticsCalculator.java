package com.javamastery.grades.util;

import com.javamastery.grades.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for statistical calculations using generics and bounded type parameters.
 * Demonstrates advanced generic programming with Number types.
 */
public final class StatisticsCalculator {

    private StatisticsCalculator() {
        // Utility class - prevent instantiation
    }

    /**
     * Generic method for calculating average of any Number collection.
     * Uses bounded type parameters and wildcard generics.
     */
    public static <T extends Number> double calculateAverage(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Generic method for finding minimum value in a collection.
     */
    public static <T extends Number & Comparable<T>> Optional<T> findMin(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }
        
        return values.stream().min(Comparator.naturalOrder());
    }

    /**
     * Generic method for finding maximum value in a collection.
     */
    public static <T extends Number & Comparable<T>> Optional<T> findMax(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }
        
        return values.stream().max(Comparator.naturalOrder());
    }

    /**
     * Calculates median for a collection of numbers.
     */
    public static <T extends Number> double calculateMedian(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        List<Double> sortedValues = values.stream()
                .mapToDouble(Number::doubleValue)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            return sortedValues.get(size / 2);
        }
    }

    /**
     * Calculates standard deviation for a collection of numbers.
     */
    public static <T extends Number> double calculateStandardDeviation(Collection<T> values) {
        if (values == null || values.size() < 2) {
            return 0.0;
        }

        double mean = calculateAverage(values);
        double variance = values.stream()
                .mapToDouble(Number::doubleValue)
                .map(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * Calculates specified percentile for a collection of numbers.
     */
    public static <T extends Number> double calculatePercentile(Collection<T> values, int percentile) {
        if (values == null || values.isEmpty() || percentile < 0 || percentile > 100) {
            return 0.0;
        }

        List<Double> sortedValues = values.stream()
                .mapToDouble(Number::doubleValue)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        if (percentile == 0) return sortedValues.get(0);
        if (percentile == 100) return sortedValues.get(sortedValues.size() - 1);

        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }

        double weight = index - lowerIndex;
        return sortedValues.get(lowerIndex) * (1 - weight) + sortedValues.get(upperIndex) * weight;
    }

    /**
     * Calculates quartiles (25th, 50th, 75th percentiles) for a collection.
     */
    public static <T extends Number> Map<String, Double> calculateQuartiles(Collection<T> values) {
        Map<String, Double> quartiles = new LinkedHashMap<>();
        quartiles.put("Q1", calculatePercentile(values, 25));
        quartiles.put("Q2", calculatePercentile(values, 50));  // Median
        quartiles.put("Q3", calculatePercentile(values, 75));
        return quartiles;
    }

    /**
     * Calculates interquartile range (IQR).
     */
    public static <T extends Number> double calculateIQR(Collection<T> values) {
        double q1 = calculatePercentile(values, 25);
        double q3 = calculatePercentile(values, 75);
        return q3 - q1;
    }

    /**
     * Calculates comprehensive descriptive statistics.
     */
    public static <T extends Number> Map<String, Double> calculateDescriptiveStatistics(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> stats = new LinkedHashMap<>();
        stats.put("count", (double) values.size());
        stats.put("sum", values.stream().mapToDouble(Number::doubleValue).sum());
        stats.put("mean", calculateAverage(values));
        stats.put("median", calculateMedian(values));
        stats.put("standardDeviation", calculateStandardDeviation(values));
        stats.put("variance", Math.pow(calculateStandardDeviation(values), 2));
        stats.put("minimum", findMin(values.stream().map(n -> (Double) n.doubleValue()).collect(Collectors.toList())).orElse(0.0));
        stats.put("maximum", findMax(values.stream().map(n -> (Double) n.doubleValue()).collect(Collectors.toList())).orElse(0.0));
        stats.put("range", stats.get("maximum") - stats.get("minimum"));
        stats.put("iqr", calculateIQR(values));
        
        // Add percentiles
        stats.put("percentile10", calculatePercentile(values, 10));
        stats.put("percentile25", calculatePercentile(values, 25));
        stats.put("percentile75", calculatePercentile(values, 75));
        stats.put("percentile90", calculatePercentile(values, 90));
        
        return stats;
    }

    /**
     * Detects outliers using the IQR method.
     */
    public static <T extends Number> List<T> detectOutliers(Collection<T> values) {
        if (values == null || values.size() < 4) {
            return Collections.emptyList();
        }

        double q1 = calculatePercentile(values, 25);
        double q3 = calculatePercentile(values, 75);
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        return values.stream()
                .filter(value -> {
                    double doubleValue = value.doubleValue();
                    return doubleValue < lowerBound || doubleValue > upperBound;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates correlation coefficient between two numeric collections.
     */
    public static <T extends Number, U extends Number> double calculateCorrelation(
            Collection<T> xValues, Collection<U> yValues) {
        
        if (xValues == null || yValues == null || xValues.size() != yValues.size() || xValues.size() < 2) {
            return 0.0;
        }

        List<Double> x = xValues.stream().mapToDouble(Number::doubleValue).boxed().collect(Collectors.toList());
        List<Double> y = yValues.stream().mapToDouble(Number::doubleValue).boxed().collect(Collectors.toList());

        double meanX = calculateAverage(x);
        double meanY = calculateAverage(y);

        double numerator = 0.0;
        double sumXSquared = 0.0;
        double sumYSquared = 0.0;

        for (int i = 0; i < x.size(); i++) {
            double devX = x.get(i) - meanX;
            double devY = y.get(i) - meanY;
            numerator += devX * devY;
            sumXSquared += devX * devX;
            sumYSquared += devY * devY;
        }

        double denominator = Math.sqrt(sumXSquared * sumYSquared);
        return denominator != 0 ? numerator / denominator : 0.0;
    }

    /**
     * Calculates z-scores for a collection of values.
     */
    public static <T extends Number> List<Double> calculateZScores(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        double mean = calculateAverage(values);
        double stdDev = calculateStandardDeviation(values);

        if (stdDev == 0) {
            return Collections.nCopies(values.size(), 0.0);
        }

        return values.stream()
                .mapToDouble(Number::doubleValue)
                .map(value -> (value - mean) / stdDev)
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Calculates moving average for a series of values.
     */
    public static <T extends Number> List<Double> calculateMovingAverage(List<T> values, int windowSize) {
        if (values == null || values.isEmpty() || windowSize <= 0 || windowSize > values.size()) {
            return Collections.emptyList();
        }

        List<Double> movingAverages = new ArrayList<>();

        for (int i = 0; i <= values.size() - windowSize; i++) {
            List<T> window = values.subList(i, i + windowSize);
            movingAverages.add(calculateAverage(window));
        }

        return movingAverages;
    }

    /**
     * Groups values into bins for histogram creation.
     */
    public static <T extends Number> Map<String, Integer> createHistogram(Collection<T> values, int binCount) {
        if (values == null || values.isEmpty() || binCount <= 0) {
            return Collections.emptyMap();
        }

        double min = findMin(values.stream().map(n -> (Double) n.doubleValue()).collect(Collectors.toList())).orElse(0.0);
        double max = findMax(values.stream().map(n -> (Double) n.doubleValue()).collect(Collectors.toList())).orElse(0.0);

        if (min == max) {
            Map<String, Integer> histogram = new LinkedHashMap<>();
            histogram.put(String.format("%.2f", min), values.size());
            return histogram;
        }

        double binWidth = (max - min) / binCount;
        Map<String, Integer> histogram = new LinkedHashMap<>();

        // Initialize bins
        for (int i = 0; i < binCount; i++) {
            double binStart = min + i * binWidth;
            double binEnd = min + (i + 1) * binWidth;
            String binLabel = String.format("%.2f-%.2f", binStart, binEnd);
            histogram.put(binLabel, 0);
        }

        // Count values in each bin
        for (T value : values) {
            double doubleValue = value.doubleValue();
            int binIndex = (int) ((doubleValue - min) / binWidth);
            
            // Handle the case where value equals max
            if (binIndex >= binCount) {
                binIndex = binCount - 1;
            }
            
            double binStart = min + binIndex * binWidth;
            double binEnd = min + (binIndex + 1) * binWidth;
            String binLabel = String.format("%.2f-%.2f", binStart, binEnd);
            histogram.merge(binLabel, 1, Integer::sum);
        }

        return histogram;
    }

    /**
     * Calculates coefficient of variation (standard deviation / mean).
     */
    public static <T extends Number> double calculateCoefficientOfVariation(Collection<T> values) {
        double mean = calculateAverage(values);
        if (mean == 0) return 0.0;
        
        double stdDev = calculateStandardDeviation(values);
        return (stdDev / Math.abs(mean)) * 100; // Return as percentage
    }

    /**
     * Calculates skewness (measure of asymmetry).
     */
    public static <T extends Number> double calculateSkewness(Collection<T> values) {
        if (values == null || values.size() < 3) {
            return 0.0;
        }

        double mean = calculateAverage(values);
        double stdDev = calculateStandardDeviation(values);
        
        if (stdDev == 0) return 0.0;

        double n = values.size();
        double sum = values.stream()
                .mapToDouble(Number::doubleValue)
                .map(x -> Math.pow((x - mean) / stdDev, 3))
                .sum();

        return (n / ((n - 1) * (n - 2))) * sum;
    }
}