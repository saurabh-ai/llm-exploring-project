package com.javamastery.llm.analysis;

/**
 * Represents a word and its frequency count.
 * 
 * @author Java Mastery Student
 */
public class WordFrequencyEntry {
    private final String word;
    private final long count;
    
    public WordFrequencyEntry(String word, long count) {
        this.word = word;
        this.count = count;
    }
    
    public String getWord() { return word; }
    public long getCount() { return count; }
    
    @Override
    public String toString() {
        return String.format("%s: %d", word, count);
    }
}