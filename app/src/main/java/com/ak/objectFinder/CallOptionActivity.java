package com.ak.objectFinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CallOptionActivity extends AppCompatActivity {
    private String speechtext = "Do you need help finding an object or reading text?";
    private TextToSpeechHelper speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calloption);
        speaker = new TextToSpeechHelper(this, speechtext);
    }

    @Override
    public void onPause() {
        super.onPause();
        speaker.cancel();
    }


    public void onFindObjectClick(View view){
        Toast.makeText(this, "Finding object", Toast.LENGTH_SHORT).show();
        speechtext = "I hope we can help you scan the room!";
        speaker.speak(speechtext);

        Intent intent = new Intent(this, ObjectFinder.class);
        startActivity(intent);
    }

    public void onScanScreenClick(View view){
        speechtext = "Let's read some words!";
        speaker.speak(speechtext);
        Toast.makeText(this, "Reading text", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ReadTextActivity.class);
        intent.putExtra("Call", true);
        startActivity(intent);
    }

    public void onCallDirectlyClick(View view){
        speechtext = "Video call directly!";
        speaker.speak(speechtext);
        Toast.makeText(this, "Waiting for others to connect", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, VideoAgorio.class);
        startActivity(intent);
    }
}
