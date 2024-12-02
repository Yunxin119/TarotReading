package com.group5.tarotreading.result;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.group5.tarotreading.BuildConfig;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;
import com.group5.tarotreading.utils.OpenAIHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class AnswerPage extends AppCompatActivity {
    private ArrayList<String> cardPicked; //pass from card picking
    private String spreadType; //pass from card picking
    private String questionContent; //pass from card picking
    private TextView answerText;
    private String[] responseArray;
    private Button nextButton;
    private Button home;
    private int currentIndex = 0;
    private ProgressBar loadingSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_answer_page);

        answerText = findViewById(R.id.answerText);
        answerText.setMovementMethod(new ScrollingMovementMethod());
        nextButton = findViewById(R.id.nextButton);

        // Add a loading spinner when user waiting for ai answer
        loadingSpinner = findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);


        spreadType = getIntent().getStringExtra("spreadType");
        questionContent = getIntent().getStringExtra("question");
        cardPicked = getIntent().getStringArrayListExtra("cardPicked");

        String prompt = constructPrompt();

        OpenAIHelper openAIHelper = new OpenAIHelper(this, BuildConfig.OPENAI_API_KEY);

        openAIHelper.generateResponse(prompt, new OpenAIHelper.AIResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (!response.contains("##")) {
                    Log.e("SplitError", "Response does not contain '##'. Check the prompt or OpenAI response.");
                    Toast.makeText(AnswerPage.this, "Response is not properly formatted.", Toast.LENGTH_LONG).show();
                    return;
                }
                loadingSpinner.setVisibility(View.GONE);

                response = response.trim().replaceAll("\\s+", " ");
                Log.d("CleanedResponse", response);

                responseArray = response.split("\\#\\#");

                if (responseArray.length > 0) {
                    currentIndex = 0;
                    answerText.setText(responseArray[currentIndex]);
                } else {
                    Toast.makeText(AnswerPage.this, "Response is empty after split.", Toast.LENGTH_LONG).show();
                    answerText.setText("Error: Response is empty.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                loadingSpinner.setVisibility(View.GONE);
                Toast.makeText(AnswerPage.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextResponse();
            }
        });

    }

    private void showNextResponse() {
        if (responseArray != null && currentIndex < responseArray.length - 1) {
            currentIndex++; // Move to the next part
            answerText.setText(responseArray[currentIndex]);
        } else {
            nextButton.setText("Home");
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AnswerPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private String constructPrompt() {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("You are a wise and mystical tarot master. Based on the following information, provide an insightful reading and interpretation.\n\n");

        promptBuilder.append("Question: ").append(questionContent).append("\n");
        String spreadTypeMeaning;
        switch (spreadType) {
            case "TimeFlow":
                spreadTypeMeaning = "when read this spread, first look at how the cut card can influence the whole spread, and then read by past, present and future's order. 1st card means past situation, 2nd card means current situation, 3rd card means future situation.";
                break;
            case "LoverBack":
                spreadTypeMeaning = "This spread is to analyze the ex's thoughts, predict the possibility and development of getting back together. 1st card, ex's thought about me, 2nd card means ex's opinion about getting back together, 3rd card means the obstacle of getting back together, and 4th card means the future of the relationship.";
                break;
            case "LoverCross":
                spreadTypeMeaning = "This spread is suitable for most of the romantic relationship situations. 1st card means your attitude and opinion about the relationship, 2nd card means the other person's attitude, 3rd card means current situation or dynamic of the relationship, 4th card means the future development, indicates potential future outcomes or progress. 5th card means the outcome, suggest the overall result of the situation.";
                break;
            case "Marlboro":
                spreadTypeMeaning = "This spread is to understand one's current situation and determing the best course of action. When reading it, first look at how the cut card can influence the whole spread, and then read by order of the card. 1st card means current situation, 2nd card means the predictable situation, aspects or events that are foreseeable and within control, 3rd card means the unpredictable situation, represent elements or influences that are uncertain or beyond control, 4th card means what will happen based on the current dynamics, 5th card means outcome, indicates the overall result of the situation and way to solve it.";
                break;
            case "TwoSelection":
                spreadTypeMeaning = "This spread can be used for situations involving two distinct choices. 1st card: your current state, represents your overall situation or mindset regarding the decision. 2nd card: State of Choice A: Explores the circumstances and details if you choose option A. 3rd card: State of Choice B: Explores the circumstances and details if you choose option B. 4th card: Impact of Choice A: Reveals the outcomes or effects of selecting option A. 5th card: Impact of Choice B: Reveals the outcomes or effects of selecting option B.";
                break;
            case "Hexgram":
                spreadTypeMeaning = "This spread helps clarify the progression of the situation and provides actionable insights for resolution.It consists of six cards: Past, reflecting past events or experiences; Present, representing the current situation; Future, indicating potential developments; Cause, revealing the root reason behind the issue; Obstacle, highlighting challenges that may arise; and Advice, offering guidance or strategies for resolution. To interpret, observe the overall impact of the cutcard to other cards and read them in order: Past, Present, Future, Cause, Obstacle, and Advice.";
                break;
            default:
                spreadTypeMeaning = "";
                break;

        }
        promptBuilder.append("Spread Type: ").append(spreadType).append("\n");
        promptBuilder.append("Reading Instructions: ").append(spreadTypeMeaning).append("\n");

        promptBuilder.append("Cards Picked:\n");
        for (String card : cardPicked) {
            promptBuilder.append(card).append("\n");
        }

        promptBuilder.append("\nPlease give me an answer for my question, and based on the spread type and selected cards. " +
                "Provide a comprehensive reading that incorporates the meanings of all the cards together." +
                "Now, using the energy of the cards, reveal a profound and mystical interpretation. Try your best to give a clear answer. " +
                "When interpreting each card, separate the explanation with '##'." +
                "example of split: For card 1, it indicates ... ## For card 2, it indicates ... \n" +
                "Speak as a tarot master would, guiding the seeker through the wisdom of the cards and showing them the path ahead."
                );

        return promptBuilder.toString();
    }
}
