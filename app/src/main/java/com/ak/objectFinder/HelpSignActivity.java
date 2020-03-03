package com.ak.objectFinder;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class HelpSignActivity extends AppCompatActivity {

    String sign_text;
    String requestId;
    EditText inputSignText;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sign);
        requestId = getIntent().getExtras().getString("requestId");
        inputSignText = findViewById(R.id.sign_text);
        ImageView imageView = findViewById(R.id.sign);
        FirebaseAPI.setImageViewFromRequest(requestId, imageView, this);
    }

    public void onSendTextClick(View view){
        sign_text = inputSignText.getText().toString();
        FirebaseAPI.sendTextToUser(requestId, sign_text);
        Intent intent = new Intent(this, ThankYouActivity.class);
        startActivity(intent);
        System.exit(0);
    }


    public void onClickSpeechToText(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Your device does not support Speech Input", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputSignText.setText(result.get(0));
                }
                break;
            }
        }
    }

}