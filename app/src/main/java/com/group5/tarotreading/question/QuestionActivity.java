package com.group5.tarotreading.question;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        question_content = findViewById(R.id.questioncontent);
        back = findViewById(R.id.back);

        ask = findViewById(R.id.ask);
        buttonContainer = findViewById(R.id.buttonContainer);
        test = findViewById(R.id.test);


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


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        test.setOnClickListener(v -> {
//            String question = question_content.getText().toString();
//            if (!question.isEmpty()) {
//                Intent myIntent = new Intent(QuestionActivity.this, AIAnswerActivity.class);
//                myIntent.putExtra("question", question);
//                QuestionActivity.this.startActivity(myIntent);
//            } else {
//                Toast.makeText(QuestionActivity.this, "Please enter a question.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    void createProceedButton(String spreadType) {
        Button nextButton = new Button(QuestionActivity.this);
        String spreadTypeFormatted = spreadType.toLowerCase();

        String str = spreadTypeFormatted.substring(0, 1).toUpperCase() + spreadTypeFormatted.substring(1);

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

        nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(QuestionActivity.this, CardPickActivity.class);
            intent.putExtra("spreadType", spreadType);
            String question = question_content.getText().toString().trim();
            intent.putExtra("question", question);
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
        nextButton.bringToFront();
        buttonContainer.setClickable(true);
        buttonContainer.setFocusable(true);

    }
}

