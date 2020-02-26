package com.ak.objectFinder;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpSignActivity extends AppCompatActivity {

    String sign_text;
    String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sign);
        requestId = getIntent().getExtras().getString("requestId");
        ImageView imageView = findViewById(R.id.sign);


    }
    public void onSendTextClick(View view){

        EditText editText = findViewById(R.id.sign_text);
        sign_text = editText.getText().toString();

        FirebaseAPI.sendTextToUser(requestId, sign_text);
        finish();
    }
}
