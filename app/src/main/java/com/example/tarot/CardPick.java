package com.example.tarot;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CardPick {
    private List<Card> cardList = new ArrayList<>();
    private Context context;

    public CardPick(Context context) {
        this.context = context;
        initCards();
    }

    private void initCards() {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream backInputStream = assetManager.open("back/Back.jpg");
            Bitmap backImage = BitmapFactory.decodeStream(backInputStream);
            String[] cardFiles = assetManager.list("cards");

            if (cardFiles != null) {
                for (String fileName : cardFiles) {
                    String filePath = "cards/" + fileName;

                    // Load Image
                    InputStream inputStream = assetManager.open(filePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    String cardName = fileName.substring(0, fileName.lastIndexOf('.'));

                    // Create card object
                    cardList.add(new Card(bitmap, backImage, cardName, "Description for " + cardName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get all cards
    public List<Card> getCardList() {
        return cardList;
    }
}

