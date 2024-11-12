package com.group5.tarotreading.cardActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.group5.tarotreading.AnswerPage;
import com.group5.tarotreading.CameraActivity;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;
import com.group5.tarotreading.ResultActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardPickActivity extends AppCompatActivity implements CardPickView.OnCardSelectedListener{

    private String spreadType;
    private String question;
    private int pickCard;
    private boolean cutCard;
    private List<ImageView> cardSlots = new ArrayList<>();
    private int selectedSlotIndex = 0;
    private ImageView cutCardSlot;
    private Button backButton;
    private Button cameraButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeData();

        loadLayout("ONECARD");

        initializeCardSlots();

        CardPickView cardPickView = findViewById(R.id.cardPickView);
        backButton = findViewById(R.id.back_button);
        cameraButton = findViewById(R.id.camera_button);

        cardPickView.setOnCardSelectedListener(this);

        cardPickView.initialize(pickCard, cutCard);

        Button myButton = findViewById(R.id.myButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CardPickActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(CardPickActivity.this, CameraActivity.class);
            startActivity(intent);
        });
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardPickActivity.this, AnswerPage.class);
                intent.putExtra("spreadType", spreadType);
                intent.putExtra("question", question);

                // Create HashMap to match AnswerPage's expected format
                HashMap<Integer, String> selectedCards = new HashMap<>();

                for (int i = 0; i < cardSlots.size(); i++) {
                    ImageView slot = cardSlots.get(i);
                    if (slot.getTag() != null) {
                        Card card = (Card) slot.getTag();
                        selectedCards.put(i + 1, card.getName());  // Using 1-based index as in AnswerPage
                    }
                }

                // Pass the HashMap as Serializable
                intent.putExtra("cardPicked", selectedCards);

                startActivity(intent);
            }
        });
    }

    private void initializeData() {
        Intent intent = getIntent();
        spreadType = intent.getStringExtra("spreadType");
        if (spreadType == null)
            spreadType = "ONECARD";
        question = intent.getStringExtra("question");
        pickCard = intent.getIntExtra("pickCard", 7);
        cutCard = intent.getBooleanExtra("cutCard", true);
    }

    public void loadLayout(String layoutType) {
        int layoutRes;

        // Select the layout resource based on the layout type
        switch (layoutType) {
            case "ONECARD":
                layoutRes = R.layout.one_card;
                break;
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