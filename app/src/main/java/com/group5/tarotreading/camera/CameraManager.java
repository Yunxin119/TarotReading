package com.group5.tarotreading.camera;


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


/**
 * Manages camera operations using CameraX API for the TarotReading application.
 * This class handles camera initialization, preview binding, and image capture functionality.
 * It abstracts the complexity of camera operations from the UI layer.
 */
public class CameraManager {
    private final ProcessCameraProvider cameraProvider;
    private final PreviewView previewView;
    private final AppCompatActivity activity;
    private ImageCapture imageCapture;
    private boolean isBackCamera = true;

    /**
     * Constructs a new CameraManager instance.
     *
     * @param activity     The activity context for camera operations
     * @param previewView  The PreviewView where camera preview will be displayed
     */
    public CameraManager(AppCompatActivity activity, PreviewView previewView) {
        this.activity = activity;
        this.previewView = previewView;
        this.cameraProvider = null; // Initialize in setup
    }

    /**
     * Initializes and starts the camera preview.
     * Sets up the camera provider and binds the preview use case.
     */
    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = cameraProviderFuture.get();
                bindCameraPreview(provider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    /**
     * Binds camera preview use cases to the lifecycle owner.
     * Sets up preview and image capture capabilities.
     *
     * @param provider The ProcessCameraProvider instance
     */
    private void bindCameraPreview(ProcessCameraProvider provider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isBackCamera ?
                        CameraSelector.LENS_FACING_BACK :
                        CameraSelector.LENS_FACING_FRONT)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        provider.unbindAll();
        provider.bindToLifecycle(activity, cameraSelector, preview, imageCapture);
    }

    /**
     * Captures an image using CameraX ImageCapture use case.
     * The captured image is processed in memory without saving to storage.
     *
     * @param callback The callback to handle capture success or failure
     */
    public void captureImage(ImageCaptureCallback callback) {
        if (imageCapture != null) {
            imageCapture.takePicture(
                    ContextCompat.getMainExecutor(activity),
                    new ImageCapture.OnImageCapturedCallback() {
                        @Override
                        public void onCaptureSuccess(@NonNull ImageProxy image) {
                            callback.onImageCaptured(image);
                            image.close();  // Release resources immediately
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            callback.onError(exception.getMessage());
                        }
                    });
        }
    }

    /**
     * Interface for handling image capture callbacks.
     * Provides methods for successful capture and error scenarios.
     */
    public interface ImageCaptureCallback {
        /**
         * Called when an image is successfully captured.
         *
         * @param image The captured ImageProxy object
         */
        void onImageCaptured(ImageProxy image);

        /**
         * Called when an error occurs during image capture.
         *
         * @param error The error message
         */
        void onError(String error);
    }
}