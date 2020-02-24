package com.ak.objectFinder;

import android.media.Image;

import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

/*
Written by Scott Crawshaw 2/23/20
Includes helper functions that can be called from other activities to get data from images and videos.
Uses Firebase ML Kit
Intended to be referenced from static context
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

    public static void getTextFromImage(ImageProxy imageProxy, int degrees, textAnalysisCallback callback) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }
        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        FirebaseVisionImage image =
                FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> callback.onSuccess(firebaseVisionText.getText()))
                .addOnFailureListener(e -> callback.onError(e));
    }

    public interface textAnalysisCallback {
        void onSuccess(String resultText);

        void onError(Exception e);
    }

}
