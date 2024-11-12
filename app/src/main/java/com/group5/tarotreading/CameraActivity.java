package com.group5.tarotreading;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.camera.core.ImageProxy;
import java.nio.ByteBuffer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
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

    private Button captureButton, switchCameraButton, analyzeButton, backButton;
    private boolean isBackCamera = true; // Track the selected camera
    private boolean isPreviewMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeViews();
        setupInitialButtonStates();




        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            startCamera();
        }
        setupButtonListeners();
    }


    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        captureButton = findViewById(R.id.button_capture);
        switchCameraButton = findViewById(R.id.button_switch_camera);
        analyzeButton = findViewById(R.id.button_analyze);
        backButton = findViewById(R.id.back_button);
    }

    private void setupInitialButtonStates() {
        analyzeButton.setVisibility(View.INVISIBLE);
        captureButton.setText("Capture");
        switchCameraButton.setVisibility(View.VISIBLE);
    }

    private void setupButtonListeners() {
        captureButton.setOnClickListener(v -> {
            if (isPreviewMode) {
                // In preview mode, "Retake" button was clicked
                resetToCamera();
            } else {
                // In camera mode, "Capture" button was clicked
                takePhoto();
            }
        });
        switchCameraButton.setOnClickListener(v -> switchCamera());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CameraActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        analyzeButton.setOnClickListener(v -> {
            // Add your analyze logic here
            Toast.makeText(this, "Analyzing image...", Toast.LENGTH_SHORT).show();
        });
    }
    private void takePhoto() {
        if (imageCapture != null) {
            imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageCapturedCallback() {
                        @Override
                        public void onCaptureSuccess(@NonNull ImageProxy image) {
                            super.onCaptureSuccess(image);
                            displayCapturedImage(image);
                            image.close();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e("CameraXApp", "Photo capture failed: " + exception.getMessage());
                        }
                    });
        }
    }

    private void displayCapturedImage(@NonNull ImageProxy image) {
        Bitmap bitmap = imageProxyToBitmap(image);
        runOnUiThread(() -> {
            capturedImageView.setImageBitmap(bitmap);
            switchToPreviewMode();
        });
    }

    private Bitmap imageProxyToBitmap(@NonNull ImageProxy image) {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void switchToPreviewMode() {
        isPreviewMode = true;
        capturedImageView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.GONE);
        captureButton.setText("Retake");
        switchCameraButton.setVisibility(View.INVISIBLE);
        analyzeButton.setVisibility(View.VISIBLE);
        analyzeButton.setEnabled(true);
    }

    private void resetToCamera() {
        isPreviewMode = false;
        capturedImageView.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        captureButton.setText("Capture");
        switchCameraButton.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.INVISIBLE);
        startCamera(); // Restart camera preview
    }

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
                .requireLensFacing(isBackCamera ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }


    private static @NonNull Preview getBuild() {
        return new Preview.Builder().build();
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