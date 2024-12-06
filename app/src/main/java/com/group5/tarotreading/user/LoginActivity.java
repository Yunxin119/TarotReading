package com.group5.tarotreading.user;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button eregister, elogin, home;
    private TextView textView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        eregister = findViewById(R.id.register);
        elogin = findViewById(R.id.login);
        home = findViewById(R.id.home);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // Handle incoming intent data
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
        eregister.setOnClickListener(view -> {
            Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(in);
        });

        // Login button click listener
        elogin.setOnClickListener(view -> {
            String email = username.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            validateLogin(email, userPassword);
        });

        // Home button click listener
        home.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void validateLogin(String email, String password) {
        if (email.isEmpty()) {
            username.setError("Email cannot be empty");
            return;
        }
        if (password.isEmpty()) {
            this.password.setError("Password cannot be empty");
            return;
        }

        // Sign in with Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Get additional user data from Firestore
                            db.collection("Tarot-Reading")
                                    .document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(document -> {
                                        if (document.exists()) {
                                            String username = document.getString("username");
                                            String userEmail = document.getString("email");
                                            String userId = user.getUid();

                                            // Save user data to SharedPreferences
                                            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.putString("username", username);
                                            editor.putString("email", userEmail);
                                            editor.putString("userId", userId);
                                            editor.apply();

                                            // Show success message
                                            Toast.makeText(LoginActivity.this, "Login Successful!",
                                                    Toast.LENGTH_SHORT).show();

                                            // Navigate to UserProfile
                                            Intent intent = new Intent(LoginActivity.this, UserProfile.class);
                                            intent.putExtra("username", username);
                                            intent.putExtra("email", userEmail);
                                            intent.putExtra("userId", userId);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error getting user data", e);
                                        Toast.makeText(LoginActivity.this,
                                                "Error retrieving user data", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this,
                                "Authentication failed: Invalid email or password",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}