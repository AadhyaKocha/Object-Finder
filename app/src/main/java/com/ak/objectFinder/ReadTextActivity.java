package com.ak.objectFinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class ReadTextActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 1;
    private Uri imgUri;
    public TextView textView;
    private String speechText = "Take image of text to be read";
    private boolean call = false;
    private boolean tryAgain = false;
    TextToSpeechHelper speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        speaker = new TextToSpeechHelper(this, speechText);
        textView = findViewById(R.id.text_show);
        textView.setText("Loading...");

        call =  getIntent().getBooleanExtra("Call", false);

        //imageView = findViewById(R.id.signImg);
        File tempImgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "sign.jpg");
        imgUri = FileProvider.getUriForFile(this, "com.ak.objectFinder", tempImgFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onPause() {
        super.onPause();
        speaker.cancel();
    }


    public void onTryAgainClicked(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(70);
                return false;
            }
        });

        textView.setText("Loading...");
        speechText = "Try Again";
        tryAgain = true;
        speaker.speak(speechText);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onAskForHelpClicked(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(70);
                return false;
            }
        });

        speechText = "Ask for help";
        if (call){
            View b1 = findViewById(R.id.try_again);
            View b2 = findViewById(R.id.help);
            b1.setVisibility(View.GONE);
            b2.setVisibility(View.GONE);
        }
        speaker.speak(speechText);
        FirebaseAPI.getTextFromHelpers(imgUri, textView, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            if (!tryAgain){
                finish();
            }
            return;
        }
        if(requestCode == CAMERA_REQUEST_CODE){

            if (call){
                onAskForHelpClicked(findViewById(R.id.text_show));
            } else {

                VisionAPI.getTextFromImage(imgUri, this, new VisionAPI.TextAnalysisCallback() {
                    @Override
                    public void onSuccess(String resultText) {
                        // show the text
                        TextView text = findViewById(R.id.text_show);
                        text.setText(resultText);
                        speechText = resultText;
                        speaker.speak(speechText);

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("MAD", "error in vision API");
                    }
                });
            }
        }
    }

}
