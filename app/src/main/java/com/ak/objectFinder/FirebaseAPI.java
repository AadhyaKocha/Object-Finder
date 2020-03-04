package com.ak.objectFinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.InputStream;
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

    public static String getCurrentUID() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static void createUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("notify", false);
        data.put("token", "");
        db.collection("users").document(userId).set(data);
    }

    public static void getRequesterID(String requestID, GetInfoCallback<String> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("helpRequests").document(requestID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                callback.onSuccess(documentSnapshot.getString("user"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onError(e);
            }
        });

    }

    public static void getTextFromHelpers(Uri imgUri, TextView resultTextView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String path = "TextImages/" + new Date() + ".jpg";
        StorageReference imageRef = storageRef.child(path);
        imageRef.putFile(imgUri);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String defaultText = "Waiting for reply...";
        Map<String, Object> data = new HashMap<>();
        data.put("imagePath", path);
        data.put("text", defaultText);
        data.put("user", userId);

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
                    String text = snapshot.getString("text");
                    resultTextView.setText(text);
                }
            }
        });


    }

    public static void sendTextToUser(String requestID, String text) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("helpRequests").document(requestID).update("text", text);
        Log.e("scott", requestID + " " + text);
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
                        FirebaseStorage.getInstance().getReference().child(document.getString("imagePath")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                new DownloadImageTask(imageView).execute(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("scott", "fuck" + exception);
                            }
                        });
                    }
                }
            }
        });
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public interface GetInfoCallback<T> {
        void onSuccess(T info);

        void onError(Exception e);
    }

}


