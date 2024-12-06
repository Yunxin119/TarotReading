package com.group5.tarotreading.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText eusername, eemail, epassword;
    Button register, login, home;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "RegisterActivity";
    private EmailVerificationHandler verificationHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Email Verification Handler
        verificationHandler = new EmailVerificationHandler();
        verificationHandler.setVerificationListener(new EmailVerificationHandler.EmailVerificationListener() {
            @Override
            public void onVerificationEmailSent() {
                Toast.makeText(RegisterActivity.this,
                        "Verification email sent. Please check your inbox.",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationSuccess() {
                Toast.makeText(RegisterActivity.this,
                        "Email verified successfully!",
                        Toast.LENGTH_SHORT).show();
                navigateToMainActivity(eemail.getText().toString());
            }

            @Override
            public void onVerificationFailed(Exception e) {
                Toast.makeText(RegisterActivity.this,
                        "Verification failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize views
        eusername = findViewById(R.id.username);
        eemail = findViewById(R.id.email);
        epassword = findViewById(R.id.password);
        register = findViewById(R.id.regibutton);
        login = findViewById(R.id.loginbutton);
        home = findViewById(R.id.home);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = eusername.getText().toString().trim();
                String email = eemail.getText().toString().trim();
                String password = epassword.getText().toString().trim();

                if (checkAllFields(username, email, password)) {
                    registerUser(username, email, password);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            verificationHandler.checkEmailVerification();
        }
    }

    private boolean checkAllFields(String username, String email, String password) {
        if (TextUtils.isEmpty(username)) {
            eusername.setError("Please enter name");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            eemail.setError("Please enter proper email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            epassword.setError("Please enter proper password");
            return false;
        }

        if (password.length() < 6) {
            epassword.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }


    private void registerUser(final String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Send verification email first
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                // Save user data to Firestore
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("username", username);
                                                userData.put("email", email);
                                                userData.put("emailVerified", false);

                                                db.collection("Tarot-Reading")
                                                        .document(user.getUid())
                                                        .set(userData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(RegisterActivity.this,
                                                                    "Registration successful! Please check your email for verification.",
                                                                    Toast.LENGTH_LONG).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.w(TAG, "Error adding user data", e);
                                                            Toast.makeText(RegisterActivity.this,
                                                                    "Error saving user data",
                                                                    Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                Log.e(TAG, "Failed to send verification email", emailTask.getException());
                                                Toast.makeText(RegisterActivity.this,
                                                        "Failed to send verification email: " + emailTask.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void navigateToMainActivity(String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("Tarot-Reading")
                    .document(user.getUid())
                    .update("emailVerified", true)
                    .addOnSuccessListener(aVoid -> {
                        // Sign out the user
                        mAuth.signOut();

                        // Show success message
                        Toast.makeText(RegisterActivity.this,
                                "Email verified! Please sign in.",
                                Toast.LENGTH_LONG).show();

                        // Navigate to LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error updating email verification status", e);
                    });
        }
    }

}