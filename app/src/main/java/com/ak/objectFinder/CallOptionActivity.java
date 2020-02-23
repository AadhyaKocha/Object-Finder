package com.ak.objectFinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CallOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calloption);
    }

    public void onFindObjectClick(View view){

        Toast.makeText(this, "Finding object", Toast.LENGTH_SHORT).show();
    }

    public void onScanScreenClick(View view){

        Toast.makeText(this, "Reading text", Toast.LENGTH_SHORT).show();
    }
}
