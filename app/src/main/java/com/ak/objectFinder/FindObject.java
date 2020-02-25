package com.ak.objectFinder;

import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.concurrent.Executor;

public class FindObject extends AppCompatActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    private int REQUEST_CODE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_object);

        textureView = findViewById(R.id.view_finder);

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        CameraX.unbindAll();

        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);
        Executor e = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("");
                imgCap.takePicture(file, e,
                        new ImageCapture.OnImageSavedListener() {
                            @Override
                            public void onImageSaved(File file) {
                                // insert your code here.
                            }

                            @Override
                            public void onError(
                                    ImageCapture.ImageCaptureError imageCaptureError,
                                    String message,
                                    Throwable cause) {
                                // insert your code here.
                            }
                        });
            }
        });

        ImageAnalysisConfig imgAConfig = new ImageAnalysisConfig.Builder().setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE).build();
        ImageAnalysis analysis = new ImageAnalysis(imgAConfig);
        TextAnalyzer a = new TextAnalyzer();
        a.setCallback(new TextAnalyzer.TextAnalysisCallback() {
            @Override
            public void onSuccess(String resultText) {
                Log.e("scott", resultText);
            }

            @Override
            public void onError(Exception e) {
                Log.e("scott", e + "");
            }
        });

        analysis.setAnalyzer(e, a);

        //bind to lifecycle:
        CameraX.bindToLifecycle(this, analysis, imgCap, preview);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
