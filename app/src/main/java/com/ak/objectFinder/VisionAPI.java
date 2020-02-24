package com.ak.objectFinder;

import android.media.Image;

import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

/*
Written by Scott Crawshaw 2/23/20
Includes helper functions that can be called from other activities to get data from images and videos.
Uses Firebase ML Kit
 */

public class VisionAPI {

    private static int degreesToFirebaseRotation(int degrees) {
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

    private static FirebaseVisionImage analyze(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return null;
        }
        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        return FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

    }

    public static void getTextFromImage(ImageProxy imageProxy, int degrees, TextAnalysisCallback callback) {
        FirebaseVisionImage image = analyze(imageProxy, degrees);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> callback.onSuccess(firebaseVisionText.getText()))
                .addOnFailureListener(e -> callback.onError(e));
    }

    public static void getObjectsFromImage(ImageProxy imageProxy, int degrees, ObjectAnalysisCallback callback) {
        FirebaseVisionImage image = analyze(imageProxy, degrees);
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

    public interface TextAnalysisCallback {
        void onSuccess(String resultText);

        void onError(Exception e);
    }

    public interface ObjectAnalysisCallback {
        void onSuccess(List<FirebaseVisionObject> detectedObjects);

        void onError(Exception e);
    }



}
