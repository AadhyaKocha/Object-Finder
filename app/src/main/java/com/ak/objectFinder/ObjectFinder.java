package com.ak.objectFinder;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Size;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.experimental.UseExperimental;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ObjectFinder extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Vibrator vibe;
    private boolean vibrating = false;
    float limit;
    private Context con;
    private FrameLayout cameraFrame;
    TextToSpeechHelper speaker;
    private String[] goalObjects;
    private boolean gonzoBall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = this;
        setContentView(R.layout.activity_object_finder);
        goalObjects = getIntent().getStringArrayExtra(Globals.OBJECT_TYPE);
        limit = getIntent().getFloatExtra(Globals.LIMIT, 0.4f);
        cameraFrame = findViewById(R.id.cameraFrame);
        speaker = new TextToSpeechHelper(this);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));


    }

    @Override
    public void onPause() {
        super.onPause();
        vibe.cancel();
        speaker.cancel();
        gonzoBall = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        gonzoBall = false;
    }

    @UseExperimental(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .setTargetName("Preview")
                .build();

        preview.setSurfaceProvider(((PreviewView) findViewById(R.id.preview_view)).getPreviewSurfaceProvider());

        CameraSelector cameraSelector =
                new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        long[] pattern = {0, 1000000000};
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Executor executor = Executors.newCachedThreadPool();
        ImageAnalysis imageAnalyzer =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalyzer.setAnalyzer(executor,
                imageProxy -> {
                    Image mediaImage = imageProxy.getImage();
                    FirebaseVisionImage image =
                            FirebaseVisionImage.fromMediaImage(mediaImage, 0);
                    FirebaseVisionOnDeviceImageLabelerOptions options = new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                            .setConfidenceThreshold(limit)
                            .build();
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                            .getOnDeviceImageLabeler(options);
                    labeler.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                    imageProxy.close();
                                    for (FirebaseVisionImageLabel obj : labels) {
                                        for (String goalObject : goalObjects) {
                                            if (obj.getText().equals(goalObject)) {
                                                if (!vibrating && !gonzoBall) {
                                                    vibe.vibrate(VibrationEffect.createWaveform(pattern, 0));
                                                    vibrating = true;
                                                    //speaker.speak("Object detected");
                                                    cameraFrame.setBackgroundColor(getResources().getColor(R.color.yellow_green));
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    if (vibrating) {
                                        vibe.cancel();
                                        vibrating = false;
                                        //speaker.speak("Object lost");
                                        cameraFrame.setBackgroundColor(getResources().getColor(R.color.rubine_red));
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    imageProxy.close();
                                }
                            });

                });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer, preview);
    }

}