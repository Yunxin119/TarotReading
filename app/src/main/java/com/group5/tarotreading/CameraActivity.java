package com.group5.tarotreading;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageView capturedImageView;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Button captureButton, switchCameraButton, analyzeButton;
    private boolean isBackCamera = true;
    private String selectedSpreadType = "lovercross";
    private Bitmap capturedBitmap;
    private VisionAIHelper visionAIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialize VisionAIHelper
        visionAIHelper = new VisionAIHelper(this, BuildConfig.OPENAI_API_KEY);

        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        captureButton = findViewById(R.id.button_capture);
        switchCameraButton = findViewById(R.id.button_switch_camera);
        analyzeButton = findViewById(R.id.button_analyze);

        analyzeButton.setEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            startCamera();
        }

        captureButton.setOnClickListener(v -> takePhoto());
        switchCameraButton.setOnClickListener(v -> switchCamera());
        analyzeButton.setOnClickListener(v -> analyzeImage());
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(getExternalFilesDir(null), "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        // Load the captured image
                        capturedBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                        // Hide preview and show captured image
                        previewView.setVisibility(View.GONE);
                        capturedImageView.setVisibility(View.VISIBLE);
                        capturedImageView.setImageBitmap(capturedBitmap);

                        // Enable analyze button and update UI
                        analyzeButton.setEnabled(true);
                        captureButton.setText("Retake");
                        switchCameraButton.setEnabled(false);

                        captureButton.setOnClickListener(v -> {
                            // Reset UI for new capture
                            previewView.setVisibility(View.VISIBLE);
                            capturedImageView.setVisibility(View.GONE);
                            captureButton.setText("Capture");
                            switchCameraButton.setEnabled(true);
                            analyzeButton.setEnabled(false);
                            captureButton.setOnClickListener(view -> takePhoto());
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        String msg = "Failed to capture photo: " + exception.getMessage();
                        Toast.makeText(CameraActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("CameraXApp", msg);
                    }
                });
    }

    private void analyzeImage() {
        if (capturedBitmap == null) {
            Toast.makeText(this, "No image to analyze", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        Toast.makeText(this, "Analyzing image...", Toast.LENGTH_SHORT).show();
        analyzeButton.setEnabled(false);

        // Use VisionAIHelper to analyze the image
        visionAIHelper.analyzeCardLocations(capturedBitmap, selectedSpreadType,
                new VisionAIHelper.AIResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            showAnalysisResults(response);
                            analyzeButton.setEnabled(true);
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(CameraActivity.this,
                                    "Analysis failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            analyzeButton.setEnabled(true);
                        });
                    }
                });
    }

    // [Previous camera setup methods remain the same]
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void switchCamera() {
        isBackCamera = !isBackCamera;
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isBackCamera ?
                        CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT)
                .build();
        bindCameraPreview(cameraProvider);
    }

    private void showAnalysisResults(String results) {
        new AlertDialog.Builder(this)
                .setTitle("Card Analysis Results")
                .setMessage(results)
                .setPositiveButton("OK", null)
                .show();
    }

    public void setSpreadType(String spreadType) {
        this.selectedSpreadType = spreadType;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}