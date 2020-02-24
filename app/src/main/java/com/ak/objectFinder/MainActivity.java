package com.ak.objectFinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToggleButton audioToggle = (ToggleButton)findViewById(R.id.audioBtn);
        audioToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Globals.audioPref = true;
                } else {
                    // The toggle is disabled
                    Globals.audioPref = false;
                }
            }
        });

        ToggleButton notifyToggle = (ToggleButton)findViewById(R.id.notifyBtn);
        notifyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Globals.notifyPref = true;
                } else {
                    // The toggle is disabled
                    Globals.notifyPref= false;
                }
            }
        });

    }

    public void onClickedScan(View view) {
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
    }

    public void onClickedCall(View view) {
        Intent intent = new Intent(this, CallOptionActivity.class);
        startActivity(intent);
    }

    public void onClickedRead(View view) {
        Intent intent = new Intent(this, ReadTextActivity.class);
        startActivity(intent);

    }
}
