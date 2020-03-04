package com.ak.objectFinder;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class VisionAPI {

    public static void getTextFromImage(Uri imgUri, Context c, TextAnalysisCallback callback) {
        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(c, imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> {
                            String text = firebaseVisionText.getText();
                            if (text == "") {
                                text = "No text found";
                            }
                            callback.onSuccess(text);
                        }
                )
                .addOnFailureListener(e -> callback.onError(e));
    }

    public interface TextAnalysisCallback {
        void onSuccess(String resultText);

        void onError(Exception e);
    }
}
