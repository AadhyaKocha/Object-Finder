package com.ak.objectFinder;

import android.content.Intent;
import android.os.Bundle;
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

        speechtext = "You have chosen glasses";
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.glasses);
        startActivity(intent);
    }

    public void onFindJacketClick(View view){
        speechtext = "You have chosen jacket";
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.jacket);
        startActivity(intent);
    }

    public void onFindBagClick(View view){
        speechtext = "You have chosen bag";
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ObjectFinder.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.bag);
        startActivity(intent);
    }
}
