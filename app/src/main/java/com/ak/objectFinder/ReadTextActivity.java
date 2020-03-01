package com.ak.objectFinder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Locale;

public class ReadTextActivity extends AppCompatActivity {

    private TextToSpeech tts;
    public static final int CAMERA_REQUEST_CODE = 1;
    private Uri imgUri;
    public TextView textView;
    private String speechText = "Take image of text to be read";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        TextToSpeechHelper.speak(getApplicationContext(), speechText);

        textView = findViewById(R.id.text_show);
        textView.setText("Loading...");
        speechText = textView.getText().toString();

        //imageView = findViewById(R.id.signImg);
        File tempImgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "sign.jpg");
        imgUri = FileProvider.getUriForFile(this, "com.ak.objectFinder", tempImgFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);

        TextToSpeechHelper.speak(getApplicationContext(), speechText);
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
        TextToSpeechHelper.speak(getApplicationContext(), speechText);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onAskForHelpClicked(View view){
        speechText = "Ask for help";
        TextToSpeechHelper.speak(getApplicationContext(), speechText);
        FirebaseAPI.getTextFromHelpers(imgUri, textView);
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
                    speechText = resultText;
                    TextToSpeechHelper.speak(getApplicationContext(), speechText);

                }

                @Override
                public void onError(Exception e) {
                    Log.d("MAD", "error in vision API");
                }
            });
        }
    }

}
