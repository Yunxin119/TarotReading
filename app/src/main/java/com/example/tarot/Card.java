package com.example.tarot;

import android.graphics.Bitmap;

public class Card {
    private Bitmap frontImage;
    private Bitmap backImage;
    private boolean isFaceUp = false;
    private String name;
    private String description;

    public Card(Bitmap frontImage, Bitmap backImage, String name, String description) {
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.name = name;
        this.description = description;
    }

    // Getter
    public Bitmap getImage() {
        return isFaceUp ? frontImage : backImage;
    }

    public void flip() {
        isFaceUp = !isFaceUp;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
