
package com.group5.tarotreading.result;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.group5.tarotreading.BuildConfig;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;
import com.group5.tarotreading.result.AnswerPageViewModel;
import com.group5.tarotreading.result.TarotPromptBuilder;
import com.group5.tarotreading.utils.OpenAIHelper;

import java.util.ArrayList;

public class AnswerPage extends AppCompatActivity {
    private ArrayList<String> cardPicked;
    private String spreadType;
    private String questionContent;
    private TextView answerText;
    private Button nextButton;
    private AnswerPageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_page);

        viewModel = new ViewModelProvider(this).get(AnswerPageViewModel.class);

        setupViews();
        getIntentData();
        setupObservers();
        generateTarotReading();
    }

    private void setupViews() {
        answerText = findViewById(R.id.answerText);
        answerText.setMovementMethod(new ScrollingMovementMethod());
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> handleNextButton());
    }

    private void getIntentData() {
        spreadType = getIntent().getStringExtra("spreadType");
        questionContent = getIntent().getStringExtra("question");
        cardPicked = getIntent().getStringArrayListExtra("cardPicked");
    }

    private void setupObservers() {
        viewModel.getCurrentInterpretation().observe(this, interpretation -> {
            answerText.setText(interpretation);
        });

        viewModel.isLastInterpretation().observe(this, isLast -> {
            if (isLast) {
                nextButton.setText("Home");
            }
        });
    }

    private void generateTarotReading() {
        TarotPromptBuilder promptBuilder = new TarotPromptBuilder(questionContent, spreadType, cardPicked);
        String prompt = promptBuilder.buildPrompt();

        OpenAIHelper openAIHelper = new OpenAIHelper(this, BuildConfig.OPENAI_API_KEY);
        openAIHelper.generateResponse(prompt, new OpenAIHelper.AIResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (!response.contains("##")) {
                    Toast.makeText(AnswerPage.this, "Response is not properly formatted.", Toast.LENGTH_LONG).show();
                    return;
                }
                viewModel.setTarotResponse(response);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AnswerPage.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleNextButton() {
        if (viewModel.isLastInterpretation().getValue()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            viewModel.showNextInterpretation();
        }
    }
}