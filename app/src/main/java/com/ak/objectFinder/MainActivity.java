package com.ak.objectFinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private TextToSpeech tts;
    private String speechtext = "What do you need help with today?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        }

        ToggleButton audioToggle = findViewById(R.id.audioBtn);
        audioToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // The toggle is disabled
                Globals.audioPref = isChecked;
                speechtext = "Audio settings on";
                speak();
            }
        });

        ToggleButton notifyToggle = findViewById(R.id.notifyBtn);
        notifyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // The toggle is disabled
                Globals.notifyPref = isChecked;
                speechtext = "Notifications setting changed";
                speak();
            }
        });
        //Intent intent = new Intent(this, FindObject.class);
        //(intent);
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

    public void onClickedScan(View view) {
        speechtext = "Scan room";
        speak();
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
    }

    public void onClickedCall(View view) {
        speechtext = "Call directly";
        speak();
        Intent intent = new Intent(this, CallOptionActivity.class);
        startActivity(intent);
    }

    public void onClickedRead(View view) {
        speechtext = "Read text";
        speak();
        Intent intent = new Intent(this, ReadTextActivity.class);
        startActivity(intent);

    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
