package com.group5.tarotreading.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class OpenAIImageOptimizer {
    private static final String TAG = "OpenAIImageOptimizer";
    private static final int MAX_SIZE = 2048;
    private static final int TARGET_SHORT_SIDE = 768;
    private static final int TILE_SIZE = 512;

    public static class ImageDetails {
        public final Bitmap optimizedImage;
        public final int tokenCost;
        public final String detail;

        public ImageDetails(Bitmap optimizedImage, int tokenCost, String detail) {
            this.optimizedImage = optimizedImage;
            this.tokenCost = tokenCost;
            this.detail = detail;
        }
    }

    public static ImageDetails optimizeImage(Bitmap originalImage, boolean useHighDetail) {
        if (!useHighDetail) {
            // For low detail, return original image with fixed token cost
            return new ImageDetails(originalImage, 85, "low");
        }

        // Start optimization for high detail
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // First scale: fit within 2048x2048
        float scale = 1.0f;
        if (width > MAX_SIZE || height > MAX_SIZE) {
            scale = Math.min((float) MAX_SIZE / width, (float) MAX_SIZE / height);
            width = Math.round(width * scale);
            height = Math.round(height * scale);
        }

        // Second scale: shortest side to 768px
        int shortestSide = Math.min(width, height);
        if (shortestSide != TARGET_SHORT_SIDE) {
            scale = (float) TARGET_SHORT_SIDE / shortestSide;
            width = Math.round(width * scale);
            height = Math.round(height * scale);
        }

        // Calculate token cost
        int tilesWide = (int) Math.ceil((double) width / TILE_SIZE);
        int tilesHigh = (int) Math.ceil((double) height / TILE_SIZE);
        int totalTiles = tilesWide * tilesHigh;
        int tokenCost = (totalTiles * 170) + 85;

        // Create optimized bitmap
        Bitmap optimizedImage = resizeImage(originalImage, width, height);

        Log.d(TAG, String.format("Optimized image: %dx%d, Tiles: %d, Token cost: %d",
                width, height, totalTiles, tokenCost));

        return new ImageDetails(optimizedImage, tokenCost, "high");
    }

    private static Bitmap resizeImage(Bitmap original, int newWidth, int newHeight) {
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / original.getWidth();
        float scaleHeight = ((float) newHeight) / original.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(original, 0, 0,
                original.getWidth(), original.getHeight(), matrix, true);
    }

    public static int estimateCost(int width, int height, boolean useHighDetail) {
        if (!useHighDetail) {
            return 85;
        }

        // Scale to fit 2048x2048
        if (width > MAX_SIZE || height > MAX_SIZE) {
            float scale = Math.min((float) MAX_SIZE / width, (float) MAX_SIZE / height);
            width = Math.round(width * scale);
            height = Math.round(height * scale);
        }

        // Scale shortest side to 768px
        int shortestSide = Math.min(width, height);
        if (shortestSide != TARGET_SHORT_SIDE) {
            float scale = (float) TARGET_SHORT_SIDE / shortestSide;
            width = Math.round(width * scale);
            height = Math.round(height * scale);
        }

        int tilesWide = (int) Math.ceil((double) width / TILE_SIZE);
        int tilesHigh = (int) Math.ceil((double) height / TILE_SIZE);
        return (tilesWide * tilesHigh * 170) + 85;
    }
}