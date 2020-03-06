package com.ak.objectFinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {
    private String speechtext = "What do you want to find? Glasses, bag or jacket?";
    TextToSpeechHelper speaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        speaker = new TextToSpeechHelper(this, speechtext);
    }

    @Override
    public void onPause() {
        super.onPause();
        speaker.cancel();
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
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.glassesLabels);
        intent.putExtra(Globals.LIMIT, 0.2f);
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
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.jacketLabels);
        intent.putExtra(Globals.LIMIT, 0.3f);
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
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.bagLabels);
        intent.putExtra(Globals.LIMIT, 0.2f);
        startActivity(intent);
    }
}
