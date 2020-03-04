package com.ak.objectFinder;

import android.media.Image;

import androidx.annotation.experimental.UseExperimental;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

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

    @UseExperimental(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    public void analyze(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null || callback == null) {
            return;
        }
        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler();
        labeler.processImage(image)
                .addOnSuccessListener(
                        detectedObjects -> callback.onSuccess(detectedObjects))
                .addOnFailureListener(
                        e -> callback.onError(e));
    }

    public interface ObjectAnalysisCallback {
        void onSuccess(List<FirebaseVisionImageLabel> detectedObjects);

        void onError(Exception e);
    }
}
