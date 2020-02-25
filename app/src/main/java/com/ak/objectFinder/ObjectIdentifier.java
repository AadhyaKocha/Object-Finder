package com.ak.objectFinder;

import android.media.Image;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.util.List;

public class ObjectIdentifier implements ImageAnalysis.Analyzer {

    private ObjectAnalysisCallback callback;

    public void setCallback(ObjectAnalysisCallback objectAnalysisCallback) {
        callback = objectAnalysisCallback;
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }

    public void analyze(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null || callback == null) {
            return;
        }
        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        FirebaseVisionObjectDetectorOptions options =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                        .enableClassification()
                        .enableMultipleObjects()
                        .build();
        FirebaseVisionObjectDetector objectDetector =
                FirebaseVision.getInstance().getOnDeviceObjectDetector(options);
        objectDetector.processImage(image)
                .addOnSuccessListener(
                        detectedObjects -> callback.onSuccess(detectedObjects))
                .addOnFailureListener(
                        e -> callback.onError(e));
    }

    public interface ObjectAnalysisCallback {
        void onSuccess(List<FirebaseVisionObject> detectedObjects);

        void onError(Exception e);
    }
}
