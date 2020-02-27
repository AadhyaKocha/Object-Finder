package com.ak.objectFinder;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ChooseActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private String speechtext = "What do you want to find? Glasses or keys?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
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

    public void onFindGlassesClick(View view){

        Toast.makeText(this, "Finding glasses", Toast.LENGTH_SHORT).show();
        speechtext = "You have chosen glasses";
        speak();
        Intent intent = new Intent(this, FindObject.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.glasses);
        startActivity(intent);
    }

    public void onFindKeyClick(View view){

        Toast.makeText(this, "Finding keys", Toast.LENGTH_SHORT).show();
        speechtext = "You have chosen keys";
        speak();
        Intent intent = new Intent(this, FindObject.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.keys);
        startActivity(intent);
    }
}
