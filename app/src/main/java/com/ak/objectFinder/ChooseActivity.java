package com.ak.objectFinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(70);
                return false;
            }
        });

        speechtext = "You have chosen glasses";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.glasses);
        startActivity(intent);
    }

    public void onFindJacketClick(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(70);
                return false;
            }
        });

        speechtext = "You have chosen jacket";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.jacket);
        startActivity(intent);
    }

    public void onFindBagClick(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(70);
                return false;
            }
        });

        speechtext = "You have chosen bag";
        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.bag);
        startActivity(intent);
    }
}
