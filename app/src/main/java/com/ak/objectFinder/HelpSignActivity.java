package com.ak.objectFinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HelpSignActivity extends AppCompatActivity {

    String sign_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sign);
    }
    public void onSendTextClick(View view){

        Toast.makeText(this, "Help received, thank you!", Toast.LENGTH_LONG);

        Intent intent = new Intent(this, ShowTextActivity.class);
        EditText editText = findViewById(R.id.sign_text);
        sign_text = editText.getText().toString();
        intent.putExtra(Globals.TEXT_KEY, sign_text);

        startActivity(intent);
        finish();
    }
}
