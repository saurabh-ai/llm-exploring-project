package com.javamastery.llm.analysis;

import com.javamastery.llm.client.LLMResponse;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for analyzing LLM responses using Stream API.
 * Demonstrates functional programming and text analysis techniques.
 * 
 * Learning Objectives:
 * - Stream API for data processing and analysis
 * - Functional programming with lambda expressions
 * - Method references and collectors
 * - Text analysis and pattern matching
 * 
 * Key Concepts Demonstrated:
 * - Functional-style operations on collections
 * - Custom collectors for complex aggregations
 * - Regular expressions for text processing
 * - Statistical analysis using streams
 * 
 * @author Java Mastery Student
 */
public class ResponseAnalyzer {
    
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+");
    
    /**
     * Analyzes a collection of LLM responses and returns comprehensive statistics.
     * Demonstrates Stream API usage for complex data analysis.
     * 
     * @param responses collection of LLM responses to analyze
     * @return analysis results
     */
    public static AnalysisResult analyzeResponses(Collection<LLMResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return new AnalysisResult(0, 0, 0, 0, 0.0, 0.0, 
                                    Collections.emptyMap(), Collections.emptyMap());
        }
        
        // Filter successful responses only
        List<LLMResponse> successfulResponses = responses.stream()
                                                        .filter(LLMResponse::isSuccess)
                                                        .collect(Collectors.toList());
        
        if (successfulResponses.isEmpty()) {
            return new AnalysisResult(0, 0, 0, 0, 0.0, 0.0, 
                                    Collections.emptyMap(), Collections.emptyMap());
        }
        
        // Calculate basic statistics using streams
        int totalResponses = successfulResponses.size();
        
        long totalWords = successfulResponses.stream()
                                           .mapToLong(LLMResponse::getWordCount)
                                           .sum();
        
        int totalCharacters = successfulResponses.stream()
                                                .mapToInt(LLMResponse::getContentLength)
                                                .sum();
        
        int totalSentences = successfulResponses.stream()
                                               .mapToInt(response -> countSentences(response.getContent()))
                                               .sum();
        
        double averageResponseTime = successfulResponses.stream()
                                                       .mapToLong(LLMResponse::getResponseTimeMs)
                                                       .average()
                                                       .orElse(0.0);
        
        double averageWordsPerResponse = (double) totalWords / totalResponses;
        
        // Analyze word frequency across all responses
        Map<String, Long> wordFrequency = analyzeWordFrequency(successfulResponses);
        
        // Analyze model usage
        Map<String, Long> modelUsage = successfulResponses.stream()
                                                         .collect(Collectors.groupingBy(
                                                             LLMResponse::getModel,
                                                             Collectors.counting()
                                                         ));
        
        return new AnalysisResult(totalResponses, totalWords, totalCharacters, 
                                totalSentences, averageResponseTime, averageWordsPerResponse,
                                wordFrequency, modelUsage);
    }
    
    /**
     * Compares two sets of responses and returns comparison metrics.
     * Demonstrates advanced Stream operations for data comparison.
     */
    public static ComparisonResult compareResponses(Collection<LLMResponse> responses1, 
                                                   Collection<LLMResponse> responses2) {
        AnalysisResult analysis1 = analyzeResponses(responses1);
        AnalysisResult analysis2 = analyzeResponses(responses2);
        
        double responseTimeDiff = analysis2.getAverageResponseTimeMs() - analysis1.getAverageResponseTimeMs();
        double avgWordsDiff = analysis2.getAverageWordsPerResponse() - analysis1.getAverageWordsPerResponse();
        
        // Calculate vocabulary overlap
        Set<String> vocab1 = analysis1.getWordFrequency().keySet();
        Set<String> vocab2 = analysis2.getWordFrequency().keySet();
        
        Set<String> commonWords = vocab1.stream()
                                       .filter(vocab2::contains)
                                       .collect(Collectors.toSet());
        
        double vocabularyOverlap = vocab1.isEmpty() || vocab2.isEmpty() ? 0.0 :
                                 (double) commonWords.size() / 
                                 Stream.concat(vocab1.stream(), vocab2.stream())
                                       .collect(Collectors.toSet()).size();
        
        return new ComparisonResult(analysis1, analysis2, responseTimeDiff, 
                                   avgWordsDiff, vocabularyOverlap, commonWords);
    }
    
    /**
     * Finds the most common words across all responses.
     * Demonstrates Stream API sorting and limiting operations.
     */
    public static List<WordFrequencyEntry> getTopWords(Collection<LLMResponse> responses, int limit) {
        return analyzeWordFrequency(responses).entrySet().stream()
                                             .map(entry -> new WordFrequencyEntry(entry.getKey(), entry.getValue()))
                                             .sorted(Comparator.comparing(WordFrequencyEntry::getCount).reversed())
                                             .limit(limit)
                                             .collect(Collectors.toList());
    }
    
    /**
     * Calculates response time percentiles.
     * Demonstrates statistical analysis using streams.
     */
    public static Map<String, Double> calculateResponseTimePercentiles(Collection<LLMResponse> responses) {
        List<Long> responseTimes = responses.stream()
                                          .filter(LLMResponse::isSuccess)
                                          .map(LLMResponse::getResponseTimeMs)
                                          .sorted()
                                          .collect(Collectors.toList());
        
        if (responseTimes.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<String, Double> percentiles = new HashMap<>();
        percentiles.put("50th", calculatePercentile(responseTimes, 50));
        percentiles.put("90th", calculatePercentile(responseTimes, 90));
        percentiles.put("95th", calculatePercentile(responseTimes, 95));
        percentiles.put("99th", calculatePercentile(responseTimes, 99));
        
        return percentiles;
    }
    
    /**
     * Analyzes word frequency across all responses.
     * Demonstrates complex stream operations with text processing.
     */
    private static Map<String, Long> analyzeWordFrequency(Collection<LLMResponse> responses) {
        return responses.stream()
                       .filter(LLMResponse::isSuccess)
                       .flatMap(response -> extractWords(response.getContent()))
                       .map(String::toLowerCase)
                       .filter(word -> word.length() > 2) // Filter short words
                       .collect(Collectors.groupingBy(
                           word -> word,
                           Collectors.counting()
                       ));
    }
    
    /**
     * Extracts individual words from text using regex and streams.
     */
    private static Stream<String> extractWords(String text) {
        return WORD_PATTERN.matcher(text)
                          .results()
                          .map(matchResult -> matchResult.group());
    }
    
    /**
     * Counts sentences in text using regex.
     */
    private static int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return SENTENCE_PATTERN.split(text).length;
    }
    
    /**
     * Calculates a specific percentile from a sorted list of values.
     */
    private static double calculatePercentile(List<Long> sortedValues, int percentile) {
        if (sortedValues.isEmpty()) {
            return 0.0;
        }
        
        int index = (int) Math.ceil((percentile / 100.0) * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(index, sortedValues.size() - 1));
        
        return sortedValues.get(index).doubleValue();
    }
}