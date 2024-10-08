package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button todayfortune;
    Button askquestion;
    Button cameraButton;
    Button eregister,elogin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        todayfortune = findViewById(R.id.today);
        askquestion = findViewById(R.id.askquestion);
        cameraButton = findViewById(R.id.camera_button);

        // Login/Register Button
        eregister = findViewById(R.id.register);
        elogin= findViewById(R.id.login);


        todayfortune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cardnum = 1;
                String question = "Tell me today's fortune, give me suggestion";

                Intent intent = new Intent(MainActivity.this, CardPickingActivity.class);
                intent.putExtra("cardnum", cardnum);
                intent.putExtra("question", question);

                startActivity(intent);
            }
        });

        askquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Set the camera button click listener
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        // register button
        eregister.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this,RegisterActivity.class);
            MainActivity.this.startActivity(myIntent);

        });

        // login button
        elogin.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this,LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        });
    }
}