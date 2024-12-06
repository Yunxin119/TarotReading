package com.group5.tarotreading.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group5.tarotreading.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";
    private EditText currentPasswordET, newPasswordET;
    private Button changePassword, back;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // If no user is signed in, return to login
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        currentPasswordET = findViewById(R.id.current_password);
        newPasswordET = findViewById(R.id.new_password);
        changePassword = findViewById(R.id.change_password);
        back = findViewById(R.id.back);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = currentPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();

                if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    newPasswordET.setError("Password must be at least 6 characters");
                    return;
                }

                updatePassword(currentPassword, newPassword);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePasswordActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });
    }

    private void updatePassword(String currentPassword, String newPassword) {
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        // Prompt the user to re-provide their sign-in credentials
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User re-authenticated successfully, proceed with password update
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Password updated successfully");
                                                Toast.makeText(ChangePasswordActivity.this,
                                                        "Password updated successfully",
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Log.w(TAG, "Error updating password", task.getException());
                                                Toast.makeText(ChangePasswordActivity.this,
                                                        "Failed to update password: " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Re-authentication failed", task.getException());
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}