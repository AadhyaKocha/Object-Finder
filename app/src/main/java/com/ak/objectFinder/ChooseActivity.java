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
    private String speechtext = "What do you want to find? Glasses, bag or jacket?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
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

    public void onFindGlassesClick(View view){

        speechtext = "You have chosen glasses";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, FindObject.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.glasses);
        startActivity(intent);
    }

    public void onFindJacketClick(View view){
        speechtext = "You have chosen jacket";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, FindObject.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.jacket);
        startActivity(intent);
    }

    public void onFindBagClick(View view){
        speechtext = "You have chosen bag";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, FindObject.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.bag);
        startActivity(intent);
    }
}
