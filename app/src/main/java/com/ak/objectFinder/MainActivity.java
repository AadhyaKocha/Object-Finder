package com.ak.objectFinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToggleButton audioToggle = (ToggleButton)findViewById(R.id.audioBtn);
        ToggleButton notifyToggle = (ToggleButton)findViewById(R.id.notifyBtn);
    }

    public void onClickedScan() {}
    public void onClickedCall() {}
    public void onClickedRead() {}
}
