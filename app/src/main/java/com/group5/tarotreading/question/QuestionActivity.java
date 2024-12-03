package com.group5.tarotreading.question;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group5.tarotreading.BuildConfig;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;
import com.group5.tarotreading.card.CardPickActivity;
import com.group5.tarotreading.utils.OpenAIHelper;

public class QuestionActivity extends AppCompatActivity {

    EditText question_content;
    Button back;

    Button ask;
    String prompt;
    FrameLayout buttonContainer;
    Button popoutInstruction;
    FrameLayout questionInstruction;
    ImageView closeIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        question_content = findViewById(R.id.questioncontent);
        back = findViewById(R.id.back);
        ask = findViewById(R.id.ask);

        // Elements for pop out instruction: initially set to invisible
        popoutInstruction = findViewById(R.id.popoutInstruction);
        questionInstruction = findViewById(R.id.questionInstruction);
        questionInstruction.setVisibility(View.GONE);
        closeIcon = findViewById(R.id.closeIcon);

        // Button container for popping out button feature, initially set to invisible
        buttonContainer = findViewById(R.id.buttonContainer);
        buttonContainer.setVisibility(View.GONE);

        // Call OpenAI helper
        OpenAIHelper openAIHelper = new OpenAIHelper(this, BuildConfig.OPENAI_API_KEY);

        ask.setOnClickListener(view -> {
            String question = question_content.getText().toString().trim();
            if (!question.isEmpty()) {
                prompt = "Question: " + question + "Spread Selection Guide:\n" +
                        "SixStars: Use this spread when the user seeks clarity on the development of a situation and the best course of action. Example Questions: \"How can I improve my relationship with my boyfriend?\", \"What can I do to strengthen my family ties?\", \"Should I continue in my current job?\"\n" +
                        "TwoSelection: Use when the user faces a decision between two distinct options. Example Questions: \"Which person is better for me, A or B?\", \"Should I take this job or pursue a higher degree?\", \"Which of the two companies that contacted me should I choose?\"\n" +
                        "Marlboro: Use when the user wants to understand their current situation and find the best course of action. Example Questions: \"How will my relationship with my boyfriend develop?\", \"Will I be laid off?\", \"Will my upcoming trip go smoothly?\"\n" +
                        "LoverCross: Prioritize this spread for romantic relationship inquiries, especially involving relationship development or breakups. Example Questions: \"How will my romantic relationship evolve?\", \"Should I end things with my partner?\"\n" +
                        "LoverBack: This spread takes priority when the user asks specific questions about their ex, such as reconciliation or how their ex feels about them. Example Questions: \"Will I get back together with my ex?\", \"What does my ex think of me?\"\n" +
                        "TimeFlow: Use this spread when the user wants to understand how a situation will progress over time. It’s best applied to scenarios that have already begun but are ongoing. Example Questions: \"How will my current job progress?\", \"I had a fight with my friend—how will things unfold?\""
                        + "Please ONLY give ONE WORD of one choice you think most suitable AND NOTHING ELSE and the world should be capitalized, for example: Timeflow";

                openAIHelper.generateResponse(prompt, new OpenAIHelper.AIResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> createProceedButton(response));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(QuestionActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            } else {
                Toast.makeText(QuestionActivity.this, "Please enter a question", Toast.LENGTH_SHORT).show();
            }
        });

        // back to home screen
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Click on the pop out instruction hint will trigger the questionInstruction to become visible
        popoutInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionInstruction.setVisibility(View.VISIBLE);
                // Give the question Instruction a scale up animation
                // Set initial scale to 0 (invisible)
                questionInstruction.setScaleX(0f);
                questionInstruction.setScaleY(0f);
                // Smooth scale-up animation
                questionInstruction.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .start();
            }
        });

        // set the pop out instruction back to invisible
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionInstruction.setVisibility(view.GONE);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    // dynamically create a proceed button to pass different spreadtype value to next screen
    void createProceedButton(String spreadType) {
        Button nextButton = new Button(QuestionActivity.this);
        String spreadTypeFormatted = spreadType.toLowerCase();

        String str = spreadTypeFormatted.substring(0, 1).toUpperCase() + spreadTypeFormatted.substring(1);

        buttonContainer.setVisibility(View.VISIBLE);

        // Set next button attributes
        nextButton.setAllCaps(false);
        nextButton.setText(str);
        nextButton.setBackgroundResource(R.drawable.popoutbtn);
        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.satisfyregular);
        nextButton.setTypeface(customTypeface);
        nextButton.setTextSize(30);
        nextButton.setTextColor(getResources().getColor(R.color.white));
        FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLayoutParams.gravity = Gravity.CENTER;
        nextButton.setLayoutParams(buttonLayoutParams);

        // Onclick logic for the nextButton, it take the response from the AI and set the text as the response
        nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(QuestionActivity.this, CardPickActivity.class);
            // Put extra info to next intent
            intent.putExtra("spreadType", spreadType);
            String question = question_content.getText().toString().trim();
            intent.putExtra("question", question);

            // According to different spread types, pass different pick card num and cut card value to next screen
            int pickcard;
            Boolean cutcard = false;

            switch (spreadType.toLowerCase()) {
                case "lovercross":
                    pickcard = 5;
                    break;
                case "twoselection":
                    pickcard = 5;
                    break;
                case "sixstars":
                    pickcard = 6;
                    cutcard = true;
                    break;
                case "marlboro":
                    pickcard = 5;
                    cutcard = true;
                    break;
                case "loverback":
                    pickcard = 4;
                    break;
                case "timeflow":
                    pickcard = 3;
                    cutcard = true;
                    break;
                default:
                    pickcard = 3;
                    break;
            }
            intent.putExtra("pickcard", pickcard);
            intent.putExtra("cutcard", cutcard);
            startActivity(intent);
        });

        buttonContainer.addView(nextButton);

        // Give the popout button a scale up animation
        // Set initial scale to 0 (invisible)
        nextButton.setScaleX(0f);
        nextButton.setScaleY(0f);
        // Smooth scale-up animation
        nextButton.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();

        nextButton.bringToFront();
        buttonContainer.setClickable(true);
        buttonContainer.setFocusable(true);
    }
}

