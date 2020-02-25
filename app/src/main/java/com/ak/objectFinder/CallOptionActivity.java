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
                                tts.speak(speechtext, TextToSpeech.QUEUE_FLUSH, null, null);
                            else
                                tts.speak(speechtext, TextToSpeech.QUEUE_FLUSH, null);
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

    public void onFindObjectClick(View view){
        Toast.makeText(this, "Finding object", Toast.LENGTH_SHORT).show();
        speechtext = "I hope we can help you scan the room!";
        speak();
    }

    public void onScanScreenClick(View view){
        speechtext = "Let's read some words!";
        speak();
        Toast.makeText(this, "Reading text", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HelpSignActivity.class);
        startActivity(intent);
    }
}
