package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestionActivity extends AppCompatActivity {

    ImageButton cards3;
    ImageButton cards4;
    EditText question_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        cards3 = findViewById(R.id.cards3);
        cards4 = findViewById(R.id.cards4);
        question_content = findViewById(R.id.questioncontent);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}