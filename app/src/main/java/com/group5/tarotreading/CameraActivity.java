package com.group5.tarotreading;

import android.Manifest;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import com.group5.tarotreading.result.AnswerPage;
import com.group5.tarotreading.utils.VisionAIHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageView capturedImageView;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private Button captureButton, analyzeButton, backButton;
    private boolean isBackCamera = true;
    private boolean isPreviewMode = false;
    private Bitmap currentImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeViews();
        setupInitialButtonStates();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            startCamera();
        }
        setupButtonListeners();
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        captureButton = findViewById(R.id.button_capture);
        analyzeButton = findViewById(R.id.button_analyze);
        backButton = findViewById(R.id.back_button);
    }

    private void setupInitialButtonStates() {
        analyzeButton.setVisibility(View.INVISIBLE);
        captureButton.setText("Capture");

    }

    private void setupButtonListeners() {
        captureButton.setOnClickListener(v -> {
            if (isPreviewMode) {
                resetToCamera();
            } else {
                takePhoto();
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
        currentImageBitmap = imageProxyToBitmap(image);
        runOnUiThread(() -> {
            capturedImageView.setImageBitmap(currentImageBitmap);
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
        startCamera();
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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