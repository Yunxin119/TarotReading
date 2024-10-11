package com.group5.tarotreading;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button eregister, elogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Correctly reference activity_login.xml

        eregister = findViewById(R.id.register);
        elogin = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // Receiving any intent extras if passed
        Intent i = getIntent();
        String a = i.getStringExtra("number1");
        String b = i.getStringExtra("number2");

        if (a != null) {
            username.setText(a);
        }
        if (b != null) {
            password.setText(b);
        }

        // Register button click listener
        eregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(in);
            }
        });

        // Login button click listener
        elogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                if (validateLogin(enteredUsername, enteredPassword)) {
                    // Successful login, proceed to MainActivity or Dashboard
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Show error message
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to validate login credentials (placeholder for now)
    private boolean validateLogin(String username, String password) {
        if (username.isEmpty()) {
            this.username.setError("Username cannot be empty");
            return false;
        }
        if (password.isEmpty()) {
            this.password.setError("Password cannot be empty");
            return false;
        }
        // Placeholder logic: login succeeds if username is "admin" and password is "1234"
        return username.equals("admin") && password.equals("1234");
    }
}
