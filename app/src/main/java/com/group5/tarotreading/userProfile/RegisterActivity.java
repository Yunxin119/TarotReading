package com.group5.tarotreading.userProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText eusername, eemail, epassword;
    Button register, login, home;
    boolean isAllFields = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDocRef = db.collection("Tarot-Reading")
            .document("Users");

    private CollectionReference collectionReference = db.collection("Tarot-Reading");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        eusername = findViewById(R.id.username);
        eemail = findViewById(R.id.email);
        epassword = findViewById(R.id.password);
        register = findViewById(R.id.regibutton);
        login = findViewById(R.id.loginbutton);
        home = findViewById(R.id.home);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                isAllFields= register();
                if(isAllFields)
                {
                    SaveDataToNewDocument();

                    String a= eusername.getText().toString();
                    String b = epassword.getText().toString();
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    i.putExtra("number1",a);
                    i.putExtra("number2",b);
                    startActivity(i);
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

        // home button
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean register() {
        String username = eusername.getText().toString().trim();
        String email = eemail.getText().toString().trim();
        String password = epassword.getText().toString().trim();
        if (CheakAllField(username, email, password)) {

            Toast.makeText(this, "you have succesfully register", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean CheakAllField (String username, String email, String password)
    {
        if (TextUtils.isEmpty(username)) {
            eusername.setError("Please enter name");
            return false;

        }


        if (TextUtils.isEmpty(email)) {
            eemail.setError("please enter proper email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            epassword.setError("please enter proper password");
            return false;
        }
        return true;
    }

    private void SaveDataToNewDocument(){
        String username = eusername.getText().toString();
        String email = eemail.getText().toString();
        String password = epassword.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);

        collectionReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String docId = documentReference.getId();
            }
        });
    }



}

