package com.ak.objectFinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
    }

    public void onFindGlassesClick(View view){

        Toast.makeText(this, "Finding glasses", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.glasses);
        startActivity(intent);
    }

    public void onFindKeyClick(View view){

        Toast.makeText(this, "Finding keys", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(Globals.OBJECT_TYPE, Globals.keys);
        startActivity(intent);
    }
}
