package com.group5.tarotreading;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button eregister, elogin;
    private TextView textView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tarot-Reading");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eregister = findViewById(R.id.register);
        elogin = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

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

                // Call the modified validateLogin method
                validateLogin(enteredUsername, enteredPassword);
            }
        });

    }

    // Method to validate login credentials
    private void validateLogin(final String username, final String password) {
        if (username.isEmpty()) {
            this.username.setError("Username cannot be empty");
            return;
        }
        if (password.isEmpty()) {
            this.password.setError("Password cannot be empty");
            return;
        }

        collectionReference.whereEqualTo("email", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                // login successfully
                                // should return here
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                Intent in = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(in);
                                finish();
                            }
                            // login failed, unable to find matched record
                            Toast.makeText(LoginActivity.this, "Can't find the email or password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

