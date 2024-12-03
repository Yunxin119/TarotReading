package com.group5.tarotreading.camera;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.camera.core.ImageProxy;
import java.nio.ByteBuffer;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.group5.tarotreading.BuildConfig;
import com.group5.tarotreading.MainActivity;
import com.group5.tarotreading.R;
import com.group5.tarotreading.result.AnswerPage;
import com.group5.tarotreading.utils.VisionAIHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity
        implements CameraPermissionHandler.PermissionCallback {

    private PreviewView previewView;
    private ImageView capturedImageView;
    private Button captureButton, analyzeButton, backButton;
    private boolean isPreviewMode = false;
    private Bitmap currentImageBitmap;

    private CameraManager cameraManager;
    private CameraPermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initializeViews();
        setupComponents();
        permissionHandler.checkAndRequestPermission();
        setupButtonListeners();
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        captureButton = findViewById(R.id.button_capture);
        analyzeButton = findViewById(R.id.button_analyze);
        backButton = findViewById(R.id.back_button);

        analyzeButton.setVisibility(View.INVISIBLE);
        captureButton.setText("Capture");
    }

    private void setupComponents() {
        cameraManager = new CameraManager(this, previewView);
        permissionHandler = new CameraPermissionHandler(this, this);
    }

    private void setupButtonListeners() {
        captureButton.setOnClickListener(v -> {
            if (isPreviewMode) {
                resetToCamera();
            } else {
                cameraManager.captureImage(new CameraManager.ImageCaptureCallback() {
                    @Override
                    public void onImageCaptured(ImageProxy image) {
                        currentImageBitmap = ImageProcessor.imageProxyToBitmap(image);
                        runOnUiThread(() -> {
                            capturedImageView.setImageBitmap(currentImageBitmap);
                            switchToPreviewMode();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("CameraXApp", "Photo capture failed: " + error);
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CameraActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        analyzeButton.setOnClickListener(v -> {
            if (currentImageBitmap != null) {
                Log.d("Camera","startDetect");
                VisionAIHelper visionAIHelper = new VisionAIHelper(this, BuildConfig.OPENAI_API_KEY);
                visionAIHelper.identifyTarotCard(currentImageBitmap, new VisionAIHelper.AIResponseListener() {
                    @Override
                    public void onSuccess(String cardName) {
                        Log.d("Camera","Recognized");
                        navigateToAnswerPage(cardName);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.d("Camera","Not_recognized");
                        Toast.makeText(CameraActivity.this,
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(CameraActivity.this,
                        "Please capture an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchToPreviewMode() {
        isPreviewMode = true;
        capturedImageView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.GONE);
        captureButton.setText("Retake");
        analyzeButton.setVisibility(View.VISIBLE);
        analyzeButton.setEnabled(true);
    }

    private void resetToCamera() {
        isPreviewMode = false;
        currentImageBitmap = null;
        capturedImageView.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        captureButton.setText("Capture");
        analyzeButton.setVisibility(View.INVISIBLE);
        cameraManager.startCamera();
    }

    private void navigateToAnswerPage(String cardName) {
        Intent intent = new Intent(CameraActivity.this, AnswerPage.class);
        intent.putExtra("spreadType", "OneCard");
        intent.putExtra("question", "Tell me today's fortune, give me suggestion");

        ArrayList<String> cardPicked = new ArrayList<>();
        cardPicked.add(cardName);
        intent.putStringArrayListExtra("cardPicked", cardPicked);

        startActivity(intent);
        finish();
    }

    @Override
    public void onPermissionGranted() {
        cameraManager.startCamera();
    }

    @Override
    public void onPermissionDenied() {
        Toast.makeText(this, "Camera permission denied",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.handlePermissionResult(requestCode, grantResults);
    }
}