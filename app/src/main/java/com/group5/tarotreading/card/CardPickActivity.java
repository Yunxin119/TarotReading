package com.group5.tarotreading.card;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.group5.tarotreading.result.AnswerPage;
import com.group5.tarotreading.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class CardPickActivity extends AppCompatActivity implements CardPickView.OnCardSelectedListener {

    private String spreadType;
    private String question;
    private int pickCard;
    private boolean cutCard;
    private List<ImageView> cardSlots = new ArrayList<>();
    private int selectedSlotIndex = 0;
    private ImageView cutCardSlot;
    private Button myButton;
    private List<String> selectedCards = new ArrayList<String>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeData();
        loadLayout(spreadType);
        initializeCardSlots();

        CardPickView cardPickView = findViewById(R.id.cardPickView);
        cardPickView.setOnCardSelectedListener(this);

        // Initialize but do not show any selected cards until user interaction
        cardPickView.initialize(pickCard, cutCard);


        myButton = findViewById(R.id.myButton);
        myButton.setVisibility(View.GONE);
        // Proceed to next view
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardPickActivity.this, AnswerPage.class);
                intent.putExtra("spreadType", spreadType);
                intent.putExtra("question", question);
                ArrayList<Integer> cardSlotIds = new ArrayList<>();
                intent.putStringArrayListExtra("cardPicked", (ArrayList<String>) selectedCards);
                startActivity(intent);
            }
        });
    }

    private void initializeData() {
        Intent intent = getIntent();
        spreadType = intent.getStringExtra("spreadType");
        if (spreadType == null)
            spreadType = "TimeFlow";
        question = intent.getStringExtra("question");
        pickCard = intent.getIntExtra("pickcard", 3);
        cutCard = intent.getBooleanExtra("cutcard", true);
    }

    public void loadLayout(String layoutType) {
        int layoutRes;

        switch (layoutType) {
            case "OneCard":
                layoutRes = R.layout.one_card_layout;
                break;
            case "SixStars":
                layoutRes = R.layout.six_star_layout;
                break;
            case "TwoSelection":
                layoutRes = R.layout.choice_layout;
                break;
            case "Marlboro":
                layoutRes = R.layout.marlboro_layout;
                break;
            case "LoverCross":
                layoutRes = R.layout.love_cross_layout;
                break;
            case "LoverBack":
                layoutRes = R.layout.ex_return_layout;
                break;
            case "TimeFlow":
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

        // If cutCard is enabled, initialize the cutCard slot
        if (cutCard) {
            String cutSlotId = "cutCard";
            int cutResId = getResources().getIdentifier(cutSlotId, "id", getPackageName());

            if (cutResId != 0) {
                cutCardSlot = findViewById(cutResId);
                if (cutCardSlot != null && !cardSlots.isEmpty()) {
                    // Display the bottom card in the cutCardSlot
                    CardPickView cardPickView = findViewById(R.id.cardPickView);
                    cardPickView.setCutCardSlot(cutCardSlot);
                }
            }
        }
    }

    @Override
    public void onCardSelected(Card selectedCard, boolean isCutCard) {
        if (isCutCard && cutCardSlot != null) {
            cutCardSlot.setImageBitmap(selectedCard.getImage());
            String cardInfo;
            String orientation = selectedCard.getIsReversed() ? "-reversed" : "";
            cardInfo = "cut card:" + selectedCard.getName() + orientation;
            selectedCards.add(cardInfo);
        } else if (selectedSlotIndex < cardSlots.size()) {

            selectedCard.flip();
            cardSlots.get(selectedSlotIndex).setImageBitmap(selectedCard.getImage());


            String cardInfo;
            String slotKey = String.valueOf(selectedSlotIndex+1);
            String orientation = selectedCard.getIsReversed() ? "-reversed" : "";
            cardInfo = slotKey + ": " + selectedCard.getName()+orientation;
            selectedCards.add(cardInfo);

            selectedSlotIndex++;
        }

        // Show button layout when the cardSlot is full
        if (selectedSlotIndex == cardSlots.size()) {
            myButton.setVisibility(View.VISIBLE);
        }
    }
}
