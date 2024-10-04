package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QuestionActivity extends AppCompatActivity {

    EditText question_content;
    Button back;
    Button ask;
    String prompt;
    FrameLayout buttonContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        question_content = findViewById(R.id.questioncontent);
        back = findViewById(R.id.back);
        ask = findViewById(R.id.ask);
        buttonContainer = findViewById(R.id.buttonContainer);


        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = question_content.getText().toString().trim();
                if (!question.isEmpty()) {
                    generateContent(question);  // 调用生成内容的方法
                } else {
                    Toast.makeText(QuestionActivity.this, "Please enter a question", Toast.LENGTH_SHORT).show();
                }

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
    }

    // 生成内容并处理 Gemini AI 的 API 请求
    void generateContent(String question) {
        prompt = "Question: " + question + "Spread Selection Guide:\n" +
                "Hexgram: Use this spread when the user seeks clarity on the development of a situation and the best course of action. Example Questions: \"How can I improve my relationship with my boyfriend?\", \"What can I do to strengthen my family ties?\", \"Should I continue in my current job?\"\n" +
                "TwoSelection: Use when the user faces a decision between two distinct options. Example Questions: \"Which person is better for me, A or B?\", \"Should I take this job or pursue a higher degree?\", \"Which of the two companies that contacted me should I choose?\"\n" +
                "Horseshoe: Use when the user wants to understand their current situation and find the best course of action. Example Questions: \"How will my relationship with my boyfriend develop?\", \"Will I be laid off?\", \"Will my upcoming trip go smoothly?\"\n" +
                "LoverCross: Prioritize this spread for romantic relationship inquiries, especially involving relationship development or breakups. Example Questions: \"How will my romantic relationship evolve?\", \"Should I end things with my partner?\"\n" +
                "LoverBack: This spread takes priority when the user asks specific questions about their ex, such as reconciliation or how their ex feels about them. Example Questions: \"Will I get back together with my ex?\", \"What does my ex think of me?\"\n" +
                "TimeFlow: Use this spread when the user wants to understand how a situation will progress over time. It’s best applied to scenarios that have already begun but are ongoing. Example Questions: \"How will my current job progress?\", \"I had a fight with my friend—how will things unfold?\""
                + "Please only give one word of one choice you think most suitable";

        // 创建 Gemini Model
        GenerativeModel gm = new GenerativeModel("gemini-1.5-pro-002", "API_KEY");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(prompt).build();

        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();

                        runOnUiThread(() -> createProceedButton(resultText));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        String errorMessage = t.getMessage();
                        runOnUiThread(() -> {
                            Toast.makeText(QuestionActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                },
                executor
        );
    }

    // 动态创建按钮，用户点击按钮后跳转到下一页面
    void createProceedButton(String spreadType) {
        Button nextButton = new Button(QuestionActivity.this);
        nextButton.setText(spreadType);

        FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLayoutParams.gravity = android.view.Gravity.CENTER;
        nextButton.setLayoutParams(buttonLayoutParams);


        nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(QuestionActivity.this, CardPickingActivity.class);
            intent.putExtra("spreadType", spreadType);
            int pickcard;
            Boolean cutcard = false;
            switch (spreadType.toLowerCase()) {
                case "lovercross":
                    pickcard = 5;
                    break;
                case "twoselection":
                    pickcard = 5;
                    break;
                case "hexgram":
                    pickcard = 6;
                    cutcard = true;
                    break;
                case "horseshoe":
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


