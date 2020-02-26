package com.ak.objectFinder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReadTextActivity extends AppCompatActivity {

    private TextToSpeech tts;
    public static final int CAMERA_REQUEST_CODE = 1;
    private Uri imgUri;
    public TextView textView;
    private String speechText = "Click image of text to be read";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        textView = findViewById(R.id.text_show);
        textView.setText("Loading...");
        checkPermissions(this);

        //imageView = findViewById(R.id.signImg);
        File tempImgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "sign.jpg");
        imgUri = FileProvider.getUriForFile(this, "com.ak.objectFinder", tempImgFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);

        speak();
    }

    public void speak() {
        if (Globals.audioPref) {
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.d("audioError", "This language is not supported");
                        } else {
                            tts.setPitch(0.6f);
                            tts.setSpeechRate(1.0f);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null);
                            else
                                tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void onTryAgainClicked(View view){
        textView.setText("Loading...");
        speechText = "Try Again";
        speak();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onAskForHelpClicked(View view){
        speechText = "Ask for help";
        speak();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("TextImages/" + new Date() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imgUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("imageUploadError", "Reading text image failed to upload");
                //can make into toast for user later
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("success", "onSuccess: Uploaded image Url is " + imgUri.toString());
                        Toast.makeText(getBaseContext(), "Sent to users, getting help soon!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return;
        if(requestCode == CAMERA_REQUEST_CODE){

            VisionAPI.getTextFromImage(imgUri, this, new VisionAPI.TextAnalysisCallback() {
                @Override
                public void onSuccess(String resultText) {
                    // show the text
                    TextView text = findViewById(R.id.text_show);
                    text.setText(resultText);
                }

                @Override
                public void onError(Exception e) {
                    Log.d("MAD", "error in vision API");
                }
            });
        }
    }

    public static void checkPermissions(Activity activity){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }
}
