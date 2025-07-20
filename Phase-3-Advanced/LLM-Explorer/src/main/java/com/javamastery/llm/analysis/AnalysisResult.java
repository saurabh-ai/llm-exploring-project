package com.javamastery.llm.analysis;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable class representing the results of LLM response analysis.
 * Demonstrates value object pattern and comprehensive data encapsulation.
 * 
 * Learning Objectives:
 * - Immutable data class design
 * - Value object pattern
 * - Defensive copying of mutable objects
 * - Comprehensive toString implementation
 * 
 * @author Java Mastery Student
 */
public class AnalysisResult {
    
    private final int totalResponses;
    private final long totalWords;
    private final int totalCharacters;
    private final int totalSentences;
    private final double averageResponseTimeMs;
    private final double averageWordsPerResponse;
    private final Map<String, Long> wordFrequency;
    private final Map<String, Long> modelUsage;
    
    public AnalysisResult(int totalResponses, long totalWords, int totalCharacters, 
                         int totalSentences, double averageResponseTimeMs, 
                         double averageWordsPerResponse, Map<String, Long> wordFrequency, 
                         Map<String, Long> modelUsage) {
        this.totalResponses = totalResponses;
        this.totalWords = totalWords;
        this.totalCharacters = totalCharacters;
        this.totalSentences = totalSentences;
        this.averageResponseTimeMs = averageResponseTimeMs;
        this.averageWordsPerResponse = averageWordsPerResponse;
        this.wordFrequency = Map.copyOf(wordFrequency); // Defensive copy
        this.modelUsage = Map.copyOf(modelUsage); // Defensive copy
    }
    
    // Getters
    public int getTotalResponses() { return totalResponses; }
    public long getTotalWords() { return totalWords; }
    public int getTotalCharacters() { return totalCharacters; }
    public int getTotalSentences() { return totalSentences; }
    public double getAverageResponseTimeMs() { return averageResponseTimeMs; }
    public double getAverageWordsPerResponse() { return averageWordsPerResponse; }
    public Map<String, Long> getWordFrequency() { return wordFrequency; }
    public Map<String, Long> getModelUsage() { return modelUsage; }
    
    // Calculated properties
    public double getAverageCharactersPerResponse() {
        return totalResponses > 0 ? (double) totalCharacters / totalResponses : 0.0;
    }
    
    public double getAverageSentencesPerResponse() {
        return totalResponses > 0 ? (double) totalSentences / totalResponses : 0.0;
    }
    
    public int getUniqueWordCount() {
        return wordFrequency.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AnalysisResult that = (AnalysisResult) obj;
        return totalResponses == that.totalResponses &&
               totalWords == that.totalWords &&
               totalCharacters == that.totalCharacters &&
               totalSentences == that.totalSentences &&
               Double.compare(that.averageResponseTimeMs, averageResponseTimeMs) == 0 &&
               Double.compare(that.averageWordsPerResponse, averageWordsPerResponse) == 0 &&
               Objects.equals(wordFrequency, that.wordFrequency) &&
               Objects.equals(modelUsage, that.modelUsage);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(totalResponses, totalWords, totalCharacters, totalSentences,
                          averageResponseTimeMs, averageWordsPerResponse, wordFrequency, modelUsage);
    }
    
    @Override
    public String toString() {
        return String.format("AnalysisResult{\n" +
                           "  Total Responses: %d\n" +
                           "  Total Words: %d\n" +
                           "  Total Characters: %d\n" +
                           "  Total Sentences: %d\n" +
                           "  Average Response Time: %.2f ms\n" +
                           "  Average Words per Response: %.2f\n" +
                           "  Average Characters per Response: %.2f\n" +
                           "  Average Sentences per Response: %.2f\n" +
                           "  Unique Words: %d\n" +
                           "  Models Used: %s\n" +
                           "}", 
                           totalResponses, totalWords, totalCharacters, totalSentences,
                           averageResponseTimeMs, averageWordsPerResponse,
                           getAverageCharactersPerResponse(), getAverageSentencesPerResponse(),
                           getUniqueWordCount(), modelUsage.keySet());
    }
}