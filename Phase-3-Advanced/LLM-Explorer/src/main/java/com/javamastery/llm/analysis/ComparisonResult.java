package com.javamastery.llm.analysis;

import java.util.Objects;
import java.util.Set;

/**
 * Immutable class representing the comparison results between two sets of LLM responses.
 * 
 * @author Java Mastery Student
 */
public class ComparisonResult {
    
    private final AnalysisResult analysis1;
    private final AnalysisResult analysis2;
    private final double responseTimeDifference;
    private final double averageWordsDifference;
    private final double vocabularyOverlap;
    private final Set<String> commonWords;
    
    public ComparisonResult(AnalysisResult analysis1, AnalysisResult analysis2,
                           double responseTimeDifference, double averageWordsDifference,
                           double vocabularyOverlap, Set<String> commonWords) {
        this.analysis1 = analysis1;
        this.analysis2 = analysis2;
        this.responseTimeDifference = responseTimeDifference;
        this.averageWordsDifference = averageWordsDifference;
        this.vocabularyOverlap = vocabularyOverlap;
        this.commonWords = Set.copyOf(commonWords);
    }
    
    // Getters
    public AnalysisResult getAnalysis1() { return analysis1; }
    public AnalysisResult getAnalysis2() { return analysis2; }
    public double getResponseTimeDifference() { return responseTimeDifference; }
    public double getAverageWordsDifference() { return averageWordsDifference; }
    public double getVocabularyOverlap() { return vocabularyOverlap; }
    public Set<String> getCommonWords() { return commonWords; }
    
    @Override
    public String toString() {
        return String.format("ComparisonResult{\n" +
                           "  Response Time Difference: %.2f ms\n" +
                           "  Average Words Difference: %.2f\n" +
                           "  Vocabulary Overlap: %.2f%%\n" +
                           "  Common Words: %d\n" +
                           "}", 
                           responseTimeDifference, averageWordsDifference,
                           vocabularyOverlap * 100, commonWords.size());
    }
}