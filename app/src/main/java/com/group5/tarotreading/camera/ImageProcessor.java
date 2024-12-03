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

public class ImageProcessor {
    /**
     * Converts a CameraX ImageProxy to a Bitmap format that can be:
     * 1. Displayed in Android ImageView
     * 2. Sent to GPT-4 Vision API
     * 3. Processed for image analysis
     *
     * @param image The ImageProxy object from CameraX capture
     * @return Bitmap The converted image in Bitmap format
     */
    public static Bitmap imageProxyToBitmap(@NonNull ImageProxy image) {
        // Get the first plane of the image (YUV format typically)
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];

        // Get the raw buffer containing image data
        ByteBuffer buffer = planeProxy.getBuffer();

        // Create byte array to hold the image data
        byte[] bytes = new byte[buffer.remaining()];

        // Copy buffer data into byte array
        buffer.get(bytes);

        // Convert byte array to Bitmap using Android's BitmapFactory
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

