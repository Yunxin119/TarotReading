package com.group5.tarotreading.result;

import java.util.ArrayList;

public class TarotResponse {
    private String[] interpretations;
    private int currentIndex;

    public TarotResponse(String response) {
        // Clean and split the response
        response = response.trim();
        this.interpretations = response.split("\\#\\#");
        // Format each section
        for (int i = 0; i < interpretations.length; i++) {
            interpretations[i] = formatSection(interpretations[i]);
        }

        this.currentIndex = 0;
    }
    private String formatSection(String section) {
        // Clean the section text first
        section = section.trim();
        if (section.isEmpty()) return "";

        StringBuilder formatted = new StringBuilder();

        // Split into sentences
        String[] sentences = section.split("\\.");

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                // Add formatted sentence with double newline
                formatted.append(sentence)
                        .append(".")
                        .append("\n\n");
            }
        }

        // Clean up any extra newlines at the end
        return formatted.toString().trim();
    }

    public String getCurrentInterpretation() {
        return interpretations[currentIndex];
    }

    public boolean hasNext() {
        return currentIndex < interpretations.length - 1;
    }

    public String getNext() {
        if (hasNext()) {
            return interpretations[++currentIndex];
        }
        return null;
    }
    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    public String getPrevious() {
        if (hasPrevious()) {
            return interpretations[--currentIndex];
        }
        return null;
    }

    public boolean isValid() {
        return interpretations != null && interpretations.length > 0;
    }
}
