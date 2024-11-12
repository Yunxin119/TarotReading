package com.group5.tarotreading.cardActivity;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Random;

public class Card {
    private Bitmap frontImage;
    private Bitmap backImage;
    private boolean isFaceUp = false;
    private String name;
    private String description;

    private boolean isReversed;

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
        Random random = new Random();
        this.isReversed = random.nextBoolean();
        frontImage = isReversed ? getRotatedBitmap(frontImage) : frontImage;

        isFaceUp = !isFaceUp;
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(180);  // 旋转180度
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
