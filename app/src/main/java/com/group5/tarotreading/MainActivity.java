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

import com.group5.tarotreading.question.AskFragment;
import com.group5.tarotreading.question.TodayFragment;
import com.group5.tarotreading.user.LoginActivity;
import com.group5.tarotreading.user.UserProfile;

public class MainActivity extends AppCompatActivity {

    // Navigation Fragments
    private Fragment todayFragment;
    private Fragment askFragment;
    private FragmentManager fragmentManager;
    private int currentFragmentIndex = 0;

    // Navbar
    Button cameraButton;
    Button eregister, logout;

    // background
    ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragments, set today's fortune fragment as default
        todayFragment = new TodayFragment();
        askFragment = new AskFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, todayFragment).commit();

        // Navbar
        cameraButton = findViewById(R.id.camera_button);
        eregister = findViewById(R.id.register);
        logout = findViewById(R.id.logout);

        imageView = findViewById(R.id.imageView);
        imageView.setZ(-1);

        // Check login status for different user icon feature
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

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

        // UserIcon button click listener
        // If user is logged in, direct to profile, otherwise direct to login page
        eregister.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent myIntent = new Intent(MainActivity.this, UserProfile.class);
                MainActivity.this.startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        // Logout button click listener: only showup when user is logged in
        logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            logout.setVisibility(View.GONE);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // Direct back to previous fragment by keep track of current Fragment Index
    // If already the first fragment, clicking on this button will not cause any changes
    private void showPreviousFragment() {
        if (currentFragmentIndex == 1) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, todayFragment)
                    .commit();
            currentFragmentIndex -= 1;
        }
    }

    // Similar logic, direct to next fragment by keep track of the fragment index
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
