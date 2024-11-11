package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class CardPickActivity extends AppCompatActivity implements CardPickView.OnCardSelectedListener{

    private String spreadType;
    private String question;
    private int pickCard;
    private boolean cutCard;
    private List<ImageView> cardSlots = new ArrayList<>();
    private int selectedSlotIndex = 0;
    private ImageView cutCardSlot;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeData();

        loadLayout("SIX_STAR");

        initializeCardSlots();

        CardPickView cardPickView = findViewById(R.id.cardPickView);

        cardPickView.setOnCardSelectedListener(this);

        cardPickView.initialize(pickCard, cutCard);

    }

    private void initializeData() {
        Intent intent = getIntent();
        spreadType = intent.getStringExtra("spreadType");
        if (spreadType == null)
            spreadType = "SIX_STAR";
        question = intent.getStringExtra("question");
        pickCard = intent.getIntExtra("pickCard", 7);
        cutCard = intent.getBooleanExtra("cutCard", true);
    }

    public void loadLayout(String layoutType) {
        int layoutRes;

        // Select the layout resource based on the layout type
        switch (layoutType) {
            case "SIX_STAR":
                layoutRes = R.layout.six_star_layout;
                break;
            case "CHOICE":
                layoutRes = R.layout.choice_layout;
                break;
            case "MARLBORO":
                layoutRes = R.layout.marlboro_layout;
                break;
            case "LOVE_CROSS":
                layoutRes = R.layout.love_cross_layout;
                break;
            case "EX_RETURN":
                layoutRes = R.layout.ex_return_layout;
                break;
            case "TIMELINE_FLOW":
                layoutRes = R.layout.timeline_flow_layout;
                break;
            default:
                throw new IllegalArgumentException("Unknown layout type: " + layoutType);
        }

        setContentView(layoutRes);
    }

    private void initializeCardSlots() {
        int i = 1;
        while (true) {
            String slotId = "card" + i;
            int resId = getResources().getIdentifier(slotId, "id", getPackageName());

            if (resId == 0) {
                break;
            }

            ImageView slot = findViewById(resId);
            if (slot != null) {
                cardSlots.add(slot);
            }

            i++;
        }

        if (cutCard) {
            String cutSlotId = "cutCard";
            int cutResId = getResources().getIdentifier(cutSlotId, "id", getPackageName());

            if (cutResId != 0) {
                ImageView cutSlot = findViewById(cutResId);
                if (cutSlot != null) {
                    cutCardSlot = cutSlot;
                }
            }
        }


    }

    @Override
    public void onCardSelected(Card selectedCard, boolean isCutCard) {
        if (isCutCard && cutCardSlot != null){
            cutCardSlot.setImageBitmap(selectedCard.getImage());
        }
        else if (selectedSlotIndex < cardSlots.size()) {
            selectedCard.flip();
            cardSlots.get(selectedSlotIndex).setImageBitmap(selectedCard.getImage());
            selectedSlotIndex++;
        }
    }


}