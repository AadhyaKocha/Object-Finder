package com.ak.objectFinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class CallOptionActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private String speechtext = "Do you need help finding an object or reading text?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calloption);
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void onFindObjectClick(View view){
        Toast.makeText(this, "Finding object", Toast.LENGTH_SHORT).show();
        speechtext = "I hope we can help you scan the room!";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
    }

    public void onScanScreenClick(View view){
        speechtext = "Let's read some words!";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Toast.makeText(this, "Reading text", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HelpSignActivity.class);
        startActivity(intent);
    }

    public void onCallDirectlyClick(View view){
        speechtext = "Video call directly!";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, VideoCall.class);
        startActivity(intent);
    }
}
