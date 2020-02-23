package com.ak.objectFinder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowTextActivity extends AppCompatActivity {

    String text ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_text);

        text = getIntent().getStringExtra(Globals.TEXT_KEY);
        TextView textView = findViewById(R.id.text_show);

        textView.setText(text);


    }
}
