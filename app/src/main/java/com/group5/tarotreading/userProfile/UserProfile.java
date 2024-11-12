package com.group5.tarotreading.userProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.Manifest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group5.tarotreading.R;

public class UserProfile extends AppCompatActivity {
    private SharedPreferences preferences;
    private String userId;
    private ImageView profileImage;
    private TextView userName, userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Adjust system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        Button changePasswordButton = findViewById(R.id.change_password_button);
        Button tarotHistoryButton = findViewById(R.id.tarot_history_button);
        Button logoutButton = findViewById(R.id.logout_button);

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        userId = intent.getStringExtra("userId");

        // Set user info
        userName.setText(name);
        userEmail.setText(email);

        // Handle change password action
        changePasswordButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(UserProfile.this, ChangePasswordActivity.class);
            myIntent.putExtra("userId", userId);
            startActivity(myIntent);
        });

        // Handle viewing tarot history
//        tarotHistoryButton.setOnClickListener(v -> {
//            // Navigate to TarotHistoryActivity (you'll need to create this)
//            startActivity(new Intent(UserProfile.this, TarotHistoryActivity.class));
//        });

        // Handle logout action
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.remove("username");
            editor.remove("email");
            editor.remove("userId");
            editor.apply();

            Intent myIntent = new Intent(UserProfile.this, LoginActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
            finish();
            });

        checkPermissions();
        profileImage.setOnClickListener(v -> openImagepicker());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void openImagepicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Set the selected image to the ImageView
                        profileImage.setImageURI(imageUri);

                        // Optionally save the image URI to SharedPreferences
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("profileImageUri", imageUri.toString());
                        editor.apply();
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
