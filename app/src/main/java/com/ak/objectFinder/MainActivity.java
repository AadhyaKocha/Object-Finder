package com.ak.objectFinder;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private String speechtext = "What do you need help with today?";
    ToggleButton audioToggle;
    TextToSpeechHelper speaker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("com.ak.objectFinder", Context.MODE_PRIVATE);
        createNotificationChannel();
        speaker = new TextToSpeechHelper(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
            return;
        }

        if (sp.contains(Globals.audio_key)){
            Globals.audioPref = sp.getBoolean(Globals.audio_key, false);
            audioToggle = findViewById(R.id.audioBtn);
            audioToggle.setChecked(true);

        } else {
            Globals.audioPref = false;
        }

        audioToggle = findViewById(R.id.audioBtn);
        audioToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // The toggle is disabled
                Globals.audioPref = isChecked;
                sp.edit().putBoolean(Globals.audio_key, isChecked).commit();
                speechtext = "Audio settings on";
                speaker.speak(speechtext);
            }
        });

        ToggleButton notifyToggle = findViewById(R.id.notifyBtn);
        FirebaseAPI.getNotificationStatus(notifyToggle, new FirebaseAPI.GetInfoCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean info) {
                notifyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Globals.notifyPref = isChecked;
                        FirebaseAPI.setNotificationStatus(isChecked);
                        speechtext = "Notifications setting changed";
                        speaker.speak(speechtext);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
        checkPermissions(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        speaker.cancel();
    }

    public void onClickedScan(View view) {
        speechtext = "Scan room";
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
    }

    public void onClickedCall(View view) {
//        speechtext = "Call directly";
//        TextToSpeechHelper.speak(getApplicationContext(), speechtext);
//        Intent intent = new Intent(this, CallOptionActivity.class);
//        startActivity(intent);

        speechtext = "Video call directly!";
        speaker.speak(speechtext);
        Toast.makeText(this, "Waiting for others to connect", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, VideoAgorio.class);
        startActivity(intent);
    }

    public void onClickedRead(View view) {
        speechtext = "Read text";
        speaker.speak(speechtext);
        Intent intent = new Intent(this, ReadTextActivity.class);
        startActivity(intent);

    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static void checkPermissions(Activity activity){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "eye spy channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("eyespy", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
