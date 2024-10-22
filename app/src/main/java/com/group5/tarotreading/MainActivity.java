package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private Fragment todayFragment;
    private Fragment askFragment;
    private FragmentManager fragmentManager;
    private int currentFragmentIndex = 0;
    Button cameraButton;
    Button eregister, logout;
    ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todayFragment = new TodayFragment();
        askFragment = new AskFragment();
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, todayFragment).commit();

        cameraButton = findViewById(R.id.camera_button);
        eregister = findViewById(R.id.register);
        logout = findViewById(R.id.logout);
        imageView = findViewById(R.id.imageView);
        imageView.setZ(-1);

        // Check login status
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            logout.setVisibility(View.VISIBLE);
        } else {
            logout.setVisibility(View.GONE);
        }

        // Set up left and right buttons for fragment switching
        Button leftButton = findViewById(R.id.leftButton);
        Button rightButton = findViewById(R.id.rightButton);

        leftButton.setOnClickListener(v -> showPreviousFragment());
        rightButton.setOnClickListener(v -> showNextFragment());

        // Camera button click listener
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        // Register button click listener
        eregister.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent myIntent = new Intent(MainActivity.this, UserProfile.class);
                MainActivity.this.startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        // Logout button click listener
        logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            logout.setVisibility(View.GONE);
        });

        // Handle window insets for better layout control
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showPreviousFragment() {
        if (currentFragmentIndex == 1) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, todayFragment)
                    .commit();
            currentFragmentIndex -= 1;
        }
    }

    private void showNextFragment() {
        if (currentFragmentIndex == 0) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out)
                    .replace(R.id.fragmentContainer, askFragment)
                    .commit();
            currentFragmentIndex += 1;
        }
    }

}
