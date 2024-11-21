package com.group5.tarotreading.result;

import java.util.ArrayList;

public class TarotResponse {
    private String[] interpretations;
    private int currentIndex;

    public TarotResponse(String response) {
        // Clean and split the response
        response = response.trim().replaceAll("\\s+", " ");
        this.interpretations = response.split("\\#\\#");
        this.currentIndex = 0;
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

    public boolean isValid() {
        return interpretations != null && interpretations.length > 0;
    }
}
