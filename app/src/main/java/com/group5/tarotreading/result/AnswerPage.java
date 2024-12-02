
package com.group5.tarotreading.result;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
    private TextView pleaseWaitText;
    private String spreadType;
    private String questionContent;
    private TextView answerText;
    private Button nextButton;
    private Button previousButton;
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
        pleaseWaitText = findViewById(R.id.pleaseWaitText);
        answerText = findViewById(R.id.answerText);
        answerText.setMovementMethod(new ScrollingMovementMethod());
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> handleNextButton());

        previousButton = findViewById(R.id.previousButton);
        previousButton.setOnClickListener(v -> handlePreviousButton());

        // Initially disable previous button


    }

    private void getIntentData() {
        spreadType = getIntent().getStringExtra("spreadType");
        questionContent = getIntent().getStringExtra("question");
        cardPicked = getIntent().getStringArrayListExtra("cardPicked");
    }

    private void setupObservers() {
        // Observe loading state
        viewModel.isLoading().observe(this, isLoading -> {
            pleaseWaitText.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Optionally add animation
            if (isLoading) {
                pleaseWaitText.setAlpha(1f);
                addLoadingAnimation();
            } else {
                pleaseWaitText.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                pleaseWaitText.setVisibility(View.GONE);
                            }
                        });
            }
        });

        viewModel.getCurrentInterpretation().observe(this, interpretation -> {
            answerText.setText(interpretation);
        });

        viewModel.isLastInterpretation().observe(this, isLast -> {
            int drawable = isLast ? R.drawable.ic_home_foreground : R.drawable.ic_right_foreground;
            nextButton.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        });

        viewModel.isFirstInterpretation().observe(this, isFirst -> {
            Log.d("AnswerPage", "isFirst: " + isFirst);
            previousButton.setVisibility(isFirst ? View.GONE : View.VISIBLE);
            previousButton.setEnabled(!isFirst);
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
    private void handlePreviousButton() {
        viewModel.showPreviousInterpretation();
    }
    private void addLoadingAnimation() {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(pleaseWaitText, "alpha", 0f, 1f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(pleaseWaitText, "alpha", 1f, 0f);
        fadeIn.setDuration(1000);
        fadeOut.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.play(fadeIn).before(fadeOut);
        set.setStartDelay(500);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                set.start(); // Restart animation
            }
        });
        set.start();
    }
}