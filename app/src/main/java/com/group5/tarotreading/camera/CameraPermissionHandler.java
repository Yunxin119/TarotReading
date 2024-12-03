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

public class CameraPermissionHandler {
    private final AppCompatActivity activity;
    private final PermissionCallback callback;

    public CameraPermissionHandler(AppCompatActivity activity, PermissionCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            callback.onPermissionGranted();
        }
    }

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}
