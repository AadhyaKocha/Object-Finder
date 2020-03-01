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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sign);
        requestId = getIntent().getExtras().getString("requestId");
        ImageView imageView = findViewById(R.id.sign);
        FirebaseAPI.setImageViewFromRequest(requestId, imageView, this);
    }

    public void onSendTextClick(View view){
        inputSignText = findViewById(R.id.sign_text);
        sign_text = inputSignText.getText().toString();
        FirebaseAPI.sendTextToUser(requestId, sign_text);
//        finish();

        Intent intent = new Intent(this, ThankYouActivity.class);
        startActivity(intent);
    }

    public void onClickSpeechToText(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent, 10);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Your device does not support Speech Input", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        switch (requestCode) {
//            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputSignText.setText(result.get(0));
                }
//                break;
//        }
    }
}
