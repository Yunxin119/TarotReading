package com.group5.tarotreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText currentPasswordET, newPasswordET;
    private Button changePassword, back;
    private String userId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordET = findViewById(R.id.current_password);
        newPasswordET = findViewById(R.id.new_password);
        changePassword = findViewById(R.id.change_password);
        back = findViewById(R.id.back);

        userId = getIntent().getStringExtra("userId");

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = currentPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();

                if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCurrentPassword(currentPassword, newPassword);
                }
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

    private void verifyCurrentPassword(String currentPassword, String newPassword) {
        DocumentReference userDocRef = db.collection("Tarot-Reading").document(userId);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String storedPassword = documentSnapshot.getString("password");
                    if (storedPassword != null && storedPassword.equals(currentPassword)) {
                        updatePassword(newPassword);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangePasswordActivity.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String newPassword) {
        DocumentReference userDocRef = db.collection("Tarot-Reading").document(userId);
        userDocRef.update("password", newPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePasswordActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
