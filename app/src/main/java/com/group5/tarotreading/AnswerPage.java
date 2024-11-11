package com.group5.tarotreading;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class AnswerPage extends AppCompatActivity {
    private HashMap<Integer, String> cardPicked; //pass from card picking
    private String spreadType; //pass from card picking
    private String questionContent; //pass from card picking
    private TextView answerText;
    private Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_answer_page);

        home = findViewById(R.id.home);
        answerText = findViewById(R.id.answerText);
        answerText.setMovementMethod(new ScrollingMovementMethod());

//        cardPicked = (HashMap<String, String>) getIntent().getSerializableExtra("cardPicked");
        spreadType = getIntent().getStringExtra("spreadType");
        questionContent = getIntent().getStringExtra("question");

        // test with real data
        cardPicked = new HashMap<>();
        cardPicked.put(1, "The Magician");
        cardPicked.put(2, "The High Priestess");
        cardPicked.put(3, "The Star");


        String prompt = constructPrompt();

        OpenAIHelper openAIHelper = new OpenAIHelper(this, BuildConfig.OPENAI_API_KEY);

        openAIHelper.generateResponse(prompt, new OpenAIHelper.AIResponseListener() {
            @Override
            public void onSuccess(String response) {
                answerText.setText(response);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AnswerPage.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnswerPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String constructPrompt() {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("You are a wise and mystical tarot master. Based on the following information, provide an insightful reading and interpretation.\n\n");

        promptBuilder.append("Question: ").append(questionContent).append("\n");
        promptBuilder.append("Spread Type: ").append(spreadType).append("\n");
        promptBuilder.append("Cards Picked:\n");
        for (Integer card : cardPicked.keySet()) {
            promptBuilder.append(card).append(": ").append(cardPicked.get(card)).append("\n");
        }

        promptBuilder.append("\nPlease give me an answer for my question, and based on the spread type and selected cards. " +
                "Provide a comprehensive reading that incorporates the meanings of all the cards together." +
                "Now, using the energy of the cards, reveal a profound and mystical interpretation. " +
                "Speak as a tarot master would, guiding the seeker through the wisdom of the cards and showing them the path ahead." +
                "No more than 300 words\"");

        return promptBuilder.toString();
    }
}
