package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestionActivity extends AppCompatActivity {

    ImageButton cards3;
    ImageButton cards4;
    EditText question_content;
    Button back;
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        cards3 = findViewById(R.id.cards3);
        cards4 = findViewById(R.id.cards4);
        question_content = findViewById(R.id.questioncontent);
        back = findViewById(R.id.back);
        test = findViewById(R.id.test);

        cards3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cardnum = 3;
                String question = "Question";
                Intent intent = new Intent(QuestionActivity.this, CardPickingActivity.class);
                intent.putExtra("question",question);
                intent.putExtra("cardnum", cardnum);
                startActivity(intent);
            }
        });

        cards4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cardnum = 4;
                String question = "Question";
                Intent intent = new Intent(QuestionActivity.this, CardPickingActivity.class);
                intent.putExtra("question",question);
                intent.putExtra("cardnum", cardnum);
                startActivity(intent);
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

        test.setOnClickListener(v -> {
            String question = question_content.getText().toString();
            if (!question.isEmpty()) {
                Intent myIntent = new Intent(QuestionActivity.this, AIAnswerActivity.class);
                myIntent.putExtra("question", question);
                QuestionActivity.this.startActivity(myIntent);
            } else {
                Toast.makeText(QuestionActivity.this, "Please enter a question.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}