package com.ak.objectFinder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TextView textView = findViewById(R.id.text);
        textView.setText("Scanning room for: " +  getIntent().getStringExtra(Globals.OBJECT_TYPE));
    }


}
