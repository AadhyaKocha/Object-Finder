package com.ak.objectFinder;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAPI extends FirebaseMessagingService {
    public static void setNotificationStatus(Boolean shouldBeNotified) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).update("notify", shouldBeNotified);

        if (shouldBeNotified) {
            FirebaseMessaging.getInstance().subscribeToTopic("helper");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("helper");
        }
    }

    public static void getNotificationStatus(ToggleButton button) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        button.setChecked(document.getBoolean("notify"));
                    }
                }
            }
        });
    }

    public static void createUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("notify", false);
        data.put("token", "");
        db.collection("users").document(userId).set(data);
    }

    public static void getTextFromHelpers(Uri imgUri, TextView resultTextView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String path = "TextImages/" + new Date() + ".jpg";
        StorageReference imageRef = storageRef.child(path);
        imageRef.putFile(imgUri);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("imagePath", path);
        data.put("text", "Waiting for reply...");

        DocumentReference docRef = db.collection("helpRequests").document();
        docRef.set(data);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    resultTextView.setText(snapshot.getString("text"));
                }
            }
        });

    }

    public static void sendTextToUser(String requestID, String text) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("helpRequests").document(requestID).update("text", text);
    }

    @Override
    public void onNewToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).update("token", token);

    }

    public static void setImageViewFromRequest(String requestId, ImageView imageView, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("helpRequests").document(requestId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Glide.with(context).load(FirebaseStorage.getInstance().getReference().child(document.getString("imagePath"))).into(imageView);
                    }
                }
            }
        });
    }
}
